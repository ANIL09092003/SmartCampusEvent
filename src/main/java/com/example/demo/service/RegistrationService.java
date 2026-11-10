package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired private RegistrationRepository regRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private EmailService emailService;

    @Transactional
    public Registration register(int eventId, Student student, int numberOfTickets) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (numberOfTickets <= 0) {
            throw new RuntimeException("Number of tickets must be positive");
        }
        if (event.getAvailableTickets() < numberOfTickets) {
            throw new RuntimeException("Only " + event.getAvailableTickets() + " tickets available");
        }

        // Reuse student by email if exists
        Student savedStudent = studentRepo.findByEmail(student.getEmail())
                .orElseGet(() -> studentRepo.save(student));

        // Update fields if student already existed
        if (savedStudent.getId() != 0) {
            savedStudent.setName(student.getName());
            if (student.getDepartment() != null) savedStudent.setDepartment(student.getDepartment());
            if (student.getPhone() != null) savedStudent.setPhone(student.getPhone());
            studentRepo.save(savedStudent);
        }

        event.setAvailableTickets(event.getAvailableTickets() - numberOfTickets);
        eventRepo.save(event);

        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setStudent(savedStudent);
        reg.setNumberOfTickets(numberOfTickets);
        reg.setTotalAmount(numberOfTickets * event.getTicketPrice());
        reg = regRepo.save(reg);

        emailService.sendRegistrationEmail(
                savedStudent.getEmail(),
                savedStudent.getName(),
                event.getName(),
                numberOfTickets,
                reg.getTotalAmount()
        );
        return reg;
    }

    public List<Registration> getByStudentEmail(String email) {
        return regRepo.findByStudentEmail(email);
    }

    public List<Registration> getByEventId(int eventId) {
        return regRepo.findByEventId(eventId);
    }

    public long countByEvent(int eventId) {
        return regRepo.countByEventId(eventId);
    }
}
