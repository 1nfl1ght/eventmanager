package com.course.eventmanager.service;

import com.course.eventmanager.model.user.Roles;
import com.course.eventmanager.model.user.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    public void checkOwnerOrAdmin(Long ownerId, User currentUser) {
        if (!ownerId.equals(currentUser.getId()) && !currentUser.getRole().equals(Roles.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
