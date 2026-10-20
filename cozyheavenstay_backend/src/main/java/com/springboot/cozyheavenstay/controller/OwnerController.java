package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.OwnerRequestDto;
import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.OwnerBookingResponseDto;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.service.HotelService;
import com.springboot.cozyheavenstay.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final HotelService hotelService;

    @PostMapping("/api/owner/add")
    public void addOwner(@Valid @RequestBody OwnerRequestDto ownerRequestDto){
        ownerService.addOwner(ownerRequestDto);
    }

    @GetMapping("/api/owner/hotel/all")
    public List<HotelResponseDto> getHotelByOwnerUsername(Principal principal){
        return hotelService.getHotelByOwnerUsername(principal.getName());
    }

    @PutMapping("/api/owner/hotel/status/{hotelId}")
    public void updateHotelStatus(@PathVariable Long hotelId,
                                  @RequestParam HotelStatus hotelStatus,
                                  Principal principal){
        ownerService.updateHotelStatus(hotelId,hotelStatus,principal.getName());
    }

    @GetMapping("/api/owner/hotel/{hotelId}/bookings")
    public List<OwnerBookingResponseDto> getHotelBookings(@PathVariable Long hotelId,
                                                          Principal principal,
                                                          @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
                                                          @RequestParam(name = "size",required = false,defaultValue = "20") Integer size){
        return ownerService.getHotelBookings(hotelId, principal.getName(),page,size);
    }

    @PutMapping("/api/owner/booking/{bookingId}/complete")
    public BookingResponseDto completeBooking(@PathVariable Long bookingId,
                                              Principal principal){
        return ownerService.completeBooking(bookingId, principal.getName());
    }


}
