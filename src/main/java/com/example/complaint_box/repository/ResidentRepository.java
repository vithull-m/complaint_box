package com.example.complaint_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.complaint_box.model.Resident;

public interface ResidentRepository extends JpaRepository<Resident, Long> {
}
