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
    private String photoPath;

    @Transient
    private java.util.List<com.shanthifarms.model.MilkPlan> plans;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Address> addresses = new java.util.ArrayList<>();

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
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public java.util.List<com.shanthifarms.model.MilkPlan> getPlans() { return plans; }
    public void setPlans(java.util.List<com.shanthifarms.model.MilkPlan> plans) { this.plans = plans; }
    public java.util.List<Address> getAddresses() { return addresses; }
    public void setAddresses(java.util.List<Address> addresses) { this.addresses = addresses; }
}
