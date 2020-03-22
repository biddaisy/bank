package com.softindustry.bank.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String city;
    private String street;
    private Integer houseNumber;
}
