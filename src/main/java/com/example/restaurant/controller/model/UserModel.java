package com.example.restaurant.controller.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class UserModel {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @Digits(integer = 10, fraction = 0)
    private String mobileNumber;
    @NotNull
    private String roleName;
}
