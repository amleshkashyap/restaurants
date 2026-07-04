package com.example.restaurant.filter;

import com.example.restaurant.model.CurrentPrincipal;
import com.example.restaurant.model.User;
import org.springframework.stereotype.Component;

@Component("resourceGuard")
public class ResourceGuard {
    public boolean checkOwnership(CurrentPrincipal currentPrincipal, String ownerUsername) {
        if (currentPrincipal == null) {
            return false;
        }

        if (currentPrincipal.getRole().equals("ROLE_ADMIN")) {
            return true;
        }

        return (currentPrincipal.getEmail().equals(ownerUsername));
    }

    public boolean checkOwnership(CurrentPrincipal currentPrincipal, String owner1Username, String owner2Username) {
        if (currentPrincipal == null) {
            return false;
        }

        if (currentPrincipal.getRole().equals("ROLE_ADMIN")) {
            return true;
        }

        return (currentPrincipal.getEmail().equals(owner1Username) || currentPrincipal.getEmail().equals(owner2Username));
    }
}
