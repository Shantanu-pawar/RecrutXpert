package com.tool.RecruitXpert.Service;

import com.tool.RecruitXpert.DTO.UserDTO.*;
import com.tool.RecruitXpert.Entities.JobsApplication;
import com.tool.RecruitXpert.Entities.User;
import com.tool.RecruitXpert.Enums.EntityRoles;
import com.tool.RecruitXpert.Enums.Status;
import com.tool.RecruitXpert.Exceptions.UserNotFoundException;
import com.tool.RecruitXpert.Repository.JobRepository;
import com.tool.RecruitXpert.Repository.UserRepository;
import com.tool.RecruitXpert.DTO.UserInfoDto.UserInfoDto;
import com.tool.RecruitXpert.Transformer.UserTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired private PasswordEncoder encoder;
    @Autowired private UserInfoService userInfoService;


    public String signUp(SignUserDto dto) throws Exception{
//        validation : check unique email
        boolean check = userRepository.existsByEmail(dto.getEmail());
        if(check) throw new RuntimeException("Email already present, Enter valid email");

        User user = new User(dto.getEmail(), encoder.encode(dto.getPassword()));
        user.setEntityRoles(EntityRoles.USER);
        userRepository.save(user);

        UserInfoDto userStore = new UserInfoDto();
        userStore.setEmail(dto.getEmail());
        userStore.setPassword(dto.getPassword());
        userStore.setName("-");
        userStore.setRoles(EntityRoles.USER.name());
        userInfoService.addUser(userStore);

//        // email integration
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//
//        String body="Hi Welcome to RecruitXpert \n"+" Let's start your job search";
//        mailMessage.setSubject("RecruitXpert");
//        mailMessage.setFrom("shivasaiparsha@gmail.com");
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setText(body);
//        mailSender.send(mailMessage);

        return "signup successfully";
    }

    public UserResponse addUser(UserRequest userRequest) {
        User user = UserTransformer.UserRequestToUser(userRequest);
        User savedUser = userRepository.save(user);
        return UserTransformer.UserToUserResponse(savedUser);
    }

    public String deleteUser(int id) {
            Optional<User> optionalUser = userRepository.findById(id);
            if(!optionalUser.isPresent()){
                throw new UserNotFoundException("User not Found");
            }
            userRepository.deleteById(id);
            return "User Successfully Deleted";
    }

    public String updateUserStatus(UpdateUserStatus updateUserStatus) {
        Optional<User> optionalUser = userRepository.findById(updateUserStatus.getId());
        if(!optionalUser.isPresent()){
            throw new UserNotFoundException("User not Found");
        }
           User user = new User();
           user.setStatus(updateUserStatus.getStatus());
           userRepository.save(user);
           return "Status Updated Succesfully";
    }

    public String updateUser(UserRequest userRequest) {
        User user = UserTransformer.UserRequestToUser(userRequest);
        userRepository.save(user);
        return "Updated Successfully !!";

    }

    public List<User> getListForNullStatus() {
        List<User> list = userRepository.findAll();
        List<User> ans = new ArrayList<>();

        for(User user : list){
            if(user.getStatus() == null)
                ans.add(user);
        }
        return ans;
    }

    public String updateStatus(UpdateUserStatus update) {
        Optional<User> op = userRepository.findById(update.getId());
        if(!op.isPresent()) throw new RuntimeException("please update correct recruiter");

        User user = op.get();
        user.setStatus(update.getStatus());
        userRepository.save(user);
        return "Status updated successful";
    }

    public List<User> getApprovedList() {
        List<User> list = userRepository.findAll();
        List<User> ans = new ArrayList<>();
        for(User user : list){
            if(user.getStatus()!=null && user.getStatus().equals(Status.APPROVED)) ans.add(user);
        }
        return ans;
    }

    public String jobAppliedByUser(JobApplyDto dto) {
        User user = userRepository.findById(dto.getUserId()).get();
        JobsApplication jobs = jobRepository.findById(dto.getJobId()).get();

        // we' also have to set each job in user's list
        // and each user in job's list
        user.getJobsApplicationList().add(jobs);
        userRepository.save(user);

        jobs.getUsersApplied().add(user);
        jobRepository.save(jobs);
        return "Job applied successfully";
    }

    public List<JobsApplication> getAllAppliedJobList(int userId) {

        Optional<User> optionalUser = userRepository.findById(userId);
        List<JobsApplication> list =  optionalUser.get().getJobsApplicationList();
        return list;
    }
}
