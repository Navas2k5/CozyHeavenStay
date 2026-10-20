package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews,Long> {

    @Query("""
            select count(r.id) from Reviews r
            where r.booking.id=?1 
            """)
    long countByBookingId(Long bookingId);
}
