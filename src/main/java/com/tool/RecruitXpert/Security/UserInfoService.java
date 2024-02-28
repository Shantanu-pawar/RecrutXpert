package com.tool.RecruitXpert.Security;

import com.tool.RecruitXpert.Repository.UserInfoRepository;
import com.tool.RecruitXpert.Security.Details.UserInfoDetails;
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
        return "password reset's successfully";
    }

    public String unblock(ResetPasswordDto reset) {
        Optional<UserInfo> op = repository.findByEmail(reset.getEmail());
        if(!op.isPresent()) return "provide correct email!";

        UserInfo user = op.get();
        user.setAccountBlock(false);
        user.setPasswordCount(0);
        repository.save(user);
        return "Account unblocked successfully";
    }
}
