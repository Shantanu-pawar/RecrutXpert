package com.tool.RecruitXpert.Controller;

import com.tool.RecruitXpert.DTO.AdminDTO.ImageUpload;
import com.tool.RecruitXpert.DTO.AdminDTO.ResponseDto.UpdateProfileDto;
import com.tool.RecruitXpert.DTO.RecruiterDto.*;

import com.tool.RecruitXpert.Service.RecruiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/recruiter")
@Slf4j
@CrossOrigin(origins =" https://recruiterexperttest.netlify.app")
public class RecruiterController {

    @Autowired
    RecruiterService service;

    @PostMapping("/sign-up")
    public ResponseEntity<?> recruiterSignUp(@RequestBody RecruiterSignUp signUpDto){
        try {
            String response = service.signUp(signUpDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // _________________recruiter profile section________________________


    @GetMapping("my-profile/{recruiterId}")
    public ResponseEntity<?> getRecruiterDetails(@PathVariable int recruiterId) {
        UpdateProfileDto message = service.getRecruiterDetails(recruiterId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // here' i'll get the adminId from your UI side.
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestBody ImageUpload dto) {
        try {
            String message = service.uploadImage(dto);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-recruiter")
    public ResponseEntity<?> updateRecruiter(@RequestBody UpdateRecruiterDto dto) {
        try {
            String response = service.updateRecruiter(dto);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/getRecruiterImg/{recruiterId}")
//    public ResponseEntity<?> getImg(@PathVariable long recruiterId){
//        try {
//            byte[] file = service.getImg(adminId);
//            return new ResponseEntity<>(file, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    // delete admin by id
//    @DeleteMapping("/delete-admin/{id}")
//    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
//        String message = service.deleteAdmin(id);
//        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
//    }


    @DeleteMapping("/delete-recruiter/{id}")
    public ResponseEntity<?> addRecruiter(@PathVariable("id") int id) {
        try {
            String response = service.deleteById(id);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

// _______________ recruiter dashboard apis' see and figure out these _____________________________

    @GetMapping("/dashboard/{id}")
    public ResponseEntity<?> recruiterDashboard(@PathVariable int id){
        RecruiterHomepageResponseDTO recruiterHomepageResponseDTO = service.recruiterDashboard(id);
        return new ResponseEntity<>(recruiterHomepageResponseDTO,HttpStatus.ACCEPTED);
    }


    @GetMapping("/user-Lists-who-Applied-to-recruiter/{id}")
    public ResponseEntity<?> getListOfUsersAppliedToJob(@PathVariable("id") long jobId) {
        try {
            List<UserAppliedJobstoRecruiterDto> list = service.getListOfUsersAppliedToJob(jobId);
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

