package com.ashapiro.client.controller;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;
import com.ashapiro.client.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping()
    public String login(Model model) {
        model.addAttribute("user", new UserRequestDto());
        return "authorization/login";
    }

    @PostMapping("/signIn")
    public String signIn(@ModelAttribute("user") UserRequestDto userRequestDto, RedirectAttributes redirectAttributes) {
        ResponseDto response = loginService.authenticateUser(userRequestDto);
        System.out.println(response.getToken());
        if (response.getStatus() == 400) {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("token", response.getToken());
            return "redirect:/chat-page";
        }
        return "redirect:/login";
    }
}
