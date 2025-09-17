package com.shanthifarms.repository;

import com.shanthifarms.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // fetch deliveries by customer id
    List<Delivery> findByCustomer_Id(Long customerId);

    long countByDeliveryDate(LocalDate deliveryDate);
}
