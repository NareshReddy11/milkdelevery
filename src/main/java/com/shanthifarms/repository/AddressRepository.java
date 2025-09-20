package com.shanthifarms.repository;

import com.shanthifarms.model.Address;
import com.shanthifarms.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer(Customer customer);
    List<Address> findByCustomerId(Long customerId);
    Address findByCustomerIdAndIsDefaultTrue(Long customerId);
}

