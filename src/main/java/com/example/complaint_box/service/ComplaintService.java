package com.example.complaint_box.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.complaint_box.dto.ComplaintCreateDTO;
import com.example.complaint_box.dto.ComplaintStatusUpdateDTO;
import com.example.complaint_box.exception.InvalidStatusTransitionException;
import com.example.complaint_box.exception.ResourceNotFoundException;
import com.example.complaint_box.exception.UnauthorizedComplaintActionException;
import com.example.complaint_box.model.Category;
import com.example.complaint_box.model.Complaint;
import com.example.complaint_box.model.ComplaintStatus;
import com.example.complaint_box.model.Resident;
import com.example.complaint_box.model.Staff;
import com.example.complaint_box.model.StaffRole;
import com.example.complaint_box.repository.CategoryRepository;
import com.example.complaint_box.repository.ComplaintRepository;
import com.example.complaint_box.repository.ResidentRepository;
import com.example.complaint_box.repository.StaffRepository;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ResidentRepository residentRepository;
    private final CategoryRepository categoryRepository;
    private final StaffRepository staffRepository;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            ResidentRepository residentRepository,
            CategoryRepository categoryRepository,
            StaffRepository staffRepository) {
        this.complaintRepository = complaintRepository;
        this.residentRepository = residentRepository;
        this.categoryRepository = categoryRepository;
        this.staffRepository = staffRepository;
    }

    // Helper to calculate and refresh the overdue flag
    private void refreshOverdueStatus(Complaint complaint) {
        if (complaint.getStatus() != ComplaintStatus.RESOLVED && complaint.getCreatedAt() != null) {
            boolean isOverdue = complaint.getCreatedAt().isBefore(LocalDateTime.now().minusDays(5));
            complaint.setOverdue(isOverdue);
        } else {
            complaint.setOverdue(false);
        }
    }

    public Complaint createComplaint(ComplaintCreateDTO dto) {
        Resident resident = residentRepository.findById(dto.getResidentId())
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + dto.getResidentId()));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Complaint complaint = new Complaint(dto.getRoomNumber(), dto.getDescription(), resident, category);
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        complaints.forEach(this::refreshOverdueStatus);
        return complaints;
    }

    public Complaint getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        refreshOverdueStatus(complaint);
        return complaint;
    }

    public List<Complaint> getComplaintsByResidentId(Long residentId) {
        if (!residentRepository.existsById(residentId)) {
            throw new ResourceNotFoundException("Resident not found with id: " + residentId);
        }
        List<Complaint> complaints = complaintRepository.findByResidentId(residentId);
        complaints.forEach(this::refreshOverdueStatus);
        return complaints;
    }

    public List<Complaint> getOpenComplaintsSortedByAge() {
        List<Complaint> complaints = complaintRepository.findByStatusOrderByCreatedAtAsc(ComplaintStatus.OPEN);
        complaints.forEach(this::refreshOverdueStatus);
        return complaints;
    }

    public List<Complaint> getOverdueComplaints() {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
        List<Complaint> complaints = complaintRepository.findByStatusNotAndCreatedAtBefore(ComplaintStatus.RESOLVED, fiveDaysAgo);
        complaints.forEach(c -> c.setOverdue(true));
        return complaints;
    }

    public Complaint assignStaff(Long complaintId, Long staffId) {
        Complaint complaint = getComplaintById(complaintId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + staffId));

        complaint.setAssignedStaff(staff);
        if (complaint.getStatus() == ComplaintStatus.OPEN) {
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        }
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaintStatus(Long id, ComplaintStatusUpdateDTO dto) {
        Complaint complaint = getComplaintById(id);
        ComplaintStatus currentStatus = complaint.getStatus();
        ComplaintStatus newStatus = dto.getStatus();

        if (currentStatus == newStatus) {
            return complaint;
        }

        // Validate status progression: OPEN -> IN_PROGRESS -> RESOLVED (No backward transitions)
        if (currentStatus == ComplaintStatus.RESOLVED) {
            throw new InvalidStatusTransitionException("Cannot change the status of an already RESOLVED complaint.");
        }

        if (currentStatus == ComplaintStatus.IN_PROGRESS && newStatus == ComplaintStatus.OPEN) {
            throw new InvalidStatusTransitionException("Backward transition from IN_PROGRESS to OPEN is not allowed.");
        }

        // Authorization check when marking as RESOLVED
        if (newStatus == ComplaintStatus.RESOLVED) {
            if (dto.getActingStaffId() != null) {
                Staff actingStaff = staffRepository.findById(dto.getActingStaffId())
                        .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + dto.getActingStaffId()));

                boolean isWarden = actingStaff.getRole() == StaffRole.WARDEN;
                boolean isAssignedStaff = complaint.getAssignedStaff() != null &&
                        complaint.getAssignedStaff().getId().equals(actingStaff.getId());

                if (!isWarden && !isAssignedStaff) {
                    throw new UnauthorizedComplaintActionException("Only the assigned staff member or a warden can mark a complaint as RESOLVED.");
                }
            }
            complaint.setResolvedAt(LocalDateTime.now());
            complaint.setResolutionRemark(dto.getRemark());
            complaint.setOverdue(false);
        }

        complaint.setStatus(newStatus);
        return complaintRepository.save(complaint);
    }

    public void deleteComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        complaintRepository.delete(complaint);
    }
}
