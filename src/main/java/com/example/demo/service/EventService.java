package com.example.demo.service;

import com.example.demo.entity.Event;
import com.example.demo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository repo;

    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    public Event saveEvent(Event e) {
        if (e.getId() == 0) {
            e.setAvailableTickets(e.getTotalTickets());
        }
        return repo.save(e);
    }

    public Event getEventById(int id) {
        return repo.findById(id).orElse(null);
    }

    public void deleteEvent(int id) {
        repo.deleteById(id);
    }

    public List<Event> search(String department, String type, LocalDate date) {
        if ((department == null || department.isBlank()) &&
            (type == null || type.isBlank()) && date == null) {
            return repo.findAll();
        }
        return repo.search(
            (department == null || department.isBlank()) ? null : department.trim(),
            (type == null || type.isBlank()) ? null : type.trim(),
            date
        );
    }

    public List<Object[]> getRegistrationStatsByDepartment() {
        return repo.countRegistrationsByDepartment();
    }

    public List<Object[]> getRegistrationStatsByEvent() {
        return repo.countRegistrationsByEvent();
    }
}
