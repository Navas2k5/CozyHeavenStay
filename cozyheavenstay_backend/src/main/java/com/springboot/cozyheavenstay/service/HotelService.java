package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.HotelRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.HotelMapper;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final OwnerRepository ownerRepository;

    @Transactional
    public void add(String ownerUsername, HotelRequestDto hotelRequestDto){
        Owner owner= ownerRepository.getByUsername(ownerUsername);

        if(owner==null)
            throw new ResourceNotFoundException("Owner does not exist");

        Hotels hotel = HotelMapper.convertDtoToEntity(hotelRequestDto);
        hotel.setVerificationStatus(VerificationStatus.PENDING);
        hotel.setHotelStatus(HotelStatus.ACTIVE);

        hotel.setOwner(owner);

        hotelRepository.save(hotel);
    }

    public List<HotelResponseDto> getHotelByOwnerUsername(String ownerUsername) {
        List<Hotels> hotels = hotelRepository.getHotelByOwnerUsername(ownerUsername);

        return hotels.stream().map(HotelMapper::convertEntityToDto).toList();
    }

    public void updateHotel(Long hotelId, @Valid HotelRequestDto hotelRequestDto, String ownerUsername) {
        //get hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel Not found"));

        //check ownership
        if(!hotel.getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("U are not allowed to update this hotel");

        HotelMapper.updateEntityFromDto(hotelRequestDto,hotel);
        hotel.setVerificationStatus(VerificationStatus.PENDING);
        hotelRepository.save(hotel);

    }

    public List<CustomerHotelResponseDto> getAllHotels(Integer page, Integer size) {

        //validate pagination
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page must be 0 or above and size must be between 1 and 100");

        //prepare pagination
        Pageable pageable= PageRequest.of(page,size);

        //fetch approved and active hotels
        List<Hotels> hotels = hotelRepository.getAvailableHotels(VerificationStatus.APPROVED,HotelStatus.ACTIVE,pageable);

        //convert to customerHotel dto
        return hotels.stream().map(HotelMapper::convertEntityToCustomerHotelResponseDto).toList();
    }

    public CustomerHotelResponseDto getHotelById(Long hotelId) {
        //get hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel Not Found"));

        //check whether it is approved and active
        if(hotel.getVerificationStatus()!=VerificationStatus.APPROVED || hotel.getHotelStatus()!=HotelStatus.ACTIVE)
            throw new ResourceNotFoundException("Hotel not found");

        //return hotels
        return HotelMapper.convertEntityToCustomerHotelResponseDto(hotel);
    }
}
