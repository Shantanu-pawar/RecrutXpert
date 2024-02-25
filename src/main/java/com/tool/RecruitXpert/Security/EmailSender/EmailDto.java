package com.tool.RecruitXpert.Security.EmailSender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class EmailDto {

    private String recipient;
    private String subject;
    private String message;

}
