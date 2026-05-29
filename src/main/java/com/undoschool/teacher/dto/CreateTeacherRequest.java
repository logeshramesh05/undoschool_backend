package com.undoschool.teacher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeacherRequest {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    @NotBlank private String timezone;
}
