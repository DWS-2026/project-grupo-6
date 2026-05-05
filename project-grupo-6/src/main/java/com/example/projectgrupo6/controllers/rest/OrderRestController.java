package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.*;
import com.example.projectgrupo6.dto.CartDTO;
import com.example.projectgrupo6.dto.OrderDTO;
import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.OrderBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.OrderItemBasicDTO;
import com.example.projectgrupo6.dto.mappers.CartItemMapper;
import com.example.projectgrupo6.dto.mappers.CartMapper;
import com.example.projectgrupo6.dto.mappers.OrderItemMapper;
import com.example.projectgrupo6.dto.mappers.OrderMapper;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.OrderService;
import com.example.projectgrupo6.services.UserService;
import org.hibernate.type.ConvertedBasicArrayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RequestMapping("/api/v1/orders")
@RestController
public class OrderRestController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;

    //GET
    //All
    @GetMapping("/")
    public Page<OrderDTO> getAll(Pageable pageable) {
        return (orderService.findAllPaged(pageable)).map(orderMapper::toDTO);
    }

    //Order by User
    @GetMapping("/{ordId}/user/{id}")
    public OrderDTO getOrderFromUser(@PathVariable long ordId, @PathVariable long id, HttpServletRequest request) {
        
        if(!userService.isAuthorized(id, request)){
            throw new IllegalArgumentException("Acceso denegado: No puedes acceder a la orden de otro usuario");
        }

        long currentUserId = userService.getCurrentUserId(request);

        if (userService.ownsOrder(currentUserId, ordId)) {
            return orderMapper.toDTO(orderService.findById(ordId).orElseThrow());
        } else {
            throw new NoSuchElementException();
        }
    }

    //Item from order
    @GetMapping("/{ordId}/user/{id}/item/{itemId}")
    public OrderItemBasicDTO getItemFromOrder(@PathVariable long ordId, @PathVariable long id, @PathVariable long itemId, HttpServletRequest request) {
        
        if(!userService.isAuthorized(id, request)){
            throw new IllegalArgumentException("Acceso denegado: No puedes acceder al item de la orden de otro usuario");
        }

        long currentUserId = userService.getCurrentUserId(request);

        if (userService.ownsOrder(currentUserId, ordId)) {
            if (orderService.findById(ordId).isPresent()) {
                return orderItemMapper.toBasicDTO(orderService.findItemById(ordId, itemId).orElseThrow());
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    //POST
    //Order
    //Add validation (??)
    //Validation is implemented in the orderService ;)
    @PostMapping("/user/{id}")
    public ResponseEntity<OrderBasicDTO> createOrder(@PathVariable long id, @RequestBody List<CartItemBasicDTO> itemBasicDTOS, HttpServletRequest request) {
        
        if(!userService.isAuthorized(id, request)){
            throw new IllegalArgumentException("Acceso denegado: No puedes crear una orden en nombre de otro usuario");
        }

        long currentUserId = userService.getCurrentUserId(request);
        User user = userService.getById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Order order = orderService.createSecureOrder(user, itemBasicDTOS);

        for (CartItemBasicDTO dtoItem : itemBasicDTOS) {
            cartService.removeItemFromCart(currentUserId, dtoItem.id());
        }

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(orderMapper.toBasicDTO(order));
    }

    //PUT
    //Order
    //Add validation (???) -> so the user can't set the price to less (?)
    //JUST ADMIN SHOULD BE ABLE TO UPDATE AN ORDER, SOOOO, NO VALIDATION NEEDED, THE ADMIN CAN DO WHATEVER HE WANTS WITH THE ORDER (???)
    //Validation is implemented on WebSecurityConfig
    @PutMapping("/{ordId}/user/{id}")
    public OrderBasicDTO updateOrder(@PathVariable long ordId, @PathVariable long id, @RequestBody OrderBasicDTO orderBasicDTO, HttpServletRequest request) {
        if (!userService.getById(id).isPresent()) {
            throw new NoSuchElementException();
        }

        Order newOrder = orderMapper.toDomainFromBasic(orderBasicDTO);
        Order updated = orderService.updateOrder(ordId, newOrder);
        return orderMapper.toBasicDTO(updated);
    }

    //DELETE
    //Order
    @DeleteMapping("/{ordId}/user/{id}")
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable long ordId, @PathVariable long id, HttpServletRequest request) {
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Acceso denegado: No puedes borrar el pedido de otro usuario");
        }

        if (userService.getById(id).isPresent()) {
            OrderDTO order = orderMapper.toDTO(orderService.findById(ordId).orElseThrow());
            orderService.deleteById(ordId);
            return ResponseEntity.ok(order);
        } else {
            throw new NoSuchElementException();
        }
    }

    //ORDER FILE
    //Create
    @PostMapping("/{ordId}/file")
    public ResponseEntity<Void> uploadFile(@PathVariable long ordId,
                                           @RequestParam MultipartFile file,
                                           HttpServletRequest request) throws IOException {

        
                                            
        long currentUserId = userService.getCurrentUserId(request);
        if (!userService.isAuthorized(currentUserId, request)) {
            throw new IllegalArgumentException("Acceso denegado: No puedes crear el pedido de otro usuario");
        }

        orderService.addFileToOrder(ordId, file);
        return ResponseEntity.ok().build();
    }

    //Get
    @GetMapping("/{ordId}/file")
    public ResponseEntity<Resource> getFile(@PathVariable long ordId, HttpServletRequest request) throws IOException {
        Order order = orderService.findById(ordId)
                .orElseThrow();

        long ownerId = order.getUser().getId();
        if (!userService.isAuthorized(ownerId, request)) {
            throw new IllegalArgumentException("Acceso denegado: No puedes descargar la factura de otro usuario");
        }

        if (order.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(order.getFilePath());
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + order.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .body(resource);
    }

}
