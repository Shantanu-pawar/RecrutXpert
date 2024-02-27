package com.tool.RecruitXpert.Security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;
    
    private String roles;

    private int passwordCount; // checking for passwords should be < 4 times

    private boolean accountBlock;

    public UserInfo(String name, String email, String password, String roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public UserInfo(String email, String password, String roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
