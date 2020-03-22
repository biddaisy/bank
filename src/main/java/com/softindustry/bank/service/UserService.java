package com.softindustry.bank.service;

import com.softindustry.bank.entity.User;
import com.softindustry.bank.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findUserById(Long userId) throws UserNotFoundException;
    User findUserByEmail(String email) throws UserNotFoundException;
}
