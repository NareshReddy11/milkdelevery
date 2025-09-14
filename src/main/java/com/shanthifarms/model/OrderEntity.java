package com.shanthifarms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
public class OrderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Customer customer;
    private double liters;
    private LocalDate deliveryDate;
    private LocalDate orderDate;
    private String status = "CONFIRMED";
    private Instant createdAt = Instant.now();
    private String paymentStatus = "PENDING";

    public OrderEntity(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Customer getCustomer(){return customer;}
    public void setCustomer(Customer c){this.customer=c;}
    public double getLiters(){return liters;}
    public void setLiters(double l){this.liters=l;}
    public LocalDate getDeliveryDate(){return deliveryDate;}
    public void setDeliveryDate(LocalDate d){this.deliveryDate=d;}
    public LocalDate getOrderDate(){return orderDate;}
    public void setOrderDate(LocalDate orderDate){this.orderDate=orderDate;}
    public String getStatus(){return status;}
    public void setStatus(String s){this.status=s;}
    public Instant getCreatedAt(){return createdAt;}
    public void setCreatedAt(Instant t){this.createdAt=t;}
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
