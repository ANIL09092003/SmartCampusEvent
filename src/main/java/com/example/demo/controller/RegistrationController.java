package com.example.demo.controller;

import com.example.demo.entity.Event;
import com.example.demo.entity.Registration;
import com.example.demo.entity.Student;
import com.example.demo.repository.EventRepository;
import com.example.demo.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    @Autowired private EventRepository eventRepo;
    @Autowired private RegistrationService regService;

    @GetMapping("/register/{eventId}")
    public String showRegisterForm(@PathVariable int eventId, Model model) {
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) return "redirect:/";
        model.addAttribute("event", event);
        model.addAttribute("student", new Student());
        model.addAttribute("numberOfTickets", 1);
        return "register";
    }

    @PostMapping("/register/{eventId}")
    public String register(@PathVariable int eventId,
                           @Valid @ModelAttribute Student student,
                           BindingResult result,
                           @RequestParam int numberOfTickets,
                           Model model,
                           RedirectAttributes ra) {
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) return "redirect:/";

        if (result.hasErrors()) {
            model.addAttribute("event", event);
            model.addAttribute("numberOfTickets", numberOfTickets);
            return "register";
        }
        try {
            Registration reg = regService.register(eventId, student, numberOfTickets);
            ra.addFlashAttribute("registration", reg);
            return "redirect:/success";
        } catch (RuntimeException ex) {
            model.addAttribute("event", event);
            model.addAttribute("student", student);
            model.addAttribute("numberOfTickets", numberOfTickets);
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.isBlank()) {
            model.addAttribute("registrations", regService.getByStudentEmail(email.trim()));
            model.addAttribute("email", email);
        }
        return "my-registrations";
    }
}
