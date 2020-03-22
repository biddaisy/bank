package com.softindustry.bank;

import com.softindustry.bank.entity.User;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class UserControllerTest {

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
    public void authorizationWithExistUser_should_returnStatusOk() throws Exception {
        mvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"test@gmail.com\", \"password\": 123456 }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void authorizationWithFakeUser_should_returnUnauthorizedStatus() throws Exception {
        mvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"fake@gmail.com\", \"password\": 312314 }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserInfoByUserId_should_statusOk_sameIdAndEmail() throws Exception{
        mvc.perform(get("/api/user/" + user.getId())
                .header("authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

}
