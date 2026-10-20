package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.dto.response.AdminHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.AdminMapper;
import com.springboot.cozyheavenstay.mapper.HotelMapper;
import com.springboot.cozyheavenstay.model.Admin;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.AdminRepository;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;

    @Transactional
    public void addAdmin(AdminRequestDto adminRequestDto) {

        Admin admin = AdminMapper.convertDtoToEntity(adminRequestDto);

        //create user object
        User user = new User();
        user.setUsername(adminRequestDto.username());
        user.setPassword(passwordEncoder.encode(adminRequestDto.password()));
        user.setRole(Role.ADMIN);
        user = userRepository.save(user);

        admin.setUser(user);

        adminRepository.save(admin);
    }

    public List<AdminHotelResponseDto> getAllPendingHotels() {
        List<Hotels> pendingHotels = hotelRepository.getAllPendingHotels(VerificationStatus.PENDING);
        return pendingHotels.stream().map(HotelMapper::convertEntityToAdminDto).toList();
    }

    public void updateHotelVerificationStatus(Long hotelId, VerificationStatus verificationStatus) {
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        //allow approval or reject only
        if(verificationStatus==null||verificationStatus==VerificationStatus.PENDING){
            throw new InvalidCallException("Select approve or reject");
        }

        //update hotel
        hotel.setVerificationStatus(verificationStatus);

        //save
        hotelRepository.save(hotel);
    }
}
