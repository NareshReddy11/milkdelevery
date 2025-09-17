package com.shanthifarms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
public class MilkPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")  // foreign key
    private Customer customer;   // ✅ should be Customer entity, not Long

    private LocalDate startDate;
    private int days;
    private double litersPerDay;
    private String status = "ACTIVE";
    private Instant createdAt = Instant.now();
    private String subscriptionType = "DAILY";

    public MilkPlan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate d) { this.startDate = d; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public double getLitersPerDay() { return litersPerDay; }
    public void setLitersPerDay(double l) { this.litersPerDay = l; }

    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant t) { this.createdAt = t; }

    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
}
