package com.softindustry.bank;

import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.User;
import com.softindustry.bank.entity.enums.AccountStatus;
import com.softindustry.bank.util.JwtTokenUtil;
import com.softindustry.bank.util.UserManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class AccountControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserManager userManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private MockMvc mvc;

    private User user;
    private String token;

    @Before
    public void setup() {
        setupMvc();
        user = userManager.createAndSaveUserWithAccounts();
        setupToken();
    }

    @After
    public void destroy(){
        userManager.deleteUser(user);
        mvc = null;
    }

    private void setupMvc() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private void setupToken() {
        token = jwtTokenUtil.generateToken(user);
    }

    @Test
    public void getAccountById_should_statusOk_sameIdAndCurrency() throws Exception{
        Account accountForTest = user.getAccounts().get(0);
        mvc.perform(get("/api/accounts/" + accountForTest.getId())
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(accountForTest.getId()))
                .andExpect(jsonPath("$.currency").value(accountForTest.getCurrency().toString()));
    }

    @Test
    public void getAllAccountsByUserId_should_statusOk_sameId() throws Exception{
        mvc.perform(get("/api/accounts")
                .header("authorization", token)
                .param("userId", user.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountId").value(user.getAccounts().get(0).getId()))
                .andExpect(jsonPath("$[1].accountId").value(user.getAccounts().get(1).getId()));
    }

    @Test
    public void withdrawWithCorrectAmountAndNotBlockStatus_should_statusOk_andUpdatedBalance() throws Exception {
        Account nonBlockAccount = user.getAccounts().stream()
                .filter(account -> !AccountStatus.BLOCKED.equals(account.getStatus()) &&
                        !AccountStatus.CLOSED.equals(account.getStatus()))
                .findFirst().get();
        final BigDecimal amount = new BigDecimal("10.5");
        mvc.perform(post("/api/accounts/"+ nonBlockAccount.getId() +"/withdraw")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": " + amount.toString() + " }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(nonBlockAccount.getBalance().subtract(amount)));
    }

    @Test
    public void withdrawWithCorrectAmountAndBlockStatus_should_status4xx() throws Exception {
        Account nonBlockAccount = user.getAccounts().stream()
                .filter(account -> AccountStatus.BLOCKED.equals(account.getStatus()) ||
                        AccountStatus.CLOSED.equals(account.getStatus()))
                .findFirst().get();
        final BigDecimal amount = new BigDecimal("10.5");
        mvc.perform(post("/api/accounts/"+ nonBlockAccount.getId() +"/withdraw")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": " + amount.toString() + " }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void withdrawWithWrongAmountAndNonBlockStatus_should_status4xx() throws Exception {
        Account nonBlockAccount = user.getAccounts().stream()
                .filter(account -> !AccountStatus.BLOCKED.equals(account.getStatus()) &&
                        !AccountStatus.CLOSED.equals(account.getStatus()))
                .findFirst().get();
        final BigDecimal amount = new BigDecimal("100000000000000000");
        mvc.perform(post("/api/accounts/"+ nonBlockAccount.getId() +"/withdraw")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": " + amount.toString() + " }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void depositWithCorrectAmountAndNotBlockStatus_should_statusOk_andUpdatedBalance() throws Exception {
        Account nonBlockAccount = user.getAccounts().stream()
                .filter(account -> !AccountStatus.BLOCKED.equals(account.getStatus()) &&
                        !AccountStatus.CLOSED.equals(account.getStatus()))
                .findFirst().get();
        final BigDecimal amount = new BigDecimal("10.5");
        mvc.perform(post("/api/accounts/"+ nonBlockAccount.getId() +"/deposit")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": " + amount.toString() + " }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(nonBlockAccount.getBalance().add(amount)));
    }

    @Test
    public void depositWithCorrectAmountAndBlockStatus_should_status4xx() throws Exception {
        Account nonBlockAccount = user.getAccounts().stream()
                .filter(account -> AccountStatus.BLOCKED.equals(account.getStatus()) ||
                        AccountStatus.CLOSED.equals(account.getStatus()))
                .findFirst().get();
        final BigDecimal amount = new BigDecimal("10.5");
        mvc.perform(post("/api/accounts/"+ nonBlockAccount.getId() +"/deposit")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": " + amount.toString() + " }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void depositWithZeroAmountAndNonBlockStatus_should_status4xx() throws Exception {
        Account nonBlockAccount = user.getAccounts().stream()
                .filter(account -> !AccountStatus.BLOCKED.equals(account.getStatus()) &&
                        !AccountStatus.CLOSED.equals(account.getStatus()))
                .findFirst().get();
        final BigDecimal amount = new BigDecimal("0");
        mvc.perform(post("/api/accounts/"+ nonBlockAccount.getId() +"/deposit")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": " + amount.toString() + " }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


}
