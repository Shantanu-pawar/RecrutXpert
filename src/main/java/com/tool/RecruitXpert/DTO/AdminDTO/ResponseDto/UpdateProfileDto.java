package com.tool.RecruitXpert.DTO.AdminDTO.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDto {

    String firstname;
    String lastname;
    String address;
    String website;
    String companyName;
    byte[] adminImg;
}

