package com.shanthifarms.repository;

import com.shanthifarms.model.DeliveryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryRecordRepository extends JpaRepository<DeliveryRecord, Long> {

    List<DeliveryRecord> findByMilkPlan_Customer_IdOrderByDeliveryDateDesc(Long customerId);

    List<DeliveryRecord> findByMilkPlan_Id(Long planId);

    List<DeliveryRecord> findByDeliveryDate(LocalDate deliveryDate);

    boolean existsByMilkPlan_IdAndDeliveryDate(Long planId, LocalDate deliveryDate);

    List<DeliveryRecord> findByMilkPlan_Customer_Id(Long customerId);

    // ✅ New method to check if deliveries already exist
    long countByMilkPlan_Id(Long planId);

    List<DeliveryRecord> findByMilkPlan_Customer_IdAndDeliveryDate(Long customerId, LocalDate deliveryDate);
}
