package com.example.restaurant.filter;

import com.example.restaurant.model.User;
import org.springframework.stereotype.Component;

@Component("resourceGuard")
public class ResourceGuard {
    public boolean checkOwnership(User user, String ownerUsername) {
        if (user == null) {
            return false;
        }

        if (user.getRole().getName().equals("ROLE_ADMIN")) {
            return true;
        }

        return (user.getEmail().equals(ownerUsername));
    }

    public boolean checkOwnership(User user, String owner1Username, String owner2Username) {
        if (user == null) {
            return false;
        }

        if (user.getRole().getName().equals("ROLE_ADMIN")) {
            return true;
        }

        return (user.getEmail().equals(owner1Username) || user.getEmail().equals(owner2Username));
    }
}
