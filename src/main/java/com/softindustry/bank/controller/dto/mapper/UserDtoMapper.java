package com.softindustry.bank.controller.dto.mapper;

import com.softindustry.bank.controller.dto.UserDto;
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
                .city(from.getAddress().getCity())
                .street(from.getAddress().getStreet())
                .houseNumber(from.getAddress().getHouseNumber())
                .build();
    }
}
