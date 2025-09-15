package com.shanthifarms.repository;

import com.shanthifarms.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    long countByDeliveryDate(LocalDate date);
}
