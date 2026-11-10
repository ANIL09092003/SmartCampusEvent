package com.example.demo.controller;

import com.example.demo.entity.Event;
import com.example.demo.service.EventService;
import com.example.demo.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
public class EventController {

    @Autowired private EventService eventService;
    @Autowired private RegistrationService registrationService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String department,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model) {
        model.addAttribute("events", eventService.search(department, type, date));
        model.addAttribute("department", department);
        model.addAttribute("type", type);
        model.addAttribute("date", date);
        return "index";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable int id, Model model) {
        Event event = eventService.getEventById(id);
        if (event == null) return "redirect:/";
        model.addAttribute("event", event);
        model.addAttribute("regCount", registrationService.countByEvent(id));
        return "event-detail";
    }

    // ===== ADMIN =====
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/dashboard";
    }

    @GetMapping("/add")
    public String addEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/add-event";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Event event, BindingResult result,
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            return event.getId() == 0 ? "admin/add-event" : "admin/edit-event";
        }
        eventService.saveEvent(event);
        ra.addFlashAttribute("successMessage", "Event saved successfully!");
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Event event = eventService.getEventById(id);
        if (event == null) return "redirect:/admin";
        model.addAttribute("event", event);
        return "admin/edit-event";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        eventService.deleteEvent(id);
        ra.addFlashAttribute("successMessage", "Event deleted.");
        return "redirect:/admin";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("byDept", eventService.getRegistrationStatsByDepartment());
        model.addAttribute("byEvent", eventService.getRegistrationStatsByEvent());
        return "admin/stats";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
