package com.tool.RecruitXpert.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data @Table
@Builder
public class Admin {

    // HERE ADMIN HAS ACCESS TO USERS SO DON'T WANT TO HAVE MAPPINGS HERE
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String firstname;

    String lastname;

    @Column(unique = true, nullable = false)
    String email;

    // we've to check if this is admin-role
    // then only we'll provide all the admin access
    String organization;

    String password;

    String expectedCTC;

    String adminJobRole; // this variable stores admins job role

    String website;

    @Lob
    @Column(length = 10000000)
    byte[] adminImg;

    String companyName;

    String adminRole;

    @CreationTimestamp
    Date createdDate;

    @UpdateTimestamp
    Date lastActive;

    String address;

    String location;

    @OneToMany(mappedBy = "admin",cascade = CascadeType.ALL)
    List<Recruiter> recruiters = new ArrayList<>();

    public Admin(String email, String organization, String adminRole) {
        this.email = email;
        this.organization = organization;
        this.adminRole = adminRole;
    }
}
