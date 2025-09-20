package com.shanthifarms.controller;

import com.shanthifarms.model.*;
import com.shanthifarms.repository.DeliveryRecordRepository;
import com.shanthifarms.repository.MilkPlanRepository;
import com.shanthifarms.repository.OrderRepository;
import com.shanthifarms.service.PlanService;
import com.shanthifarms.service.WhatsAppService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import com.shanthifarms.repository.AddressRepository;
import com.shanthifarms.repository.CustomerRepository;
import java.nio.file.*;
import java.io.IOException;

@Controller
public class CustomerController {

    private final PlanService planService;
    private final MilkPlanRepository planRepo;
    private final DeliveryRecordRepository recordRepo;
    private final WhatsAppService whatsAppService;
    private final OrderRepository orderRepo;
    private final AddressRepository addressRepo;
    private final CustomerRepository customerRepo;

    @Value("${milk.pricePerLiter}")
    private double pricePerLiter;

    public CustomerController(PlanService planService,
                              MilkPlanRepository planRepo,
                              DeliveryRecordRepository recordRepo,
                              WhatsAppService whatsAppService,
                              OrderRepository orderRepo,
                              AddressRepository addressRepo,
                              CustomerRepository customerRepo) {
        this.planService = planService;
        this.planRepo = planRepo;
        this.recordRepo = recordRepo;
        this.whatsAppService = whatsAppService;
        this.orderRepo = orderRepo;
        this.addressRepo = addressRepo;
        this.customerRepo = customerRepo;
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
    public String newPlanForm(HttpSession session, Model model){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalDate startDate;
        if (now.isBefore(java.time.LocalTime.of(21, 0))) {
            startDate = java.time.LocalDate.now().plusDays(1);
        } else {
            startDate = java.time.LocalDate.now().plusDays(2);
        }
        model.addAttribute("suggestedStartDate", startDate);
        model.addAttribute("pricePerLiter", pricePerLiter);
        // Default values for preview
        model.addAttribute("previewTotal", 0);
        return "plan-form";
    }

    @PostMapping("/plans")
    public String createPlan(HttpSession session,
                             @RequestParam int days,
                             @RequestParam double liters,
                             Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalDate startDate;
        if (now.isBefore(java.time.LocalTime.of(21, 0))) {
            startDate = java.time.LocalDate.now().plusDays(1);
        } else {
            startDate = java.time.LocalDate.now().plusDays(2);
        }
        double totalAmount = days * liters * pricePerLiter;
        model.addAttribute("suggestedStartDate", startDate);
        model.addAttribute("pricePerLiter", pricePerLiter);
        model.addAttribute("totalAmount", totalAmount);
        MilkPlan savedPlan = planService.createPlan(
                cid,
                startDate,
                days,
                liters
        );
        LocalDate deliveryDate = savedPlan.getStartDate();
        OrderEntity existingOrder = orderRepo.findByPlan_IdAndDeliveryDate(savedPlan.getId(), deliveryDate);
        if (existingOrder == null) {
            OrderEntity order = new OrderEntity();
            order.setCustomer(savedPlan.getCustomer());
            order.setLiters(savedPlan.getLitersPerDay());
            order.setDeliveryDate(deliveryDate);
            order.setOrderDate(LocalDate.now());
            order.setStatus("CONFIRMED");
            order.setPaymentStatus("PENDING");
            order.setPlan(savedPlan);
            orderRepo.save(order);
        }
        whatsAppService.sendMessage("+91-00000", "Your plan is created");
        model.addAttribute("message", "Plan created");
        return "redirect:/plans/" + savedPlan.getId() + "/pay";
    }

    @GetMapping("/plans/{id}/pay")
    public String payPlan(@PathVariable Long id, Model model, HttpSession session) {
        MilkPlan plan = planRepo.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));
        LocalDate deliveryDate = plan.getStartDate();
        OrderEntity order = orderRepo.findByPlan_IdAndDeliveryDate(plan.getId(), deliveryDate);
        if (order == null) {
            order = new OrderEntity();
            order.setCustomer(plan.getCustomer());
            order.setLiters(plan.getLitersPerDay());
            order.setDeliveryDate(deliveryDate);
            order.setOrderDate(deliveryDate);
            order.setStatus("CONFIRMED");
            order.setPaymentStatus("PENDING");
            order.setPlan(plan);
            // Set address to default address if available
            Long cid = (Long) session.getAttribute("customerId");
            Address defaultAddress = addressRepo.findByCustomerIdAndIsDefaultTrue(cid);
            if (defaultAddress != null) {
                order.setAddress(defaultAddress);
            }
            orderRepo.save(order);
        }
        // If no address, redirect to address selection page
        if (order.getAddress() == null) {
            return "redirect:/select-address?orderId=" + order.getId();
        }
        double totalAmount = plan.getDays() * plan.getLitersPerDay() * pricePerLiter;
        model.addAttribute("plan", plan);
        model.addAttribute("orderId", order.getId());
        model.addAttribute("pricePerLiter", pricePerLiter);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("address", order.getAddress());
        return "payment";
    }

    @PostMapping("/orders/{id}/paid")
    @ResponseBody
    public String markOrderPaid(@PathVariable Long id) {
        OrderEntity order = orderRepo.findById(id).orElseThrow();
        order.setPaymentStatus("PAID");
        orderRepo.save(order);
        MilkPlan plan = order.getPlan();
        // ✅ Prevent duplicate delivery records
        if (recordRepo.countByMilkPlan_Id(plan.getId()) == 0) {
            LocalDate start = plan.getStartDate();
            for (int i = 0; i < plan.getDays(); i++) {
                DeliveryRecord rec = new DeliveryRecord();
                rec.setDeliveryDate(start.plusDays(i));
                rec.setStatus("PENDING");
                rec.setMilkPlan(plan);
                recordRepo.save(rec);
            }
        }
        whatsAppService.sendMessage("+91-00000", "Payment received for your order!");
        return "OK";
    }


    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) {
            return "redirect:/login";
        }
        // Fetch orders for the logged-in customer
        List<OrderEntity> orders = orderRepo.findByCustomerId(cid);
        // Sort orders by createdAt descending (latest first)
        orders.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        model.addAttribute("orders", orders);
        // Fetch customer for address check
        com.shanthifarms.model.Customer customer = customerRepo.findById(cid).orElse(null);
        model.addAttribute("customer", customer);
        // Fetch addresses for the logged-in customer
        java.util.List<Address> addresses = addressRepo.findByCustomerId(cid);
        model.addAttribute("addresses", addresses);
        return "orders";
    }

    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        Customer customer = customerRepo.findById(cid).orElse(null);
        List<Address> addresses = addressRepo.findByCustomerId(cid);
        List<MilkPlan> plans = planRepo.findByCustomerId(cid);
        model.addAttribute("customer", customer);
        model.addAttribute("addresses", addresses);
        model.addAttribute("plans", plans);
        return "account";
    }

    @GetMapping("/select-address")
    public String selectAddress(@RequestParam Long orderId, HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return "redirect:/orders";
        List<Address> addresses = addressRepo.findByCustomerId(cid);
        model.addAttribute("addresses", addresses);
        model.addAttribute("orderId", orderId);
        return "select-address";
    }

    @PostMapping("/select-address")
    public String setOrderAddress(@RequestParam Long orderId, @RequestParam Long addressId) {
        OrderEntity order = orderRepo.findById(orderId).orElse(null);
        Address address = addressRepo.findById(addressId).orElse(null);
        if (order != null && address != null) {
            order.setAddress(address);
            orderRepo.save(order);
            return "redirect:/plans/" + order.getPlan().getId() + "/pay";
        }
        return "redirect:/orders";
    }

    @GetMapping("/add-address")
    public String showAddAddressForm(@RequestParam(required = false) Long orderId, HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        model.addAttribute("orderId", orderId);
        return "add-address";
    }

    @PostMapping("/add-address")
    public String addAddress(@RequestParam String label,
                            @RequestParam String address,
                            @RequestParam(required = false) Long orderId,
                            HttpSession session) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        Customer customer = customerRepo.findById(cid).orElse(null);
        if (customer == null) return "redirect:/login";
        Address addr = new Address();
        addr.setLabel(label);
        addr.setAddress(address);
        addr.setCustomer(customer);
        addressRepo.save(addr);
        if (orderId != null) {
            return "redirect:/select-address?orderId=" + orderId;
        } else {
            return "redirect:/account";
        }
    }

    @PostMapping("/customer/address/default")
    @ResponseBody
    public String setDefaultAddress(HttpSession session, @RequestParam Long addressId) {
        Long cid = (Long) session.getAttribute("customerId");
        System.out.println("setDefaultAddress called: addressId=" + addressId + ", customerId=" + cid);
        if (cid == null) return "ERROR: Not logged in";
        Address addr = addressRepo.findById(addressId).orElse(null);
        System.out.println("Address found: " + (addr != null ? addr.getId() : null));
        if (addr == null || !addr.getCustomer().getId().equals(cid)) {
            System.out.println("Address not found or does not belong to customer");
            return "ERROR: Address not found";
        }
        // Unset previous default
        List<Address> addresses = addressRepo.findByCustomerId(cid);
        for (Address a : addresses) {
            if (a.isDefault()) {
                a.setDefault(false);
                addressRepo.save(a);
            }
        }
        // Set new default
        addr.setDefault(true);
        addressRepo.save(addr);
        System.out.println("Default address set: " + addr.getId());
        return "OK";
    }

    @DeleteMapping("/customer/address/{id}")
    @ResponseBody
    public String deleteAddress(HttpSession session, @PathVariable Long id) {
        Long cid = (Long) session.getAttribute("customerId");
        System.out.println("deleteAddress called: id=" + id + ", customerId=" + cid);
        if (cid == null) return "ERROR: Not logged in";
        Address addr = addressRepo.findById(id).orElse(null);
        System.out.println("Address found: " + (addr != null ? addr.getId() : null));
        if (addr == null || !addr.getCustomer().getId().equals(cid)) {
            System.out.println("Address not found or does not belong to customer");
            return "ERROR: Address not found";
        }
        addressRepo.delete(addr);
        System.out.println("Address deleted: " + id);
        return "OK";
    }

    @PostMapping("/account/photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file, HttpSession session) throws IOException {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";
        Customer customer = customerRepo.findById(cid).orElse(null);
        if (customer == null) return "redirect:/login";
        if (!file.isEmpty()) {
            String filename = "user-" + cid + "-" + file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/user-photos/" + filename);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            customer.setPhotoPath("/user-photos/" + filename);
            customerRepo.save(customer);
        }
        return "redirect:/account";
    }
}
