package com.softindustry.bank.dto.mapper;

import com.softindustry.bank.dto.UserDto;
import com.softindustry.bank.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements DtoMapper<User, UserDto> {

    @Override
    public UserDto map(User from) {
        return UserDto.builder()
                .id(from.getId())
                .email(from.getEmail())
                .firstName(from.getFirstName())
                .lastName(from.getLastName())
                .build();
    }
}
