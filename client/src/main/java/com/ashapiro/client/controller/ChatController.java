package com.ashapiro.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat-page")
public class ChatController {

    @GetMapping()
    public String showLoginForm(@ModelAttribute("token") String token, Model model) {
        model.addAttribute("token", token);
        return "chat/chat-page";
    }
}
