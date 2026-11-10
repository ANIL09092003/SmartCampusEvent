package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByDepartmentContainingIgnoreCase(String department);
    List<Event> findByTypeContainingIgnoreCase(String type);
    List<Event> findByDate(LocalDate date);
    List<Event> findByStatus(String status);

    @Query("SELECT e FROM Event e WHERE " +
           "(:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
           "(:type IS NULL OR LOWER(e.type) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
           "(:date IS NULL OR e.date = :date)")
    List<Event> search(@Param("department") String department,
                       @Param("type") String type,
                       @Param("date") LocalDate date);

    @Query("SELECT e.department, COUNT(r) FROM Registration r JOIN r.event e GROUP BY e.department")
    List<Object[]> countRegistrationsByDepartment();

    @Query("SELECT e.name, COUNT(r) FROM Registration r JOIN r.event e GROUP BY e.name ORDER BY COUNT(r) DESC")
    List<Object[]> countRegistrationsByEvent();
}
