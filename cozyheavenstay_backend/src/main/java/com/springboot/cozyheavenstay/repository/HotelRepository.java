package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.model.Hotels;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotels,Long> {

    @Query("""
            select h from Hotels h
            join h.owner o
            join o.user u
            where u.username=?1
            """)
    List<Hotels> getHotelByOwnerUsername(String ownerUsername);

    @Query("""
            select h from Hotels h
            join h.owner o
            where h.verificationStatus=?1
            """)
    List<Hotels> getAllPendingHotels(VerificationStatus verificationStatus);

    @Query("""
        select h from Hotels h
        where h.verificationStatus = ?1
        AND h.hotelStatus = ?2
        AND (?3 IS NULL OR h.location = ?3)
        AND (?4 IS NULL OR h.hotelName = ?4)
        order by h.id desc
        """)
    List<Hotels> getAvailableHotels(
            VerificationStatus verificationStatus,
            HotelStatus hotelStatus,
            String location,
            String hotelName,
            Pageable pageable
    );
}
