package com.shanthifarms.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String,String> otps = new ConcurrentHashMap<>();
    public String generateOtp(String phone){
        String otp = "1234";
        otps.put(phone, otp);
        return otp;
    }
    public boolean verifyOtp(String phone, String otp){
        String v = otps.get(phone);
        if(v!=null && v.equals(otp)){ otps.remove(phone); return true; }
        return false;
    }
}
