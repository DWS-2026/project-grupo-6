package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.CartItem;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

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

    // GET ALL ORDERS
    @GetMapping("/")
    public Page<OrderDTO> getAll(Pageable pageable) {
        return (orderService.findAllPaged(pageable)).map(orderMapper::toDTO);
    }

    // GET ORDER FROM USER
    @GetMapping("/{ordId}/user/{id}")
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
    @GetMapping("/{ordId}/user/{id}/item/{itemId}")
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
    @PostMapping("/user/{id}")
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
    @PutMapping("/{ordId}/user/{id}")
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
    @DeleteMapping("/{ordId}/user/{id}")
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