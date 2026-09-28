package com.example.complaint_box.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.complaint_box.exception.ResourceNotFoundException;
import com.example.complaint_box.model.Resident;
import com.example.complaint_box.repository.ResidentRepository;

@Service
public class ResidentService {

    private final ResidentRepository residentRepository;

    public ResidentService(ResidentRepository residentRepository) {
        this.residentRepository = residentRepository;
    }

    public List<Resident> getAllResidents() {
        return residentRepository.findAll();
    }

    public Resident getResidentById(Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + id));
    }

    public Resident addResident(Resident resident) {
        return residentRepository.save(resident);
    }

    public Resident updateResident(Long id, Resident updatedResident) {
        Resident existingResident = getResidentById(id);

        existingResident.setName(updatedResident.getName());
        existingResident.setEmail(updatedResident.getEmail());
        existingResident.setRoomNumber(updatedResident.getRoomNumber());

        return residentRepository.save(existingResident);
    }

    public void deleteResident(Long id) {
        Resident existingResident = getResidentById(id);
        residentRepository.delete(existingResident);
    }
}
