package com.tool.RecruitXpert.Service;

import com.tool.RecruitXpert.Entities.UserInfo;
import com.tool.RecruitXpert.Repository.UserInfoRepository;
import com.tool.RecruitXpert.Security.Details.UserInfoDetails;
import com.tool.RecruitXpert.Security.EmailSender.EmailDto;
import com.tool.RecruitXpert.Security.EmailSender.EmailService;
import com.tool.RecruitXpert.DTO.UserInfoDto.ResetPasswordDto;
import com.tool.RecruitXpert.DTO.UserInfoDto.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;
    @Autowired private EmailService emailService;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserInfo> userDetail = repository.findByEmail(email);
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email));
    }

    public String addUser(UserInfoDto dto) {
        Optional<UserInfo> user = repository.findByEmail(dto.getEmail());

        if(user.isPresent()) return "email is already present enter new email";

        UserInfo userInfo = new UserInfo();
        userInfo.setName(dto.getName());
        userInfo.setEmail(dto.getEmail());
        userInfo.setRoles(dto.getRoles());
        userInfo.setPassword(encoder.encode(dto.getPassword()));
        repository.save(userInfo);
        return "User Added Successfully";
    }

    public String resetPassword(ResetPasswordDto reset) {
        Optional<UserInfo> op = repository.findByEmail(reset.getEmail());
        if(!op.isPresent()) return "provide correct email!";

        if(op.get().isAccountBlock())
            return "you're account is blocked and under cool down time-limit so wait! for it.";

        UserInfo user = op.get();
        user.setPassword(encoder.encode(reset.getPassword()));
        repository.save(user);

        EmailDto dto = new EmailDto();
//        dto.setRecipient(recruiter.getEmail());
        dto.setRecipient("shantanup2100@gmail.com");
        dto.setSubject("hey, here's you're password reset link! Message from RecruitXpert.");
        dto.setMessage("Dear User \n " +
                "since this email is private so don't forward it to anyone." );
        emailService.sendEmail(dto);

        return "password reset's successfully";
    }

    public String unblock(ResetPasswordDto reset) {
        Optional<UserInfo> op = repository.findByEmail(reset.getEmail());
        if(!op.isPresent()) return "provide correct email!";

        UserInfo user = op.get();
        user.setAccountBlock(false);
        user.setPasswordCount(0);
        repository.save(user);

        EmailDto dto = new EmailDto();
//        dto.setRecipient(recruiter.getEmail());
        dto.setRecipient("shantanup2100@gmail.com");
        dto.setSubject("Hey! you're Account status was reset by admin! Message from RecruitXpert.");
        dto.setMessage("Dear User \n " +
                "you're Block account status was just updated successfully by admin \n " +
                "Now you can easily login with you're previous credentials");
        emailService.sendEmail(dto);

        return "Account unblocked successfully";
    }
}

