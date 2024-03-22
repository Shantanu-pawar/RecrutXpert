package com.tool.RecruitXpert.Service;

import com.tool.RecruitXpert.DTO.AdminDTO.ImageUpload;
import com.tool.RecruitXpert.DTO.AdminDTO.ResponseDto.UpdateProfileDto;
import com.tool.RecruitXpert.DTO.AdminDTO.UpdateRecruiterStatus;
import com.tool.RecruitXpert.DTO.RecruiterDto.*;

import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.AssignRecruiterResponse;
import com.tool.RecruitXpert.Entities.Admin;
import com.tool.RecruitXpert.Entities.JobsApplication;
import com.tool.RecruitXpert.Entities.Recruiter;
import com.tool.RecruitXpert.Entities.User;
import com.tool.RecruitXpert.Enums.EntityRoles;
import com.tool.RecruitXpert.Enums.Status;
import com.tool.RecruitXpert.Repository.JobRepository;
import com.tool.RecruitXpert.Repository.RecruiterRepository;
import com.tool.RecruitXpert.Security.EmailSender.EmailDto;
import com.tool.RecruitXpert.Security.EmailSender.EmailService;
import com.tool.RecruitXpert.DTO.UserInfoDto.UserInfoDto;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.util.Optional;

@Service
public class RecruiterService {

    @Autowired
    RecruiterRepository repository;
    @Autowired private UserInfoService userInfoService;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired JobRepository jobRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // signup recruiter :
    public String signUp(RecruiterSignUp dto) throws Exception{

//        validation : check unique email
        boolean check = repository.existsByEmail(dto.getEmail());
        if(check) throw new RuntimeException("Email already present, Enter valid email");

//        check unique org
        boolean uniqueOrg = repository.existsByOrganisation(dto.getOrganisationName());
        if(uniqueOrg) throw new RuntimeException("organization already present, Enter new one");

        Recruiter recruiter = new Recruiter(dto.getEmail(), dto.getOrganisationName(), EntityRoles.RECRUITER);
        recruiter.setPassword(passwordEncoder.encode(dto.getPassword()));
        repository.save(recruiter);

        UserInfoDto userStore = new UserInfoDto();
        userStore.setEmail(dto.getEmail());
        userStore.setPassword(dto.getPassword());
        userStore.setName("-");
        userStore.setRoles(EntityRoles.RECRUITER.name());
        userInfoService.addUser(userStore);
        return "signup successfully";
    }



    // update itself
    public String updateRecruiter(UpdateRecruiterDto dto) throws Exception {

        Optional<Recruiter> optional = repository.findById(dto.getId());
        Recruiter recruiter = optional.get();

        recruiter.setFirstname(dto.getFirstname());
        recruiter.setLastname(dto.getLastname());
        recruiter.setEmail(dto.getEmail());
        recruiter.setPassword(dto.getPassword());
        recruiter.setJobRole(dto.getJobRole());

        repository.save(recruiter);
        return "Recruiter Added Successfully";
    }

    // delete recruiter by id
    public String deleteById(int id) throws Exception {
        Optional<Recruiter> optional = repository.findById(id);
        if (!optional.isPresent()) throw new Exception("recruiter not exists");

        Recruiter recruiter = optional.get();
        repository.deleteById(recruiter.getId());
        return "Recruiter Deleted Successfully";
    }


    // getting list of recruiters whoes status is null for user
    public List<Recruiter> getListForNullStatus(){

        List<Recruiter> list = repository.findAll();
        List<Recruiter> ans = new ArrayList<>();

        for(Recruiter recruiter : list){
            if(recruiter.getRecruiterStatus() == null)
                ans.add(recruiter);
        }
        return ans;
    }

