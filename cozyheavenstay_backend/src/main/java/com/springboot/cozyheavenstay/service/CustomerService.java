package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.mapper.CustomerMapper;
import com.springboot.cozyheavenstay.model.Customer;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.CustomerRepository;
import com.springboot.cozyheavenstay.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addCustomer(CustomerRequestDto customerRequestDto) {

        Customer customer = CustomerMapper.mapDtoToEntity(customerRequestDto);

        //create User object

        User user = new User();
        user.setUsername(customerRequestDto.username());
        user.setPassword(passwordEncoder.encode(customerRequestDto.password()));
        user.setRole(Role.CUSTOMER);
        user = userRepository.save(user);

        customer.setUser(user);

        customerRepository.save(customer);
    }
}
