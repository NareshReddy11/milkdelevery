package com.shanthifarms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
public class DeliveryRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private MilkPlan milkPlan;
    private LocalDate deliveryDate;
    private String status = "PENDING";
    private Instant deliveredAt;

    public DeliveryRecord(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public MilkPlan getMilkPlan(){return milkPlan;}
    public void setMilkPlan(MilkPlan m){this.milkPlan=m;}
    public LocalDate getDeliveryDate(){return deliveryDate;}
    public void setDeliveryDate(LocalDate d){this.deliveryDate=d;}
    public String getStatus(){return status;}
    public void setStatus(String s){this.status=s;}
    public Instant getDeliveredAt(){return deliveredAt;}
    public void setDeliveredAt(Instant t){this.deliveredAt=t;}
}
