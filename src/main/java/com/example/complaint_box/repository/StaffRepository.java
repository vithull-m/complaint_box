package com.example.complaint_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.complaint_box.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
