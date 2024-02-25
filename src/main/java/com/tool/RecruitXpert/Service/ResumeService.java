package com.tool.RecruitXpert.Service;

import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.ResponseForResumeName;
import com.tool.RecruitXpert.Entities.ResumeEntity;
import com.tool.RecruitXpert.Entities.User;
import com.tool.RecruitXpert.Exceptions.UserNotFoundException;
import com.tool.RecruitXpert.Repository.ResumeRepository;
import com.tool.RecruitXpert.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

import static com.tool.RecruitXpert.ResumeUtility.ResumeUtilities.getMediaType;

@Service
@Slf4j
public class ResumeService {
    @Autowired
    ResumeRepository resumeRepository;
    @Autowired
    UserRepository userRepository;

    // save resume in db
    public String saveResumeToDb(MultipartFile file, int userId) throws Exception {

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent())
            throw new RuntimeException("User not found");

        // while saving we can check if there is only 4 resumes we can upload
        User user = userRepository.findById(userId).get();
        int size = userOptional.get().getResumeList().size();
        if(size > 4) throw new RuntimeException("You already cross the limit for uploading resume");

        long sizeInBytes = file.getSize(); // Getting size of the uploaded file in bytes


        long sizeInKb=sizeInBytes/1024;
        long sizeInMb = sizeInBytes /2048; // Converting bytes to megabyte
        long lowBound=50;
        long upperBound=1; // 1mb size
        if(sizeInKb<50) {
            log.error("resume size is less than  50kb log");
            throw new Exception("uploaded Resume doc size"+ sizeInKb +"KB is lessthan required size"+ lowBound);
        }


        if(sizeInMb>upperBound) {
            log.error("resume size is greater than  1mb log");
            throw new Exception("uploaded image size"+sizeInMb+"MB is greaterthan required size"+upperBound);
        }

        ResumeEntity imageData = resumeRepository.save(ResumeEntity.builder()
                .name(file.getOriginalFilename())
                .docType(file.getContentType())
                .imageData(file.getBytes()).user(user).build());

        user.getResumeList().add(imageData);
        userRepository.save(user);

        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        } else return "File not uploaded successfully";
    }


    // see Resume from database
    public ResponseEntity<?> downloadResume(Integer id) {
        Optional<ResumeEntity> resumeList = resumeRepository.findById(id);
        byte[] images = resumeList.get().getImageData();

        String typearray[] = resumeList.get().getDocType().split("/");
        String type = typearray[1];

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(getMediaType(type)).body(images);
    }

    // send list of resumes from this
    public List<ResponseForResumeName> showResume(int userId) {

        Optional<User> op = userRepository.findById(userId);
        User user = op.get();
        List<ResumeEntity> resumeList = user.getResumeList();

        if(resumeList.size() == 0)
            throw new RuntimeException("No resumes present");

        List<ResponseForResumeName> response = new ArrayList<>();
        for(ResumeEntity resume : resumeList){
            ResponseForResumeName addThis = new ResponseForResumeName(resume.getResumeId(), resume.getName());
            response.add(addThis);
        }
        return response;
    }


    // Delete resume by resumeId service
    public String deleteResumeByResumeId(int resumeId) throws Exception {

        if (!resumeRepository.existsById(resumeId))
            throw new Exception("resume not found exception"); //if resume id not found throw exception

        resumeRepository.deleteById(resumeId);
        return resumeId + " resume : " + resumeId + " deleted successfully";
    }

    //delete resumes of user by user id services
    public String deleteAllResumesByUserId(int userId) {

        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("user not found with userId " + userId);

        User user = userRepository.findById(userId).get();
        List<ResumeEntity> list = user.getResumeList();
        resumeRepository.deleteAll(list);

        return "successfully deleted resumes of userId :" + userId;
    }


    public List<ResumeEntity> getResumeList() {
        return resumeRepository.findAll();
    }

    public String versionControlMethod(int resumeId) {
        // set resume id in user entity
        Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
        ResumeEntity resume = optional.get();
        int userId = resume.getUser().getUserId();

        User user = userRepository.findById(userId).get();
        user.setCurrentResumeVersion(resumeId);

        userRepository.save(user);

        return "resume updated successfully";
    }


}

