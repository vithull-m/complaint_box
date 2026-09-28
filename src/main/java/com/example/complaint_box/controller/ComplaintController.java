package com.example.complaint_box.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.complaint_box.dto.ComplaintCreateDTO;
import com.example.complaint_box.dto.ComplaintStatusUpdateDTO;
import com.example.complaint_box.model.Complaint;
import com.example.complaint_box.service.ComplaintService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // POST /api/complaints - Create a new complaint
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody ComplaintCreateDTO dto) {
        Complaint createdComplaint = complaintService.createComplaint(dto);
        return new ResponseEntity<>(createdComplaint, HttpStatus.CREATED);
    }

    // GET /api/complaints/{id} - Get complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    // GET /api/complaints/resident/{residentId} - Get complaints by resident
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<Complaint>> getComplaintsByResident(@PathVariable Long residentId) {
        return ResponseEntity.ok(complaintService.getComplaintsByResidentId(residentId));
    }

    // GET /api/complaints/open - Get all open complaints sorted by age (oldest first)
    @GetMapping("/open")
    public ResponseEntity<List<Complaint>> getOpenComplaints() {
        return ResponseEntity.ok(complaintService.getOpenComplaintsSortedByAge());
    }

    // GET /api/complaints/overdue - Get all overdue complaints
    @GetMapping("/overdue")
    public ResponseEntity<List<Complaint>> getOverdueComplaints() {
        return ResponseEntity.ok(complaintService.getOverdueComplaints());
    }

    // PUT /api/complaints/{id}/assign/{staffId} - Assign staff member to complaint
    @PutMapping("/{id}/assign/{staffId}")
    public ResponseEntity<Complaint> assignStaff(
            @PathVariable Long id,
            @PathVariable Long staffId) {
        return ResponseEntity.ok(complaintService.assignStaff(id, staffId));
    }

    // PUT /api/complaints/{id}/status - Update complaint status (with transition & authorization check)
    @PutMapping("/{id}/status")
    public ResponseEntity<Complaint> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintStatusUpdateDTO dto) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(id, dto));
    }

    // DELETE /api/complaints/{id} - Delete complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
