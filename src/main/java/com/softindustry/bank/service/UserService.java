package com.softindustry.bank.service;

import com.softindustry.bank.entity.User;
import com.softindustry.bank.exception.UserNotFoundException;

public interface UserService {

    User findUserById(Long userId) throws UserNotFoundException;
    User findUserByEmail(String email) throws UserNotFoundException;
}
