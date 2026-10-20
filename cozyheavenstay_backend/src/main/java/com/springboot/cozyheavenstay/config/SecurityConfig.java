package com.springboot.cozyheavenstay.config;

import com.springboot.cozyheavenstay.service.MyUserDetailsService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // login API
                        .requestMatchers(HttpMethod.GET,"/api/auth/login").authenticated()
                        // Register customer, hotel owner and admin
                        .requestMatchers(HttpMethod.POST,"/api/admin/add").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/customer/add").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/owner/add").permitAll()
                        // Register hotel by owner
                        .requestMatchers(HttpMethod.POST,"/api/hotel/add").hasAuthority("OWNER")
                        // Get hotel by owner
                        .requestMatchers(HttpMethod.GET, "/api/owner/hotel/all").hasAuthority("OWNER")
                        // Get all pending verification status hotel - viewed by admin
                        .requestMatchers(HttpMethod.GET, "/api/admin/hotel/pending").hasAuthority("ADMIN")
                        // change cerification status of hotel by admin
                        .requestMatchers(HttpMethod.PUT, "/api/admin/hotel/verification/{hotelId}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/owner/hotel/status/{hotelId}").hasAuthority("OWNER")
                        // add room by owner
                        .requestMatchers(HttpMethod.POST, "/api/room/hotel/{hotelId}/add").hasAuthority("OWNER")
                        // get all room by owner
                        .requestMatchers(HttpMethod.GET, "/api/room/hotel/{hotelId}/all").hasAuthority("OWNER")
                        // change status of room by owner
                        .requestMatchers(HttpMethod.PUT, "/api/room/{roomId}/status").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/room/{roomId}/update").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/hotel/{hotelId}/update").hasAuthority("OWNER")
                        // get all approved and active hotels
                        .requestMatchers(HttpMethod.GET,"/api/hotel/all").permitAll()
                        //get hotel by id
                        .requestMatchers(HttpMethod.GET,"/api/hotel/{hotelId}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/room/hotel/{hotelId}/available").permitAll()

                        //get booking details after selecting booking for customers
                        .requestMatchers(HttpMethod.POST,"/api/booking/quote").hasAuthority("CUSTOMER")
                        //add booking
                        .requestMatchers(HttpMethod.POST,"api/booking/add").hasAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.POST,"/api/payment/booking/{bookingId}/pay").hasAuthority("CUSTOMER")
                        //get all bookings by customer
                        .requestMatchers(HttpMethod.GET,"/api/booking/my").hasAuthority("CUSTOMER")
                        //get booking details by bookingId
                        .requestMatchers(HttpMethod.GET,"/api/booking/{bookingId}").hasAuthority("CUSTOMER")
                        //cancel booking
                        .requestMatchers(HttpMethod.PUT,"/api/booking/{bookingId}/cancel").hasAuthority("CUSTOMER")
                        //refund done by owner
                        .requestMatchers(HttpMethod.PUT,"/api/payment/booking/{bookingId}/refund").hasAuthority("OWNER")
                        //get all booking details from owner
                        .requestMatchers(HttpMethod.GET,"/api/owner/hotel/{hotelId}/bookings").hasAuthority("OWNER")
                        //make booking status complete by owner
                        .requestMatchers(HttpMethod.PUT,"/api/owner/booking/{bookingId}/complete").hasAuthority("OWNER")
                        //add review after stay
                        .requestMatchers(HttpMethod.POST,"/api/review/booking/{bookingId}/add").hasAuthority("CUSTOMER")
                );


        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider getProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(getPasswordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
