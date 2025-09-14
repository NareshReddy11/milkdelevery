package com.shanthifarms.service;

import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {
    public void sendMessage(String phone, String message){
        System.out.println("[WhatsApp] to="+phone+" msg="+message);
    }
}
