package com.example.projectgrupo6.controllers.rest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.OrderDTO;
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

import jakarta.servlet.http.HttpServletRequest;

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

    // GET ALL ORDERS
    @GetMapping("/")
    public Page<OrderDTO> getAll(Pageable pageable) {
        return (orderService.findAllPaged(pageable)).map(orderMapper::toDTO);
    }

    // GET ORDER FROM USER
    @GetMapping("/{ordId}/users/{id}")
    public OrderDTO getOrderFromUser(
            @PathVariable long ordId,
            @PathVariable long id,
            HttpServletRequest request) {

        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException(
                    "Access denied: You cannot access another user's order");
        }

        long currentUserId = userService.getCurrentUserId(request);

        if (userService.ownsOrder(currentUserId, ordId)) {
            return orderMapper.toDTO(
                    orderService.findById(ordId).orElseThrow()
            );
        } else {
            throw new NoSuchElementException();
        }
    }

    // GET ITEM FROM ORDER
    @GetMapping("/{ordId}/users/{id}/item/{itemId}")
    public OrderItemBasicDTO getItemFromOrder(
            @PathVariable long ordId,
            @PathVariable long id,
            @PathVariable long itemId,
            HttpServletRequest request) {

        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException(
                    "Access denied: You cannot access another user's order item");
        }

        long currentUserId = userService.getCurrentUserId(request);

        if (userService.ownsOrder(currentUserId, ordId)) {

            if (orderService.findById(ordId).isPresent()) {

                return orderItemMapper.toBasicDTO(
                        orderService.findItemById(ordId, itemId).orElseThrow()
                );

            } else {
                throw new NoSuchElementException();
            }

        } else {
            throw new NoSuchElementException();
        }
    }

    // CREATE ORDER
    @PostMapping("/users/{id}")
    public ResponseEntity<OrderBasicDTO> createOrder(
            @PathVariable long id,
            @RequestBody List<CartItemBasicDTO> itemBasicDTOS,
            HttpServletRequest request) {

        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException(
                    "Access denied: You cannot create an order for another user");
        }

        long currentUserId = userService.getCurrentUserId(request);

        User user = userService.getById(currentUserId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        Order order = orderService.createSecureOrder(user, itemBasicDTOS);

        for (CartItemBasicDTO dtoItem : itemBasicDTOS) {
            cartService.removeItemFromCart(currentUserId, dtoItem.id());
        }

        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(orderMapper.toBasicDTO(order));
    }

    // UPDATE ORDER
    @PutMapping("/{ordId}/users/{id}")
    public OrderBasicDTO updateOrder(
            @PathVariable long ordId,
            @PathVariable long id,
            @RequestBody OrderBasicDTO orderBasicDTO,
            HttpServletRequest request) {

        if (!userService.getById(id).isPresent()) {
            throw new NoSuchElementException();
        }

        Order newOrder = orderMapper.toDomainFromBasic(orderBasicDTO);

        Order updated = orderService.updateOrder(ordId, newOrder);

        return orderMapper.toBasicDTO(updated);
    }

    // DELETE ORDER
    @DeleteMapping("/{ordId}/users/{id}")
    public ResponseEntity<OrderDTO> deleteOrder(
            @PathVariable long ordId,
            @PathVariable long id,
            HttpServletRequest request) {

        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException(
                    "Access denied: You cannot delete another user's order");
        }

        if (userService.getById(id).isPresent()) {

            OrderDTO order = orderMapper.toDTO(
                    orderService.findById(ordId).orElseThrow()
            );

            orderService.deleteById(ordId);

            return ResponseEntity.ok(order);

        } else {
            throw new NoSuchElementException();
        }
    }

    // GET INVOICE PDF
    @GetMapping("/invoice/{fileName:.+}")
public ResponseEntity<Resource> getInvoice(
        @PathVariable String fileName) throws IOException {

    Path invoicePath = Paths.get(
            System.getProperty("user.dir"),
            "uploads",
            "invoices",
            fileName
    );

    if (!Files.exists(invoicePath)) {
        return ResponseEntity.notFound().build();
    }

    Resource resource = new UrlResource(invoicePath.toUri());

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + fileName + "\""
            )
            .body(resource);
}
}