package com.shanthifarms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"milk_plan_id", "deliveryDate"})
)
@Entity
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate deliveryDate;

    private String status;  // PENDING, DELIVERED, SKIPPED

    private LocalDateTime deliveredAt;  // when delivery was actually completed

    @ManyToOne
    @JoinColumn(name = "milk_plan_id")
    private MilkPlan milkPlan;

    // --- getters & setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public MilkPlan getMilkPlan() { return milkPlan; }
    public void setMilkPlan(MilkPlan milkPlan) { this.milkPlan = milkPlan; }
}