    public RecruiterHomepageResponseDTO recruiterDashboard(int id) {
          Recruiter recruiter = repository.findById(id).get();
          Admin admin = recruiter.getAdmin();
        RecruiterHomepageResponseDTO recruiterHomepageResponseDTO = new RecruiterHomepageResponseDTO();

        recruiterHomepageResponseDTO.setRecruiterImg(recruiter.getRecruiterImg());
        recruiterHomepageResponseDTO.setRecruiterDate((Date) recruiter.getCreatedDate());
        recruiterHomepageResponseDTO.setRecruiterLocation(recruiter.getLocation());
        recruiterHomepageResponseDTO.setRecruiterName(recruiter.getFirstname());
        recruiterHomepageResponseDTO.setRecruiterRole(recruiter.getJobRole());
        recruiterHomepageResponseDTO.setAdminDate((Date) admin.getCreatedDate());
        recruiterHomepageResponseDTO.setAdminImg(admin.getAdminImg());
        recruiterHomepageResponseDTO.setAdminName(admin.getFirstname());
        recruiterHomepageResponseDTO.setAdminRole(admin.getAdminRole());
        return  recruiterHomepageResponseDTO;
    }

    @Autowired private EmailService emailService;
    // recruiter can approve or disapprove the user status
    public String updateStatus(UpdateRecruiterStatus status) {
        Optional<Recruiter> op = repository.findById(status.getId());
        if(!op.isPresent()) throw new RuntimeException("please update correct recruiter");


        Recruiter recruiter = op.get();
        recruiter.setRecruiterStatus(status.getRecruiterStatus()); // setting the status here
        repository.save(recruiter);

        EmailDto dto = new EmailDto();
        dto.setRecipient("shantanupawar292@gmail.com");

//        dto.setRecipient(recruiter.getEmail());
        dto.setSubject("Congratulations! " + recruiter.getFirstname() + " :: Message from RecruitXpert.");
        dto.setMessage("Dear recruiter \n " +
                "you're status just updated successfully by admin \n " +
                "you're new status is : " + status.getRecruiterStatus());
        emailService.sendEmail(dto);
        return "Status updated successful";
    }


    public List<Recruiter> getApprovedList(){
        List<Recruiter> list = repository.findAll();
        List<Recruiter> ans = new ArrayList<>();

        for(Recruiter recruiter : list){
            if(recruiter.getRecruiterStatus()!=null &&
                    recruiter.getRecruiterStatus().equals(Status.APPROVED))
                ans.add(recruiter);
        }
        return ans;
    }

    public List<AssignRecruiterResponse> getAllListOfRecruiters() {
        List<Recruiter> list = repository.findAll();
        List<AssignRecruiterResponse> returnList = new ArrayList<>();

        for(Recruiter recruiter : list){
            AssignRecruiterResponse assign = new AssignRecruiterResponse(recruiter.getId(),
                    recruiter.getFirstname(), recruiter.getJobRole());

            returnList.add(assign);
        }
        return returnList;
    }

    public List<UserAppliedJobstoRecruiterDto> getListOfUsersAppliedToJob(long jobId) {

        List<UserAppliedJobstoRecruiterDto> list = new ArrayList<>();
        Optional<JobsApplication> op = jobRepository.findById(jobId);
        List<User> userList = op.get().getUsersApplied();

        for (User user : userList){
            UserAppliedJobstoRecruiterDto userApplied = new UserAppliedJobstoRecruiterDto();
            userApplied.setUserId(user.getUserId());
            userApplied.setFirstName(user.getFirstName());
            userApplied.setExperienceInYears(user.getExperienceInYears());
            userApplied.setCurrentJobTitle(user.getCurrentJobTitle());

            list.add(userApplied);
        }
        return list;
    }

    //Updating recruiter role
    public String updateRecruiterRoles(RecruiterRolesUpdate dto) {
        Optional<Recruiter> op = repository.findById(dto.getRecruiter_id());
        Recruiter recruiter = op.get();
        recruiter.setRecruiterRole(dto.getRoles());
        repository.save(recruiter);
        return "Status Updated";
    }

    public UpdateProfileDto getRecruiterDetails(int recruiterId) {

        Recruiter recruiter = repository.findById(recruiterId).get();

        UpdateProfileDto profile = new UpdateProfileDto(recruiter.getFirstname(), recruiter.getLastname(),
                recruiter.getAddress(), recruiter.getWebsite(), recruiter.getCompanyName(), recruiter.getRecruiterImg());
        return profile;
    }

    public String uploadImage(ImageUpload dto) {

        return "successfully";
    }


    // recruiter can change the status of user like [commenter | reviewer | all actions ]

}

