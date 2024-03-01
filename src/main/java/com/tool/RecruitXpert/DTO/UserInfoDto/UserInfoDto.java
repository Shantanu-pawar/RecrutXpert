package com.tool.RecruitXpert.Security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class UserInfoDto {

    private String email;

    private String name;

    private String password;

    private String roles;

    public UserInfoDto(String email, String password, String roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}