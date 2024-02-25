package com.tool.RecruitXpert.DTO.AdminDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminDTO {

    Long adminId;

    String username;
    String expectedCTC;
    String expectedJobRole;
    String location;
}
