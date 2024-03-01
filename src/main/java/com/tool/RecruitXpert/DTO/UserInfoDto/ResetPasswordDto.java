package com.tool.RecruitXpert.Security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordDto {
    private String email;
    private String password;
}
