package com.tool.RecruitXpert.Transformer;

import com.tool.RecruitXpert.DTO.UserDTO.UserRequest;
import com.tool.RecruitXpert.DTO.UserDTO.UserResponse;
import com.tool.RecruitXpert.Entities.User;

public class UserTransformer {

    public static User UserRequestToUser(UserRequest userRequest){
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .city(userRequest.getCity())
                .currentJobTitle(userRequest.getCurrentJobTitle())
                .experienceInYears(userRequest.getExperienceInYears())
                .relevantExp(userRequest.getRelevantExp())
                .location(userRequest.getLocation())
                .city(userRequest.getCity())
                .mobileNo(userRequest.getMobileNo())
                .currentOrg(userRequest.getCurrentOrg())
                .skillset(userRequest.getSkillSet())
                .zipCode(userRequest.getZipCode())
                .build();
    }

    public static UserResponse UserToUserResponse(User user){
       return UserResponse.builder()
               .firstName(user.getFirstName())
               .lastName(user.getLastName())
               .mobileNo(user.getMobileNo())
               .email(user.getEmail())
               .build();
    }
}
