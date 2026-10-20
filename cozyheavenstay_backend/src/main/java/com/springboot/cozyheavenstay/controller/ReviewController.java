package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.ReviewRequestDto;
import com.springboot.cozyheavenstay.dto.response.ReviewResponseDto;
import com.springboot.cozyheavenstay.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/review/booking/{bookingId}/add")
    public ReviewResponseDto addReview(@PathVariable Long bookingId,
                                       @Valid @RequestBody ReviewRequestDto reviewRequestDto,
                                       Principal principal){
        return reviewService.addReview(bookingId,reviewRequestDto, principal.getName());
    }
}
