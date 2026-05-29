package com.undoschool.parent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateParentRequest {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    @NotBlank private String timezone;
}
