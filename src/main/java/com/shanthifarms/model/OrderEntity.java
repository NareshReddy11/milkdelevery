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

    // ✅ NEW: Link each order to the MilkPlan
    @ManyToOne
    @JoinColumn(name = "plan_id")   // this will create a foreign key in DB
    private MilkPlan plan;

    private double liters;
    private LocalDate deliveryDate;
    private LocalDate orderDate;
    private String status = "CONFIRMED";
    private Instant createdAt = Instant.now();
    private String paymentStatus = "PENDING";

    // NEW: Link each order to a delivery Address
    @ManyToOne
    private Address address;

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
    public MilkPlan getPlan() { return plan; }
    public void setPlan(MilkPlan plan) { this.plan = plan; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
