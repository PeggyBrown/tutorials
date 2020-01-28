package com.baeldung.crud;

import com.baeldung.crud.entities.User;
import com.baeldung.crud.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @After
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    public void whenValidInput_thenCreateUser() throws Exception {
        User bob = new User("bob", "bob@domain.com");
        this.mockMvc.perform(post("/adduser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bob)))
                .andExpect(status().isOk());

        List<User> found = (List<User>) repository.findAll();
        assertThat(found).extracting(User::getName).containsOnly("bob");
    }

    @Test
    public void whenInvalidInput_thenCreateUser() throws Exception {
        User bob = new User("bob", null);
        this.mockMvc.perform(post("/adduser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bob)));

        List<User> found = (List<User>) repository.findAll();
        assertThat(found).extracting(User::getName).doesNotContain("bob");
    }

    @Test
    public void whenUserExists_thenDelete() throws Exception {
        User alice = new User("alice", "alice@domain.com");
        repository.save(alice);

        this.mockMvc.perform(get("/delete/" + alice.getId()))
                .andExpect(status().isOk());

        List<User> found = (List<User>) repository.findAll();
        assertThat(found).extracting(User::getName).doesNotContain("alice");
    }

    public static byte[] asJsonString(final Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(obj);
    }
}

