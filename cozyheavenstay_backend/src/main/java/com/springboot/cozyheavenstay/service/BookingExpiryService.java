package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BookingExpiryService {

    private final BookingRepository bookingRepository;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expirePendingBookings(){
        bookingRepository.expirePendingBookings(BookingStatus.CANCELLED,BookingStatus.PENDING, Instant.now());
    }
}
