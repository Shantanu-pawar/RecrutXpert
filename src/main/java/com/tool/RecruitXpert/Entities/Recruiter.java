package com.tool.RecruitXpert.Entities;

import com.tool.RecruitXpert.Enums.EntityRoles;
import com.tool.RecruitXpert.Enums.RecruiterRoles;
import com.tool.RecruitXpert.Enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String firstname;

    String lastname;

    String organisation;

    @Column(unique = true, nullable = false)
    String email;
    String address;
    String website;
    String companyName;

    String password;

    @Lob
    @Column(length = 10000000)
    byte[] recruiterImg;

    String jobRole;

    @CreationTimestamp
    Date createdDate;

    @UpdateTimestamp
    Date lastActive;

    String location;

    int passwordCount; // checking for passwords should be < 4 times

    boolean accountBlock;

    @Enumerated(value = EnumType.STRING)
    Status recruiterStatus; // approved | dis-approve = cannot access |

    @Enumerated(value = EnumType.STRING)
    RecruiterRoles recruiterRole; // active | de-active

    @Enumerated(value = EnumType.STRING)
    EntityRoles entityRoles; // admin, recruiter, user

//    // mapped for user to recruiter
//    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
//    private List<User> userList = new ArrayList<>();

    @ManyToOne
    @JoinColumn
    Admin admin;

    @OneToOne(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private JobsApplication jobsApplication;


    public Recruiter(String firstname, String lastname, String email, String password, String jobRole) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.jobRole = jobRole;
    }

    public Recruiter( String email, String organisation, EntityRoles entityRoles) {
        this.email = email;
        this.organisation = organisation;
        this.entityRoles = entityRoles;
    }
}
