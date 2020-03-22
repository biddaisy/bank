package com.softindustry.bank.controller;

import com.softindustry.bank.controller.dto.UserDto;
import com.softindustry.bank.controller.dto.mapper.DtoMapper;
import com.softindustry.bank.controller.request.AuthenticationRequest;
import com.softindustry.bank.controller.response.JwtResponse;
import com.softindustry.bank.entity.User;
import com.softindustry.bank.service.UserService;
import com.softindustry.bank.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final DtoMapper<User, UserDto> userMapper;

    @Autowired
    public UserController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          UserService userService,
                          DtoMapper<User, UserDto> userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(value = "/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final User userDetails = (User) userService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token, userDetails.getId()));
    }

    @GetMapping("api/user/{id}")
    public ResponseEntity<UserDto> getUserInfoById(@PathVariable(name = "id") Long userId) {
        ResponseEntity<UserDto> resultEntity;
        try {
            resultEntity = ResponseEntity.ok(userMapper.map(userService.findUserById(userId)));
        } catch (Exception e) {
            log.error("Error occurred while getting user {}", e);
            resultEntity = ResponseEntity.badRequest().build();
        }
        return resultEntity;
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
