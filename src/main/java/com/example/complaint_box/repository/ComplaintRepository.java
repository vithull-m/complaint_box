package com.example.complaint_box.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.complaint_box.model.Complaint;
import com.example.complaint_box.model.ComplaintStatus;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Find all complaints submitted by a specific resident
    List<Complaint> findByResidentId(Long residentId);

    // Find complaints by status
    List<Complaint> findByStatus(ComplaintStatus status);

    // Find open complaints sorted by creation date (oldest first)
    List<Complaint> findByStatusOrderByCreatedAtAsc(ComplaintStatus status);

    // Find complaints that are not resolved and older than a given date threshold (overdue > 5 days)
    List<Complaint> findByStatusNotAndCreatedAtBefore(ComplaintStatus status, LocalDateTime threshold);
}
