package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.Cart;
import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.OrderItem;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.CartDTO;
import com.example.projectgrupo6.dto.OrderDTO;
import com.example.projectgrupo6.dto.basicDtos.OrderBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.OrderItemBasicDTO;
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

import java.net.URI;
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

    //GET
    //All
    @GetMapping("/")
    public Page<OrderDTO> getAll(Pageable pageable){
        return (orderService.findAllPaged(pageable)).map(orderMapper::toDTO);
    }

    //Order by User
    @GetMapping("/{ordId}/user/{id}")
    public OrderDTO getOrderFromUser(@PathVariable long ordId, @PathVariable long id){
        if(userService.getById(id).isPresent()){
            return orderMapper.toDTO(orderService.findById(ordId).orElseThrow());
        } else {
            throw new NoSuchElementException();
        }
    }

    //Item from order
    @GetMapping("/{ordId}/user/{id}/item/{itemId}")
    public OrderItemBasicDTO getItemFromOrder(@PathVariable long ordId, @PathVariable long id, @PathVariable long itemId){
        if(userService.getById(id).isPresent() && orderService.findById(ordId).isPresent()){
            return orderItemMapper.toBasicDTO(orderService.findItemById(ordId, itemId).orElseThrow());
        } else {
            throw new NoSuchElementException();
        }
    }

    //POST
    //Order
    @PostMapping("/user/{id}")
    public ResponseEntity<CartDTO> newOrder(@PathVariable long id, @RequestBody CartDTO cartDTO){
        if(userService.getById(id).isPresent()){
            User user = userService.getById(id).get();
            Cart cart = cartMapper.toDomain(cartDTO);

            orderService.createOrderFromCart(user, cart.getItems(), cartService.getCartTotal(id));

            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(cart.getId()).toUri();;
            return ResponseEntity.created(location).body(cartMapper.toDTO(cart));
        } else {
            throw new NoSuchElementException();
        }
    }

    //PUT
    //Order
    @PutMapping("/{ordId}/user/{id}")
    public OrderBasicDTO updateOrder(@PathVariable long ordId, @PathVariable long id, @RequestBody OrderBasicDTO orderBasicDTO){
        if (!userService.getById(id).isPresent()) {
            throw new NoSuchElementException();
        }

        Order newOrder = orderMapper.toDomainFromBasic(orderBasicDTO);
        Order updated = orderService.updateOrder(ordId, newOrder);
        return orderMapper.toBasicDTO(updated);
    }

    //Item from order
    //change
    @PutMapping("/{ordId}/user/{id}/item/{itemId}")
    public OrderItemBasicDTO updateOrderItem(@PathVariable long ordId, @PathVariable long id,@PathVariable long itemId, @RequestBody OrderItemBasicDTO orderItemBasicDTO){
        if(userService.getById(id).isPresent()) {
            OrderItem item = orderItemMapper.toDomainFromBasic(orderItemBasicDTO);
            OrderItem updated = orderService.updateOrderItem(ordId, itemId, item);
            return orderItemMapper.toBasicDTO(updated);
        } else {
            throw new NoSuchElementException();
        }
    }

    //DELETE
    //Order
    @DeleteMapping("/{ordId}/user/{id}")
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable long ordId, @PathVariable long id){
        if(userService.getById(id).isPresent()){
            OrderDTO order = orderMapper.toDTO(orderService.findById(ordId).orElseThrow());
            orderService.deleteById(ordId);
            return ResponseEntity.ok(order);
        } else {
            throw new NoSuchElementException();
        }
    }

    //Item from Order
    @DeleteMapping("/{ordId}/user/{id}/item/{itemId}")
    public ResponseEntity<OrderItemBasicDTO> removeFromOrder(@PathVariable long ordId, @PathVariable long id, @PathVariable long itemId){
        if(userService.getById(id).isPresent() && orderService.findById(ordId).isPresent()){
            OrderItemBasicDTO item = orderItemMapper.toBasicDTO(orderService.findItemById(ordId, itemId).orElseThrow());
            orderService.removeItemFromOrder(ordId, itemId);
            return ResponseEntity.ok(item);
        } else {
            throw new NoSuchElementException();
        }
    }

}
