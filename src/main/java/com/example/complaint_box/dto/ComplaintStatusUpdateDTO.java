package com.example.complaint_box.dto;

import com.example.complaint_box.model.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public class ComplaintStatusUpdateDTO {

    @NotNull(message = "Status is mandatory")
    private ComplaintStatus status;

    private String remark;

    private Long actingStaffId; // ID of the staff/warden performing the update

    public ComplaintStatusUpdateDTO() {
    }

    public ComplaintStatusUpdateDTO(ComplaintStatus status, String remark, Long actingStaffId) {
        this.status = status;
        this.remark = remark;
        this.actingStaffId = actingStaffId;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getActingStaffId() {
        return actingStaffId;
    }

    public void setActingStaffId(Long actingStaffId) {
        this.actingStaffId = actingStaffId;
    }
}
