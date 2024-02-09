package com.ashapiro.client.controller;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;
import com.ashapiro.client.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new UserRequestDto());
        return "authorization/register";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("user") UserRequestDto userRequestDto, RedirectAttributes redirectAttributes) {
        ResponseDto response = registerService.save(userRequestDto);
        System.out.println(response.getMessage());
        if (response.getStatus() == 400) {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
        } else {
            return "redirect:/login";
        }
        return "redirect:/register/new";
    }
}
