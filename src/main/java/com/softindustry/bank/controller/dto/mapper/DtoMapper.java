package com.softindustry.bank.controller.dto.mapper;

public interface DtoMapper<F, T> {
    T map(F from);
}
