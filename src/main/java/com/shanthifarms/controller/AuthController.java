package com.shanthifarms.controller;

import com.shanthifarms.service.AuthService;
import com.shanthifarms.repository.CustomerRepository;
import com.shanthifarms.model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;
    private final CustomerRepository customerRepo;

    public AuthController(AuthService authService, CustomerRepository customerRepo) {
        this.authService = authService;
        this.customerRepo = customerRepo;
    }

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    @PostMapping("/auth/send-otp")
    public String sendOtp(@RequestParam String phone,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String address,
                          RedirectAttributes redirectAttributes) {
        String otp = authService.generateOtp(phone);
        redirectAttributes.addAttribute("phone", phone); // adds ?phone=xxxx in URL
        if (name != null) redirectAttributes.addAttribute("name", name);
        if (address != null) redirectAttributes.addAttribute("address", address);
        redirectAttributes.addFlashAttribute("debugOtp", otp); // flash OTP for UI
        return "redirect:/login";
    }

    @PostMapping("/auth/verify-otp")
    public String verify(@RequestParam String phone,
                         @RequestParam String otp,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String address,
                         HttpSession session) {
        boolean ok = authService.verifyOtp(phone, otp);
        if (!ok) return "redirect:/login?error=otp";

        // save customer if not exists, or update name/address if missing
        Customer c = customerRepo.findByPhone(phone)
                .orElseGet(() -> {
                    Customer nc = new Customer(phone);
                    nc.setName(name);
                    nc.setAddress(address);
                    return customerRepo.save(nc);
                });
        boolean updated = false;
        if (c.getName() == null && name != null) {
            c.setName(name);
            updated = true;
        }
        if (c.getAddress() == null && address != null) {
            c.setAddress(address);
            updated = true;
        }
        if (updated) customerRepo.save(c);

        // store in session
        session.setAttribute("customerId", c.getId());
        session.setAttribute("phone", phone);

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
