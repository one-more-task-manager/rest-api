package com.danyatheworst.task_manager.exceptions;

import jakarta.persistence.EntityExistsException;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
