package com.softindustry.bank.dto.mapper;

public interface DtoMapper<F, T> {
    T map(F from);
}
