package com.tool.RecruitXpert.DTO.RecruiterDto;

import com.tool.RecruitXpert.Enums.RecruiterRoles;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruiterRolesUpdate {
    int recruiter_id;
    RecruiterRoles roles;
}
