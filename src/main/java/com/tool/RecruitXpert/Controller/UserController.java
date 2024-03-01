package com.tool.RecruitXpert.Controller;


import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.ResponseForResumeName;
import com.tool.RecruitXpert.DTO.UserDTO.*;
import com.tool.RecruitXpert.Entities.JobsApplication;
import com.tool.RecruitXpert.Entities.User;
import com.tool.RecruitXpert.ResumeUtility.ResumeUtilities;
import com.tool.RecruitXpert.Service.JobService;
import com.tool.RecruitXpert.Service.ResumeService;
import com.tool.RecruitXpert.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins =" https://recruiterexperttest.netlify.app")
//@CrossOrigin(origins = "https://blue-arda-82.tiiny.site/")

public class UserController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ResumeService resumeService;
    @Autowired private ResumeUtilities resumeUtilities;

    @PostMapping("/sign-up")
    public ResponseEntity<?> userSignUp(@RequestBody SignUserDto signUpDto) {
        try {
            String response = userService.signUp(signUpDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ______________ user profile apis same as admin-recruiter ____________________


//    baki rest of 4-5 api's just configured here

    // delete user by id
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam int id) {
        String message = userService.deleteUser(id);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }


    // ______________  find job button ____________________

    @PutMapping("/job-apply")
    public ResponseEntity<?> jobAppliedByUser(@RequestBody JobApplyDto dto) {
        try {
            String response = userService.jobAppliedByUser(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    // here check the responseBody that we wanted as
    @GetMapping("/showAllJobList")
    public ResponseEntity<?> findAllJobList(){
        try{
            List<JobsApplication> jobs = jobService.findAllJobLists();
            return new ResponseEntity<>(jobs, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ______________ [my dashboard in user button] ____________________

    // _______[resume section]_______

    // upload resume by specific user
    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file,
                                               @RequestParam("userid") int userid) throws IOException {
        try {
            String uploadResume = resumeService.saveResumeToDb(file, userid);
//            return ResponseEntity.status(HttpStatus.OK).body(uploadResume);
            return ResponseEntity.ok().body("{\"message\": \"" + uploadResume + "\"}");
        }
        catch (IOException e) {
            log.error("resume size not in correct format");
            throw new IOException(e.getMessage());
        }
        catch (Exception e){
            return new ResponseEntity<>("Resume size is exceed > 1 mb", HttpStatus.BAD_REQUEST);
        }
    }

    // view resume by id - recruiter can view resume when he's commenting
    @GetMapping("/viewResumes/{resumeid}")
    public ResponseEntity<?> viewImage(@PathVariable Integer resumeid) throws Exception {
        try {
            return resumeService.downloadResume(resumeid);
        }
        catch (Exception e) {
            String error = ("resume with " + resumeid + " not found : " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    //Versioning : update this logic to above endpoints
    @GetMapping("/get-all-uploaded-resume-list/{id}")
    public ResponseEntity<List<ResponseForResumeName>> showResume(@PathVariable("id") int userId) throws Exception{
        try {
            List<ResponseForResumeName> list =  resumeService.showResume(userId);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }



//    [told frontEnd to implement delete resume buttor]

    // delete resume by resume id
    @DeleteMapping("deleteResume/{resumeId}")
    public ResponseEntity<String> deleteResume(@PathVariable Integer resumeId) {
        try {
            resumeService.deleteResumeByResumeId(resumeId);
        } catch (Exception e) {
            log.error("resumeId not found");
            throw new RuntimeException(e.getMessage());
        }
        return new ResponseEntity<>("file deleted successfully", HttpStatus.OK);
    }

    // delete resumes of user by userid
    @DeleteMapping("/deleteByUserId/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer userId) throws Exception {
        try {
            String message = resumeService.deleteAllResumesByUserId(userId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){ throw new Exception(e.getMessage()); }
    }

    // setting current resume versioning
    @PutMapping("/setCurrentVersion/{resumeID}")
    public ResponseEntity<?> versionControlMethod(@PathVariable int resumeId) {
        try {
            String response = resumeService.versionControlMethod(resumeId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }


    //    here add the responseBody fields as : company name, job ctc , status of Job
    @GetMapping("/get-List-of-Applied-Jobs/{id}")
    public ResponseEntity<?> getAllAppliedJobList(@PathVariable("id") int userId) {
        try {
            List<JobsApplication> list = userService.getAllAppliedJobList(userId);
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }




    // ______________ Userful api's but we're not impl now ____________________


    // get approved users list
    @GetMapping("/manage")
    public ResponseEntity<?> getApprovedList() {
        try {
            List<User> list = userService.getApprovedList();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //create DTO : update user account status by id
    @PutMapping("/update_status")
    public ResponseEntity<?> updateUserStatus(@RequestBody UpdateUserStatus updateUserStatus) {
        String message = userService.updateUserStatus(updateUserStatus);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/approve") // APPROVED | DISAPPROVED | DISABLED for user statuses.
    public ResponseEntity<?> returnReturnStatus(@RequestBody UpdateUserStatus update) {
        try {
            String response = userService.updateStatus(update);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/listOfUsersByStatus")
    public ResponseEntity<?> returnReturnStatus() {
        try {
            List<User> list = userService.getListForNullStatus();
            return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
