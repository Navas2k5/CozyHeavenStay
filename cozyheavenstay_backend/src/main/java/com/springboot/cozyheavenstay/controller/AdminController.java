package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.dto.response.AdminBookingResponseDto;
import com.springboot.cozyheavenstay.dto.response.AdminHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.AdminUserResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.Role;
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

    @GetMapping("/api/admin/booking/all")
    List<AdminBookingResponseDto> getAllBookings(@RequestParam(name = "bookingStatus",required = false)BookingStatus bookingStatus,
                                                 @RequestParam(name="hotelId",required = false)Long hotelId,
                                                 @RequestParam(name="page",required = false,defaultValue = "0")Integer page,
                                                 @RequestParam(name = "size",required = false,defaultValue = "20")Integer size){
        return adminService.getAllBookings(bookingStatus,hotelId,page,size);
    }

    @GetMapping("/api/admin/user/all")
    public List<AdminUserResponseDto> getAllUser(@RequestParam(name = "role",required = false)Role role,
                                                 @RequestParam(name = "page",required = false,defaultValue = "0")Integer page,
                                                 @RequestParam(name = "size",required = false,defaultValue = "20")Integer size){
        return adminService.getAllUsers(role, page, size);
    }

    @PutMapping("/api/admin/user/{userId}/status")
    public AdminUserResponseDto updateUserStatus(@PathVariable Long userId,
                                                 @RequestParam(name = "active")boolean active){
        return adminService.updateUserStatus(userId,active);
    }
}
