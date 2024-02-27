package com.tool.RecruitXpert.Security;

import com.tool.RecruitXpert.Enums.Status;
import com.tool.RecruitXpert.Repository.UserInfoRepository;
import com.tool.RecruitXpert.Security.Config.AuthRequest;
import com.tool.RecruitXpert.Security.Jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserInfoController {
    @Autowired
    private UserInfoService service;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository repository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BCryptPasswordEncoder encoder;

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
        if (authenticate(authRequest)) {
            user.setPasswordCount(0);
            user.setAccountBlock(false);
            repository.save(user);
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
    @PutMapping("/account-unblocked-by-admin")
    public ResponseEntity<?> unblock(@RequestParam String email){
        String response = service.unblock(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // access this API from user.
    @PutMapping("/reset-password")
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
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

}