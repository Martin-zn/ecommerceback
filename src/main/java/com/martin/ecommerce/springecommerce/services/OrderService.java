package com.martin.ecommerce.springecommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.WebOrder;
import com.martin.ecommerce.springecommerce.repositories.WebOrderRepository;

@Service
public class OrderService {

    //Inyeccion

    @Autowired
    private WebOrderRepository webOrderRepository;

    //Metodos

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderRepository.findByUser(user);
    }

    public WebOrder createWebOreder(){

        return null;
    }








}
