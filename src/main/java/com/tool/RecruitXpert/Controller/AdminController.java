package com.tool.RecruitXpert.Controller;

import com.tool.RecruitXpert.DTO.AdminDTO.*;
import com.tool.RecruitXpert.DTO.AdminDTO.ResponseDto.UpdateProfileDto;
import com.tool.RecruitXpert.DTO.JobDTO.JobCreationDTO;
import com.tool.RecruitXpert.DTO.JobDTO.UpdateJobDto;
import com.tool.RecruitXpert.DTO.RecruiterDto.JobAssignDto;
import com.tool.RecruitXpert.DTO.RecruiterDto.RecruiterRolesUpdate;
import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.AssignRecruiterResponse;
import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.JobTitleList;
import com.tool.RecruitXpert.Entities.JobsApplication;
import com.tool.RecruitXpert.Entities.Recruiter;
import com.tool.RecruitXpert.Security.Jwt.JwtService;
import com.tool.RecruitXpert.Service.AdminService;
import com.tool.RecruitXpert.Service.JobService;
import com.tool.RecruitXpert.Service.RecruiterService;
import com.tool.RecruitXpert.Service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins =" https://recruiterexperttest.netlify.app")
@Slf4j
public class AdminController {

    @Autowired private UserInfoService service;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private AdminService adminService;
    @Autowired private RecruiterService recruiterService;
    @Autowired private JobService jobService;
    @Autowired private UserInfoController userInfoController;


    // after successful signup just send - notification
    @PostMapping("/sign-up")
    public ResponseEntity<?> adminSignUp(@RequestBody AdminSignUp signUpDto) {
        try {
            String response = adminService.adminSignUp(signUpDto);
            log.info("admin signup successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("failed to create admin sign-up process with admin email {}" + signUpDto.getEmail());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // _________________admin profile section________________________

    // whenever we hit this api so we get all the details of admin that we wanted to update
    @GetMapping("my-profile/{adminId}")
    public ResponseEntity<?> getAdminDetails(@PathVariable Long adminId) {
        UpdateProfileDto message = adminService.getAdminDetails(adminId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // here' i'll get the adminId from your UI side.
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestBody ImageUpload dto) {
        try {
            String message = adminService.uploadImage(dto);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAdminImg/{adminId}")
    public ResponseEntity<?> getImg(@PathVariable long adminId){
        try {
            byte[] file = adminService.getImg(adminId);
            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // update admin profile
    @PutMapping("/update-admin")
    public ResponseEntity<?> updateAdmin(@RequestBody UpdateAdminDTO adminRequest) throws IOException {
        String message = adminService.updateAdmin(adminRequest);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    // delete admin by id
    @DeleteMapping("/delete-admin/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        String message = adminService.deleteAdmin(id);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    // _________________ [my dashboard Button] admin getting Recruiter list section________________________

    //    return the list whoes recruiter status == null;
    @GetMapping("/recruiter/listOfRecruitersByStatus")
    public ResponseEntity<?> returnReturnStatus() {
        try {
            List<Recruiter> list = recruiterService.getListForNullStatus();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-status") // APPROVE | DIS-APPROVED
    public ResponseEntity<?> returnReturnStatus(@RequestBody UpdateRecruiterStatus update) {
        try {
            String response = recruiterService.updateStatus(update);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/manage") // getting the list OF recruiters who is approved
    public ResponseEntity<?> getApprovedList() {
        try {
            List<Recruiter> list = recruiterService.getApprovedList();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-recruiter/{id}")
    public ResponseEntity<?> deleteRecruiterById(@PathVariable("id") int recruiterId) {
        String msg = adminService.deleteRecruiterById(recruiterId);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    //Changing recruiter approve reviewer commenter
    @PutMapping("/update-recruiter-roles")
    public ResponseEntity<?> updateRecruiterRoles(@RequestBody RecruiterRolesUpdate dto) {
        try {
            String response = recruiterService.updateRecruiterRoles(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ________________ [button create/update job roles] job manage section________________________

    @PostMapping("/jobCreation")
    public ResponseEntity<?> createJob(@RequestBody JobCreationDTO jobCreationDTO) {
        String message = jobService.createJob(jobCreationDTO);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    // getting recruiter lists whoes status is APPROVED
    @PutMapping("/JobAssigned-toRecruiter")
    public ResponseEntity<?> jobAssigned(@RequestBody JobAssignDto status) {
        try {
            String response = adminService.jobAssigned(status);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // here just getting the job object that admin want's to update
    @GetMapping("/updateRole/{jobId}")
    public ResponseEntity<?> updateRole(@PathVariable long jobId) {
        try {
            JobsApplication list = adminService.updateRole(jobId);
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    // getting all the job role types in list - for setting
    @GetMapping("/getJobList")
    public ResponseEntity<?> getJobList() {
        try {
            List<JobTitleList> list = adminService.getJobList();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    // update and delete endpoint
    @PutMapping("/update-job")
    public ResponseEntity<?> updateJobs(@RequestBody UpdateJobDto dto){
        try{
            String msg = jobService.updateJob(dto);
            return new ResponseEntity<>(msg, HttpStatus.GONE);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-job/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable("id") long jobId){
        try{
            String msg = jobService.deleteJob(jobId);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // assign roles to recruiter
    @PutMapping("/assignJobRole/{recruiterId}/{jobId}")
    public ResponseEntity<?> assignJobRole(@PathVariable int recruiterId, @PathVariable long jobId) {
        String msg = adminService.assignJobRole(recruiterId, jobId);
        return new ResponseEntity<>(msg, HttpStatus.ACCEPTED);
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

    // might delete this api  : but first see the usage
    @PostMapping("/assign") // here get all list of recruiters for assign
    public ResponseEntity<?> getAllListOfRecruiters() {
        try {
            List<AssignRecruiterResponse> responses = recruiterService.getAllListOfRecruiters();
            return new ResponseEntity<>(responses, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
