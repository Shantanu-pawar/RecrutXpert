package com.tool.RecruitXpert.Service;


import com.tool.RecruitXpert.DTO.AdminDTO.*;
import com.tool.RecruitXpert.DTO.AdminDTO.ResponseDto.GetAdminUpdateProfile;
import com.tool.RecruitXpert.DTO.JobDTO.JobCreationDTO;
import com.tool.RecruitXpert.DTO.JobDTO.UpdateJobDto;
import com.tool.RecruitXpert.DTO.RecruiterDto.JobAssignDto;
import com.tool.RecruitXpert.DTO.RecruiterDto.responseDto.JobTitleList;
import com.tool.RecruitXpert.Entities.Admin;
import com.tool.RecruitXpert.Entities.JobsApplication;
import com.tool.RecruitXpert.Entities.Recruiter;
import com.tool.RecruitXpert.Entities.ResumeEntity;
import com.tool.RecruitXpert.Exceptions.AdminNotFoundException;
import com.tool.RecruitXpert.Repository.AdminRepository;
import com.tool.RecruitXpert.Repository.JobRepository;
import com.tool.RecruitXpert.Repository.RecruiterRepository;
import com.tool.RecruitXpert.Repository.UserInfoRepository;
import com.tool.RecruitXpert.Security.UserInfo;
import com.tool.RecruitXpert.Security.UserInfoController;
import com.tool.RecruitXpert.Security.UserInfoDto;
import com.tool.RecruitXpert.Security.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    ModelMapper modelMapper;


    public String updateAdmin(UpdateAdminDTO payload) throws IOException {
        Optional<Admin> optionalAdmin = adminRepository.findById(payload.getAdminId());
        if (!optionalAdmin.isPresent())
            throw new AdminNotFoundException("Enter correct ID, Admin not Found");

        Admin admin = optionalAdmin.get();
        admin.setFirstname(payload.getUsername());
        admin.setExpectedCTC(payload.getExpectedCTC());
        admin.setAdminJobRole(payload.getExpectedJobRole());
        admin.setLocation(payload.getLocation());

        adminRepository.save(admin);
        return "Successfully Updated !";
    }

    public String deleteAdmin(Long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (!optionalAdmin.isPresent()) {
            throw new AdminNotFoundException("Admin not Found");
        }
        adminRepository.deleteById(id);
        return "Successfully Deleted";
    }

    // update profile image
    public String addMultipartFile(MultipartFile file, Long id) throws IOException {
        Admin admin = adminRepository.findById(id).get();
        //byte[] fileImage = file.getBytes();
        //  admin.setAdminImg(fileImage);
        adminRepository.save(admin);
        return "success";
    }


    public String adminSignUp(AdminSignUp dto) throws Exception {
//        validation : check unique email
        boolean check = adminRepository.existsByEmail(dto.getEmail());
        if (check) throw new RuntimeException("Email already present, Enter valid email");

//        check unique org
        boolean uniqueOrg = adminRepository.existsByOrganization(dto.getOrganization());
        if (uniqueOrg) throw new RuntimeException("organization already present, Enter new one");

        Admin admin = new Admin(dto.getEmail(), dto.getOrganization(), "ADMIN");
        admin.setPassword(encoder.encode(dto.getPassword()));

        UserInfoDto userStore = new UserInfoDto();
        userStore.setEmail(dto.getEmail());
        userStore.setPassword(dto.getPassword());
        userStore.setName("-");
        userStore.setRoles("ADMIN");
        userInfoService.addUser(userStore);
        adminRepository.save(admin);
        return "signup successfully";
    }

    // login to recruiter ?: case : if recruiter approved then only he can able to access the portal
    public String adminSignIn(AdminSignUp login) {
        Admin recruiter = adminRepository.findByEmail(login.getEmail()).get();

        if (recruiter.getEmail().equals(login.getEmail()) &&
                recruiter.getOrganization().equals(login.getOrganization()) &&
                recruiter.getPassword().equals(login.getPassword()))
            return "successful login";

        else return "Incorrect organisation, email or password";
    }


    public AdminHomePageResponseDTO adminDashboard(Long id) {

        Admin admin = adminRepository.findById(id).get();
        AdminHomePageResponseDTO adminHomepageResponseDTO = new AdminHomePageResponseDTO();

        adminHomepageResponseDTO.setAdminDate((Date) admin.getCreatedDate());
        adminHomepageResponseDTO.setAdminImg(admin.getAdminImg());
        adminHomepageResponseDTO.setAdminName(admin.getFirstname());
        adminHomepageResponseDTO.setAdminRole(admin.getAdminRole());
        adminHomepageResponseDTO.setWebsite(admin.getWebsite());
        adminHomepageResponseDTO.setLocation(admin.getLocation());
        adminHomepageResponseDTO.setCompanyName(admin.getCompanyName());
        return adminHomepageResponseDTO;
    }

    public String jobAssigned(JobAssignDto jobAssignDto) {

        Recruiter recruiter = recruiterRepository.findById(jobAssignDto.getRecruiterId()).get();


//        recruiter.setRecruiterStatus(jobAssignDto.getRecruiterRole().);

        return "status updated Successfully";
    }

    public String deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
        return "job deleted successfully";
    }


    public List<JobTitleList> getJobList() {

        List<JobTitleList> list = new ArrayList<>();
        List<JobsApplication> applicationList = jobRepository.findAll();

        for (JobsApplication jobsApplication : applicationList) {
            JobTitleList jobTitleList = new JobTitleList();
            jobTitleList.setJobId(jobsApplication.getJobid());
            jobTitleList.setJobTitle(jobsApplication.getJobTitle());

            list.add(jobTitleList);
        }
        return list;
    }

    public JobsApplication updateRole(long jobId) {
        Optional<JobsApplication> op = jobRepository.findById(jobId);
        return op.get();
    }

    public String deleteRecruiterById(int recruiterId) {
        recruiterRepository.deleteById(recruiterId);
        return "recruiter deleted successfully";
    }


    public String assignJobRole(int recruiterId, long jobId) {
        JobsApplication jobOptional = jobRepository.findById(jobId).get();
        Recruiter recruiterOptional = recruiterRepository.findById(recruiterId).get();

        recruiterOptional.setJobsApplication(jobOptional);
        recruiterRepository.save(recruiterOptional);

        jobOptional.setRecruiter(recruiterOptional);
        jobRepository.save(jobOptional);
        return "job assigned successfully";
    }


    public GetAdminUpdateProfile getAdminDetails(Long adminId) {

        Admin admin = adminRepository.findById(adminId).get();
        GetAdminUpdateProfile profile = new GetAdminUpdateProfile();

        profile.setUsername(admin.getFirstname());
        profile.setExpectedCTC(admin.getExpectedCTC());
        profile.setExpectedJobRole(admin.getExpectedCTC());
        profile.setLocation(admin.getLocation());

        // set this pic after we're uploading img
//        profile.setAdminImg(admin.getAdminImg());
        return profile;
    }

    public String uploadImage(MultipartFile imageFile) throws Exception{

        long sizeInBytes = imageFile.getSize(); // Getting size of the uploaded file in bytes

        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        long sizeInKb=sizeInBytes/1024; // converting size into kb
//        long sizeInMb = sizeInBytes /2048; // Converting bytes to megabyte
        long lowBound=30; //lower bound in kb
        long upperBound=1000; // upper bound in mb

        if(sizeInKb<30) {
            log.error("image size is less than  50kb log");
            throw new Exception("uploaded Resume doc size"+ sizeInKb +"KB is lessthan required size"+ lowBound);
        }


        if(sizeInKb>upperBound) {
            log.error("image size is greater than  1mb log");
            throw new Exception("uploaded image size"+1+"MB is greaterthan required size"+upperBound);
        }

       String s[]=imageFile.getContentType().split(".");
        if(s.length>=1&&s[1].equals("pdf")) throw new Exception("file format does't support");

        admin.setAdminImg(imageFile.getBytes());
        return "image uploaded successfully";

    }
}

