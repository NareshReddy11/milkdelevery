package com.shanthifarms.repository;

import com.shanthifarms.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomerId(Long customerId);
    List<OrderEntity> findByDeliveryDate(LocalDate date);
}
