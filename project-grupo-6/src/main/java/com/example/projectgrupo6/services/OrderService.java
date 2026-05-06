package com.example.projectgrupo6.services;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import com.example.projectgrupo6.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.OrderItem;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.repositories.OrderRepository;


import jakarta.transaction.Transactional;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ValidationService validationService;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Save an order
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    // Get all orders (useful to admin.html)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAllPaged (Pageable pageable){
        return orderRepository.findAll(pageable);
    }

    // Search by id
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<OrderItem> findItemById (Long ordId, Long itemId){
        return orderItemRepository.findByIdAndOrderId(itemId, ordId);
    }

    // Delete an order
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    //Delete an item
    public Order removeItemFromOrder(Long orderId, Long itemId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no encontrada"));

        order.getItems().removeIf(item -> {
            boolean match = item.getId().equals(itemId);
            if (match) {
                item.setOrder(null);
            }
            return match;
        });
        return order;
    }

    public OrderItem updateOrderItem(Long orderId, Long itemId, OrderItem item){
        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        order.updateItem(itemId, item.getQuantity());
        orderRepository.save(order);
        return order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow();
    }

    public List<Order> findAllByUser (User user){
        return orderRepository.findAllByUser(user);
    }

    public void deleteList (List<Order> orders){
        orderRepository.deleteAll(orders);
    }
    public Order updateOrder(long ordId, Order newOrd) {

        Order order = orderRepository.findById(ordId).orElseThrow(NoSuchElementException::new);

        order.setStatus(newOrd.getStatus());

        return orderRepository.save(order);
    }
    @Transactional
    public Order createSecureOrder(User user, List<CartItemBasicDTO> itemBasicDTOS) {
        
        List<CartItem> secureItems = new ArrayList<>();
        double secureTotalAmount = 0.0;

        for (CartItemBasicDTO dtoItem : itemBasicDTOS) {
            // Search for the real product in the database to get its price and validate its existence
            Product realProduct = productService.getById(dtoItem.product().id())
                    .orElseThrow(() -> new NoSuchElementException("El producto con ID " + dtoItem.product().id() + " ya no existe."));

            // Validate the quantity
            int quantity = dtoItem.quantity();
            if (!validationService.isValidQuantity(quantity)) {
                throw new IllegalArgumentException("Cantidad inválida para el producto " + realProduct.getName());
            }

            // Create a secure CartItem with the real product and validated quantity
            CartItem secureCartItem = new CartItem(realProduct, quantity);
            secureItems.add(secureCartItem);

            // Calculate the total amount using the real product price
            secureTotalAmount += realProduct.getPrice() * quantity;
        }

        // Create the order using the secure items and total amount
        return createOrderFromCart(user, secureItems, secureTotalAmount);
    }

    @Transactional
    public Order createOrderFromCart(User user, List<CartItem> cartItems, double totalAmount){
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("COMPLETED");
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                order, 
                cartItem.getProduct(), 
                cartItem.getQuantity(), 
                cartItem.getProduct().getPrice() // Freeze the price
            );
            order.addOrderItem(orderItem); 
        }
        order=orderRepository.save(order);
        try{
            String safeUsername = user.getUsername()
                .replaceAll("[^a-zA-Z0-9]", "_");
            Path uploadPath = Paths.get(System.getProperty("user.dir"),"uploads","invoices");
            Files.createDirectories(uploadPath);
            String fileName = "invoice_order_" + safeUsername + "_" + order.getId() + ".pdf";
            Path pdfPath = uploadPath.resolve(fileName);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath.toFile()));
            document.open();
                   document.add(new Paragraph("CROSSFIRE PURCHASE RECEIPT"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("User: " + user.getUsername()));
            document.add(new Paragraph("Date: " + order.getOrderDate()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("PRODUCTS:"));
            document.add(new Paragraph(" "));
            for (OrderItem item : order.getItems()){
                document.add(new Paragraph(item.getProduct().getName() + " x" + item.getQuantity() + " - $" + item.getPriceAtPurchase()));
            }
            document.add(new Paragraph(" "));
            document.add(new Paragraph("TOTAL: $" + order.getTotalAmount()));
            document.close();
            order.setInvoiceFileName(fileName);
            order.setInvoiceFilePath(pdfPath.toString());
            order=orderRepository.save(order);
        }catch (Exception e){
            throw new RuntimeException("Error generating invoice PDF ", e);
        }
        return order;
    }
        
}