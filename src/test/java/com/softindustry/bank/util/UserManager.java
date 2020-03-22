package com.softindustry.bank.util;


import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.Address;
import com.softindustry.bank.entity.User;
import com.softindustry.bank.entity.enums.AccountStatus;
import com.softindustry.bank.entity.enums.Currency;
import com.softindustry.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class UserManager {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(userRepository.findUserById(user.getId()).get());
    }

    public User createAndSaveUserWithAccounts(){
        User user = createAndGetUser();
        user.setAddress(createAndGetAddress(user));
        user.setAccounts(createAndGetListOfAccount(user));
        return  userRepository.save(user);
    }

    private User createAndGetUser(){
        return User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("test@gmail.com")
                .password("$2a$10$HaygCzWGqc27M8E3mESJ/.yuIodx2wTERzk1wTzw7QzEWrkX3lvvi")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private Address createAndGetAddress(User user) {
        return Address.builder()
                .city("Kyiv")
                .street("Shevchenka")
                .user(user)
                .houseNumber(2)
                .build();
    }

    private List<Account> createAndGetListOfAccount(User user) {
        return Arrays.asList(
                createAndGetAccount(user, Currency.USD, AccountStatus.BLOCKED),
                createAndGetAccount(user, Currency.UAH, AccountStatus.ACTIVE)
        );
    }

    private Account createAndGetAccount(User user, Currency currency, AccountStatus accountStatus) {
        return Account.builder()
                .user(user)
                .balance(new BigDecimal("100"))
                .currency(currency)
                .status(accountStatus)
                .build();
    }
}
