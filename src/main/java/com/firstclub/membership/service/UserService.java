package com.firstclub.membership.service;

import com.firstclub.membership.dto.CreateUserRequest;
import com.firstclub.membership.dto.UserResponse;
import com.firstclub.membership.entity.MembershipActivity;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.BusinessRuleException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.UserMapper;
import com.firstclub.membership.repository.MembershipActivityRepository;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MembershipActivityRepository activityRepository;
    private final Clock clock;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already registered: " + request.email());
        }
        User user = userRepository.save(new User(request.name(), request.email(), request.cohort()));
        activityRepository.save(new MembershipActivity(user, LocalDate.now(clock)));
        return UserMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserMapper.toResponse(getEntity(id));
    }

    /** Loads the user or fails with 404. Shared by other services that need a managed User. */
    @Transactional(readOnly = true)
    public User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
