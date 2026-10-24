package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.HotelRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotelController {


    private final HotelService hotelService;

    @PostMapping("/api/hotel/add")
    public void addHotel(@Valid @RequestBody HotelRequestDto hotelRequestDto,
                         Principal principal){
        hotelService.add(principal.getName(),hotelRequestDto);
    }

    @PutMapping("/api/hotel/{hotelId}/update")
    public void updateHotel(@PathVariable Long hotelId,
                            @Valid @RequestBody HotelRequestDto hotelRequestDto,
                            Principal principal){
        hotelService.updateHotel(hotelId,hotelRequestDto,principal.getName());
    }

    @GetMapping("/api/hotel/all")
    public List<CustomerHotelResponseDto> getAllHotels(@RequestParam(name = "location",required = false) String location,
                                                       @RequestParam(name = "hotelName", required = false) String hotelName,
                                                       @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
                                                       @RequestParam(name = "size",required = false,defaultValue = "10") Integer size){
        return hotelService.getAllHotels(location,hotelName,page,size);
    }

    @GetMapping("/api/hotel/{hotelId}")
    public CustomerHotelResponseDto getHotelById(@PathVariable Long hotelId){
        return hotelService.getHotelById(hotelId);
    }


}
