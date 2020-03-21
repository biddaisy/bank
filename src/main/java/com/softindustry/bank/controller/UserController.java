package com.softindustry.bank.controller;

import com.softindustry.bank.dto.UserDto;
import com.softindustry.bank.dto.mapper.DtoMapper;
import com.softindustry.bank.entity.User;
import com.softindustry.bank.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;
    private final DtoMapper<User, UserDto> userMapper;

    @Autowired
    public UserController(UserService userService,
                          DtoMapper<User, UserDto> userMapper) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("api/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long userId) {
        ResponseEntity<UserDto> resultEntity;
        try {
            resultEntity = ResponseEntity.ok(userMapper.map(userService.findUserById(userId)));
        } catch (Exception e) {
            log.error("Error occurred while getting user {}", e);
            resultEntity = ResponseEntity.badRequest().build();
        }
        return resultEntity;
    }
}
