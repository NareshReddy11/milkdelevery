package com.shanthifarms.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String phone;
    private String name;
    private Instant createdAt = Instant.now();
    private String address;
    private String otp;

    @Transient
    private java.util.List<com.shanthifarms.model.MilkPlan> plans;

    public Customer(){}
    public Customer(String phone){ this.phone = phone; this.createdAt = Instant.now();}

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone=phone;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public Instant getCreatedAt(){return createdAt;}
    public void setCreatedAt(Instant t){this.createdAt=t;}
    public String getAddress(){return address;}
    public void setAddress(String address){this.address=address;}
    public String getOtp(){return otp;}
    public void setOtp(String otp){this.otp=otp;}
    public java.util.List<com.shanthifarms.model.MilkPlan> getPlans() { return plans; }
    public void setPlans(java.util.List<com.shanthifarms.model.MilkPlan> plans) { this.plans = plans; }
}
