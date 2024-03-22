package com.tool.RecruitXpert.DTO.RecruiterDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseJWT {

   private String email_id;
   private String jwt_token;

}
