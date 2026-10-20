package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.model.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public static Admin convertDtoToEntity(AdminRequestDto dto){
        Admin admin = new Admin();
        admin.setName(dto.name());
        admin.setEmail(dto.email());
        return admin;
    }
}
