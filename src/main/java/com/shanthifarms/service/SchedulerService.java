package com.shanthifarms.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class SchedulerService {
    private final DeliveryService deliveryService;
    private final com.shanthifarms.repository.CustomerRepository customerRepo;
    private final WhatsAppService whatsAppService;

    public SchedulerService(DeliveryService deliveryService, com.shanthifarms.repository.CustomerRepository customerRepo, WhatsAppService whatsAppService){
        this.deliveryService = deliveryService;
        this.customerRepo = customerRepo;
        this.whatsAppService = whatsAppService;
    }

    @Scheduled(cron = "0 5 21 * * *")
    public void nightlyGenerate(){
        LocalDate next = LocalDate.now().plusDays(1);
        deliveryService.generateDeliveryRecordsForDate(next);
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void sendCutoffReminders() {
        for (com.shanthifarms.model.Customer c : customerRepo.findAll()) {
            whatsAppService.sendMessage(c.getPhone(), "Reminder: You can pause/skip tomorrow's milk before 9 PM.");
        }
    }
}
