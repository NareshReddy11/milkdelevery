package com.shanthifarms.model;

import jakarta.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String address;
    private String label; // e.g. Home, Work
    private Double latitude;
    private Double longitude;
    private boolean isDefault;

    public Address() {}

    public Address(Customer customer, String address, String label, Double latitude, Double longitude, boolean isDefault) {
        this.customer = customer;
        this.address = address;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}

