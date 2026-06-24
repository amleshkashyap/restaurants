package com.example.restaurant.controller.dto;

import com.example.restaurant.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String roleName;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getFirstName() + " " + user.getLastName();
        this.mobileNumber = user.getMobile();
        this.roleName = user.getRole().getName();
    }
}
