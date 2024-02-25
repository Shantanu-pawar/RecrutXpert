package com.tool.RecruitXpert.DTO.RecruiterDto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRecruiterDto {
    String firstname;

    String lastname;

    String email;

    String password;

    String recruiterImg;

    String jobRole;

}
