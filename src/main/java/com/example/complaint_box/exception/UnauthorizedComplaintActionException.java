package com.example.complaint_box.exception;

public class UnauthorizedComplaintActionException extends RuntimeException {
    public UnauthorizedComplaintActionException(String message) {
        super(message);
    }
}
