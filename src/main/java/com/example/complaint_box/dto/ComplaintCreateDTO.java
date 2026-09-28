package com.example.complaint_box.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintCreateDTO {

    @NotNull(message = "Resident ID is mandatory")
    private Long residentId;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    @NotBlank(message = "Room number is mandatory")
    private String roomNumber;

    @NotBlank(message = "Description is mandatory")
    private String description;

    public ComplaintCreateDTO() {
    }

    public ComplaintCreateDTO(Long residentId, Long categoryId, String roomNumber, String description) {
        this.residentId = residentId;
        this.categoryId = categoryId;
        this.roomNumber = roomNumber;
        this.description = description;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
