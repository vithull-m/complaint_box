package com.example.complaint_box.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.complaint_box.exception.ResourceNotFoundException;
import com.example.complaint_box.model.Staff;
import com.example.complaint_box.repository.StaffRepository;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));
    }

    public Staff addStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff updateStaff(Long id, Staff updatedStaff) {
        Staff existingStaff = getStaffById(id);

        existingStaff.setName(updatedStaff.getName());
        existingStaff.setEmail(updatedStaff.getEmail());
        existingStaff.setRole(updatedStaff.getRole());

        return staffRepository.save(existingStaff);
    }

    public void deleteStaff(Long id) {
        Staff existingStaff = getStaffById(id);
        staffRepository.delete(existingStaff);
    }
}
