package com.example.restaurant.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginModel {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
