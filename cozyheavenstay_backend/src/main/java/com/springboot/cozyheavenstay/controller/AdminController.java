package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.dto.response.AdminHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/api/admin/add")
    public void addAdmin(@Valid @RequestBody AdminRequestDto adminRequestDto){
        adminService.addAdmin(adminRequestDto);
    }

    @GetMapping("/api/admin/hotel/pending")
    public List<AdminHotelResponseDto> getAllPendingHotel(){
        return adminService.getAllPendingHotels();
    }

    @PutMapping("/api/admin/hotel/verification/{hotelId}")
    public void updateVerificationStatus(@PathVariable Long hotelId,
                                         @RequestParam VerificationStatus verificationStatus){
        adminService.updateHotelVerificationStatus(hotelId,verificationStatus);
    }
}
