package com.shanthifarms.controller;

import com.shanthifarms.model.DeliveryRecord;
import com.shanthifarms.repository.DeliveryRecordRepository;
import com.shanthifarms.service.DeliveryService;
import com.shanthifarms.service.ReportService;
import com.shanthifarms.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final DeliveryService deliveryService;
    private final DeliveryRecordRepository recordRepo;
    private final ReportService reportService;
    private final CustomerRepository customerRepo;

    public AdminController(DeliveryService deliveryService, DeliveryRecordRepository recordRepo, ReportService reportService, CustomerRepository customerRepo){
        this.deliveryService = deliveryService;
        this.recordRepo = recordRepo;
        this.reportService = reportService;
        this.customerRepo = customerRepo;
    }

    @GetMapping({"", "/"})
    public String adminHome(){ return "admin"; }

    @GetMapping("/deliveries")
    public String deliveries(@RequestParam(required=false) String date, Model model){
        LocalDate d = date==null? LocalDate.now() : LocalDate.parse(date);
        model.addAttribute("date", d.toString());
        model.addAttribute("records", recordRepo.findByDeliveryDate(d));
        return "admin-deliveries";
    }

    @PostMapping("/deliveries/{id}/deliver")
    public String markDelivered(@PathVariable Long id){
        deliveryService.markDelivered(id);
        return "redirect:/admin/deliveries";
    }

    @GetMapping("/report")
    public void report(@RequestParam(required=false) String date, HttpServletResponse resp) throws Exception{
        LocalDate d = date==null? LocalDate.now() : LocalDate.parse(date);
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition","attachment; filename=report-"+d+".csv");
        reportService.writeCsvForDate(d, resp.getOutputStream());
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerRepo.findAll());
        return "admin";
    }

    @GetMapping("/customers/{id}/orders")
    public String customerOrders(@PathVariable Long id, Model model) {
        model.addAttribute("records", recordRepo.findByMilkPlanCustomerId(id));
        return "admin-customer-orders";
    }
}
