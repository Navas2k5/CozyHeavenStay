package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.OwnerRequestDto;
import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.dto.response.BookingRoomDetailsDto;
import com.springboot.cozyheavenstay.dto.response.OwnerBookingResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.BookingMapper;
import com.springboot.cozyheavenstay.mapper.OwnerMapper;
import com.springboot.cozyheavenstay.model.*;
import com.springboot.cozyheavenstay.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomsRepository bookingRoomsRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void addOwner(OwnerRequestDto ownerRequestDto) {

        Owner owner = OwnerMapper.convertDtoToEntity(ownerRequestDto);

        //create user object
        User user = new User();
        user.setUsername(ownerRequestDto.username());
        user.setPassword(passwordEncoder.encode(ownerRequestDto.password()));
        user.setRole(Role.OWNER);
        user = userRepository.save(user);

        owner.setUser(user);
        ownerRepository.save(owner);
    }

    @Transactional
    public void updateHotelStatus(Long hotelId, HotelStatus hotelStatus, String ownerUsername) {
        // Find Hotel or else throw exception
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        // check ownership
        if(!hotel.getOwner().getUser().getUsername().equals(ownerUsername)){
            throw new OwnerShipException("Owner does not match");
        }

        // validate status
        if(hotelStatus==null){
            throw new InvalidCallException("Hotel status is required");
        }

        //update operational status
        hotel.setHotelStatus(hotelStatus);

        //save hotel
        hotelRepository.save(hotel);
    }

    public List<OwnerBookingResponseDto> getHotelBookings(Long hotelId,String ownerUsername,Integer page,Integer size){
        //validate page and size
        if(page<0 || size<1 || size>100)
            throw new InvalidCallException("Page must be 0 or above, size must be between 1 and 100");

        //find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        //check ownership
        if(!hotel.getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("You can view bookings from your own hotels only");

        //get bookings with pagination
        Pageable pageable= PageRequest.of(page,size);
        List<Bookings> bookings = bookingRepository.getBookingsByHotelId(hotelId,pageable);

        List<OwnerBookingResponseDto> response = new ArrayList<>();

        for(Bookings booking:bookings){
            List<BookingRooms> bookingRooms=bookingRoomsRepository.getRoomsByBookingId(booking.getId());
            List<BookingRoomDetailsDto> rooms = bookingRooms.stream().map(BookingMapper::convertBookingRoomToDto).toList();

            Payments payment = paymentRepository.getPaymentsByBookingId(booking.getId()).orElse(null);

            //map booking to response
            response.add(BookingMapper.convertEntityToOwnerDto(booking,rooms,payment));
        }

        return response;
    }

    @Transactional
    public BookingResponseDto completeBooking(Long bookingId,String ownerUsername){
        //find booking
        Bookings booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found"));

        //find rooms
        List<BookingRooms> bookingRooms = bookingRoomsRepository.getRoomsByBookingId(bookingId);
        if(bookingRooms.isEmpty())
            throw new InvalidCallException("No rooms found for this booking");

        //check hotel ownership
        for(BookingRooms bookingRoom:bookingRooms){
            String hotelOwnerUsername=bookingRoom.getRoom().getHotel().getOwner().getUser().getUsername();

            if(!hotelOwnerUsername.equals(ownerUsername))
                throw new OwnerShipException("You can complete only bookings for ypur own hotels");
        }

        //allow only confirmed bookings
        if(booking.getBookingStatus()!= BookingStatus.CONFIRMED)
            throw new InvalidCallException("Only confirmed bookings can be completed");

        //check whether checkout date has arrived
        LocalDate today = LocalDate.now();
        if(booking.getCheckOut().isAfter(today))
            throw new InvalidCallException("Boking can be completed only on or after the check-out date");

        //complete booking
        booking.setBookingStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        //return updated booking
        return BookingMapper.convertEntityToDto(booking);
    }
}
