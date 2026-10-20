package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.controller.ReviewMapper;
import com.springboot.cozyheavenstay.dto.request.ReviewRequestDto;
import com.springboot.cozyheavenstay.dto.response.ReviewResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Reviews;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewResponseDto addReview(Long bookingId, ReviewRequestDto dto,String customerUsername){
        //find booking
        Bookings booking= bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found"));

        //check ownership
        if(!booking.getCustomer().getUser().getUsername().equals(customerUsername))
            throw new OwnerShipException("You can review only your own bookings");

        //allow review only for completed stays
        if(!booking.getBookingStatus().equals(BookingStatus.COMPLETED))
            throw new InvalidCallException("You can only review for completed bookings");

        //map request and attach booking
        Reviews review= ReviewMapper.convertDtoToEntity(dto);
        review.setBooking(booking);
        review.setCreatedAt(Instant.now());
        reviewRepository.save(review);

        return ReviewMapper.convertEntityToDto(review);


    }
}
