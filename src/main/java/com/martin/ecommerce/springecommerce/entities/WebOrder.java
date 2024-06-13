package com.martin.ecommerce.springecommerce.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "web_orders")
public class WebOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WebOrderQuantities> quantities = new ArrayList<>();

    private Integer totalPrice;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "webOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "order_items")
    private List<OrderItems> orderItems = new ArrayList<>();

}
