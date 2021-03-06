package com.softindustry.bank.service.impl;

import com.softindustry.bank.entity.User;
import com.softindustry.bank.exception.UserNotFoundException;
import com.softindustry.bank.repository.UserRepository;
import com.softindustry.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(s).orElseThrow(() -> new UsernameNotFoundException(s));
    }

    @Override
    @Transactional
    public User findUserById(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserById(userId);
        return user.orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));
    }

    @Override
    @Transactional
    public User findUserByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email);
        return user.orElseThrow(() -> new UserNotFoundException("User with email:" + email + " not found"));
    }
}
