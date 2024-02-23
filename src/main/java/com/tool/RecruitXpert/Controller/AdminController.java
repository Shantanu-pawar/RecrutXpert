package com.tool.RecruitXpert.Controller;

import com.tool.RecruitXpert.DTO.AdminDTO.*;
import com.tool.RecruitXpert.DTO.JobDTO.JobCreationDTO;
import com.tool.RecruitXpert.DTO.LogIn.LogIn;
import com.tool.RecruitXpert.DTO.RecruiterDto.JobAssignDto;
import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.AssignRecruiterResponse;
import com.tool.RecruitXpert.Entities.Recruiter;
import com.tool.RecruitXpert.Security.Jwt.JwtService;
import com.tool.RecruitXpert.Security.UserInfo;
import com.tool.RecruitXpert.Security.UserInfoController;
import com.tool.RecruitXpert.Service.AdminService;
import com.tool.RecruitXpert.Service.JobService;
import com.tool.RecruitXpert.Service.RecruiterService;
import com.tool.RecruitXpert.Security.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserInfoService service;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private AdminService adminService;
    @Autowired private RecruiterService recruiterService;
    @Autowired private JobService jobService;

    @Autowired private UserInfoController userInfoController;


    // for login we have to call the userInfo login api so login is sorted.
    @PostMapping("/sign-up")
    public ResponseEntity<?> adminSignUp(@RequestBody AdminSignUp signUpDto) {
        try {
            String response = adminService.adminSignUp(signUpDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/jobCreation")
    public ResponseEntity<?> createJob(@RequestBody JobCreationDTO jobCreationDTO) {
        String message = jobService.createJob(jobCreationDTO);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }


//    // get all list of jobs which is created by admin
//    response : id , job roles


    // 2nd api : as parameter UI se : job role id,
//    update all those filed




    @PostMapping("/assign") // here get all list of recruiters for assign
    public ResponseEntity<?> getAllListOfRecruiters() {
        try {
            List<AssignRecruiterResponse> responses = recruiterService.getAllListOfRecruiters();
            return new ResponseEntity<>(responses, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // set enums here for status
    @PutMapping("/JobAssigned-toRecruiter")
    public ResponseEntity<?> jobAssigned(@RequestBody JobAssignDto status) {
        try {
            String response = adminService.jobAssigned(status);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ithe manage jobs cha endpoint yeil


    // addAdmin new admin
    @PostMapping("/register")
    public ResponseEntity<?> addAdmin(@RequestBody FormAdminDTO formAdminDTO) throws IOException {
        String message = adminService.addAdmin(formAdminDTO);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    // update
    //  ADMIN CAN UPDATE THE JOB ROLE AND DESCRIPTION
    @PutMapping("/update")
    public ResponseEntity<?> updateAdmin(@RequestBody UpdateAdminDTO adminRequest) throws IOException {
        String message = adminService.updateAdmin(adminRequest);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    //    show this list to admin home page whos status is not active.
//    return the list whos recruiter status == null;
    @GetMapping("/recruiter/listOfRecruitersByStatus")
    public ResponseEntity<?> returnReturnStatus() {
        try {
            List<Recruiter> list = recruiterService.getListForNullStatus();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/approve")
    public ResponseEntity<?> returnReturnStatus(@RequestBody UpdateRecruiterStatus update) {
        try {
            String response = recruiterService.updateStatus(update);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/manage")
    public ResponseEntity<?> getApprovedList() {
        try {
            List<Recruiter> list = recruiterService.getApprovedList();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // delete profile by id
    @DeleteMapping("/delete-admin")
    public ResponseEntity<?> deleteAdmin(@RequestParam Long id) {
        String message = adminService.deleteAdmin(id);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    @GetMapping("/dashboard/{id}")
    public ResponseEntity<?> adminDashboard(@PathVariable Long id) {
        try {
            AdminHomePageResponseDTO adminHomepageResponseDTO = adminService.adminDashboard(id);
            return new ResponseEntity<>(adminHomepageResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // delete job by id
    @DeleteMapping("/deleteJob")
    public ResponseEntity<?> deleteJob(@RequestParam Long jobId) {
        String message = adminService.deleteJob(jobId);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    // update job by id
    @PutMapping("/deleteProfile")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        String message = adminService.deleteAdmin(id);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);

    }
}
