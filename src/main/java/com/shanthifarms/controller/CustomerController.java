package com.shanthifarms.controller;

import com.shanthifarms.model.MilkPlan;
import com.shanthifarms.model.DeliveryRecord;
import com.shanthifarms.repository.DeliveryRecordRepository;
import com.shanthifarms.repository.MilkPlanRepository;
import com.shanthifarms.service.PlanService;
import com.shanthifarms.service.DeliveryService;
import com.shanthifarms.service.WhatsAppService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CustomerController {
    private final PlanService planService;
    private final MilkPlanRepository planRepo;
    private final DeliveryRecordRepository recordRepo;
    private final DeliveryService deliveryService;
    private final WhatsAppService whatsAppService;

    public CustomerController(PlanService planService, MilkPlanRepository planRepo, DeliveryRecordRepository recordRepo, DeliveryService deliveryService, WhatsAppService whatsAppService){
        this.planService = planService;
        this.planRepo = planRepo;
        this.recordRepo = recordRepo;
        this.deliveryService = deliveryService;
        this.whatsAppService = whatsAppService;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";
        List<MilkPlan> plans = planService.getPlansForCustomer(cid);
        model.addAttribute("plans", plans);
        return "home";
    }

    @GetMapping("/plans/new")
    public String newPlanForm(HttpSession session){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";
        return "plan-form";
    }

    @PostMapping("/plans")
    public String createPlan(HttpSession session, @RequestParam String startDate, @RequestParam int days, @RequestParam double liters, @RequestParam String subscriptionType, Model model){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";
        planService.createPlan("unknown-phone-"+cid, LocalDate.parse(startDate), days, liters, subscriptionType);
        whatsAppService.sendMessage("+91-00000","Your plan is created");
        model.addAttribute("message","Plan created");
        return "redirect:/home";
    }

    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";

        List<DeliveryRecord> recs = recordRepo.findByMilkPlanCustomerIdAndDeliveryDateGreaterThanEqual(cid, LocalDate.now());
        com.shanthifarms.repository.OrderRepository orderRepo = (com.shanthifarms.repository.OrderRepository) org.springframework.beans.factory.BeanFactoryUtils.beanOfTypeIncludingAncestors(org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(session.getServletContext()), com.shanthifarms.repository.OrderRepository.class);
        List<com.shanthifarms.model.OrderEntity> orders = orderRepo.findByCustomerId(cid);
        // Fetch active plans for the customer
        List<com.shanthifarms.model.MilkPlan> activePlans = planRepo.findByCustomerId(cid).stream()
            .filter(p -> "ACTIVE".equals(p.getStatus()))
            .toList();
        model.addAttribute("records", recs);
        model.addAttribute("orders", orders);
        model.addAttribute("activePlans", activePlans);
        return "orders";
    }

    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        com.shanthifarms.model.Customer customer = planRepo.findByCustomerId(cid).stream().findFirst().map(com.shanthifarms.model.MilkPlan::getCustomer).orElse(null);
        com.shanthifarms.model.MilkPlan plan = planRepo.findByCustomerId(cid).stream().findFirst().orElse(null);
        model.addAttribute("customer", customer);
        model.addAttribute("plan", plan);
        return "account";
    }

    @PostMapping("/orders/place")
    public String placeOrder(HttpSession session,
                        @RequestParam Long planId,
                        @RequestParam String deliveryDate,
                        @RequestParam double liters,
                        Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        // Fetch plan
        com.shanthifarms.model.MilkPlan plan = planRepo.findById(planId).orElse(null);
        if (plan == null || !"ACTIVE".equals(plan.getStatus()) || !plan.getCustomer().getId().equals(cid)) {
            model.addAttribute("error", "Invalid plan selected.");
            return "redirect:/orders";
        }
        LocalDate deliveryDt = LocalDate.parse(deliveryDate);
        LocalDate start = plan.getStartDate();
        LocalDate end = start.plusDays(plan.getDays() - 1);
        if (deliveryDt.isBefore(start) || deliveryDt.isAfter(end)) {
            model.addAttribute("error", "Delivery date is outside plan range.");
            return "redirect:/orders";
        }
        // Create order
        com.shanthifarms.model.OrderEntity order = new com.shanthifarms.model.OrderEntity();
        order.setCustomer(plan.getCustomer());
        order.setLiters(liters);
        order.setDeliveryDate(deliveryDt);
        order.setOrderDate(LocalDate.now());
        order.setStatus("CONFIRMED");
        order.setPaymentStatus("PENDING");
        com.shanthifarms.repository.OrderRepository orderRepo = (com.shanthifarms.repository.OrderRepository) org.springframework.beans.factory.BeanFactoryUtils.beanOfTypeIncludingAncestors(org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(session.getServletContext()), com.shanthifarms.repository.OrderRepository.class);
        orderRepo.save(order);
        return "redirect:/orders";
    }
}
