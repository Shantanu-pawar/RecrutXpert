package com.tool.RecruitXpert.Controller;

import com.tool.RecruitXpert.Repository.UserInfoRepository;
import com.tool.RecruitXpert.Security.Config.AuthRequest;
import com.tool.RecruitXpert.Security.Jwt.JwtService;
import com.tool.RecruitXpert.DTO.UserInfoDto.ResetPasswordDto;
import com.tool.RecruitXpert.Entities.UserInfo;
import com.tool.RecruitXpert.DTO.UserInfoDto.UserInfoDto;
import com.tool.RecruitXpert.Service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins =" https://recruiterexperttest.netlify.app")

public class UserInfoController {
    @Autowired private UserInfoService service;
    @Autowired BCryptPasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private UserInfoRepository repository;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private BCryptPasswordEncoder encoder;

    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(@RequestBody UserInfoDto dto) {
        try {
            String response = service.addUser(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok("successfully Logout.");
    }

    // helper function
    public boolean authenticate(AuthRequest authRequest) {
        // this userDetails load from database
        UserDetails userDetails = service.loadUserByUsername(authRequest.getEmail());
        String password = userDetails.getPassword();
        return passwordEncoder.matches(authRequest.getPassword(), password);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) throws Exception, RuntimeException {

        UserInfo user = repository.findByEmail(authRequest.getEmail()).get();

        // case : only admin can unblock the account status once account block

//        if (authenticate(authRequest)) {
//            user.setPasswordCount(0);
//            user.setAccountBlock(false);
//            repository.save(user);
        if (authenticate(authRequest) && !user.isAccountBlock()) {
            return new ResponseEntity<>(jwtService.generateToken(authRequest.getEmail()), HttpStatus.OK);
        }

            if (user.isAccountBlock()) {
            String res =  "Oops! you're account is blocked! reach-out to Admin or reset you're password";
            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        if (user.getPasswordCount() == 1) {
            user.setAccountBlock(true);
            repository.save(user);
            String res =  "Warning! you've only one chance to login now";
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        else {
            user.setPasswordCount(user.getPasswordCount() + 1);
            repository.save(user);
            String res = "invalid Credentials";
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // unblock the status of that user or recruiter from admin
    @PostMapping("/account-unblocked-by-admin")
    public ResponseEntity<?> unblock(@RequestBody ResetPasswordDto reset){
        String response = service.unblock(reset);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // access this API from user.
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto reset){
        String response = service.resetPassword(reset);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // testing purpose
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }


    @GetMapping("/user/check")
    @PreAuthorize("hasAuthority('USER')")
    public String check() {
        return "this is checking purpose";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

}