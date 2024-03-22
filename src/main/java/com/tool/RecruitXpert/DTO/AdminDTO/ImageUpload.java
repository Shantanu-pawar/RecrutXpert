package com.tool.RecruitXpert.DTO.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data @AllArgsConstructor @NoArgsConstructor
public class ImageUpload {
    private long adminId;
    private MultipartFile imageFile;
}
