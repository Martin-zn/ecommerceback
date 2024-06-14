package com.martin.ecommerce.springecommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.martin.ecommerce.springecommerce.entities.*;
import com.martin.ecommerce.springecommerce.exceptions.OrderException;
import com.martin.ecommerce.springecommerce.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.repositories.WebOrderRepository;

@Service
public class OrderService {

    //Inyeccion

    @Autowired
    private WebOrderRepository webOrderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    //Metodos

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderRepository.findByUser(user);
    }

    public WebOrder createWebOrder(LocalUser user){

        Cart cart = cartService.findUserCart(user.getId());
        List<OrderItems> orderItems = new ArrayList<>();

        for (CartItem item : cart.getCartItems()){
            OrderItems orderItem = new OrderItems();

            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProduct(item.getProduct());
            orderItem.setUserId(item.getUserId());

            OrderItems createdOrderItem = orderItemRepository.save(orderItem);

            orderItems.add(createdOrderItem);
        }

        WebOrder createdOrder = new WebOrder();
        createdOrder.setUser(user);
        createdOrder.setOrderItems(orderItems);
        createdOrder.setTotalPrice((int) cart.getTotalPrice());
        createdOrder.setTotalItems((int) cart.getTotalItem());

        WebOrder savedOrder = webOrderRepository.save(createdOrder);

        for (OrderItems item : orderItems){
            item.setWebOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;

    }

    public WebOrder findOrderById(Long orderId)throws OrderException {
        Optional<WebOrder> opt = webOrderRepository.findById(orderId);
        if (opt.isPresent()){
            return opt.get();
        }
        throw new OrderException("La orden con el id " + orderId + ", no existe");
    }

    public List<WebOrder> userOrderHistory(Long userId){
        LocalUser user = userService.findUserById(userId);
        List<WebOrder> order = webOrderRepository.findByUser(user);

        return order;
    }

    public List<WebOrder> getAllOrders(){
        return webOrderRepository.findAll();
    }

    public void deleteOrder(Long orderId)throws OrderException{
        Optional<WebOrder> order = webOrderRepository.findById(orderId);

        if(order.isPresent()){
            webOrderRepository.delete(order.get());
        }
        throw new OrderException("La orden con id " +orderId + " ,no exsiste");

    }










}
