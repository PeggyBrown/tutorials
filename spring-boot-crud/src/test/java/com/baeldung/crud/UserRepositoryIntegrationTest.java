package com.baeldung.crud;

import com.baeldung.crud.entities.User;
import com.baeldung.crud.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@AutoConfigureMockMvc
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnUser() {
        User user = new User("Julie", "julie@domain.com");
        entityManager.persistAndFlush(user);

        List<User> found = userRepository.findByName(user.getName());
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void whenInvalidName_thenReturnEmptyList() {
        List<User> fromDb = userRepository.findByName("doesNotExist");
        assertThat(fromDb).isEmpty();
    }

    @Test
    public void whenFindById_thenReturnUser() {
        User emp = new User("test", "test@tst.com");
        entityManager.persistAndFlush(emp);

        User fromDb = userRepository.findById(emp.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo(emp.getName());
    }

    @Test
    public void whenInvalidId_thenReturnNull() {
        User fromDb = userRepository.findById(-11L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    public void givenSetOfUsers_whenFindAll_thenReturnAllUsers() {
        User alex = new User("alex", "julie@domain.com");
        User ron = new User("ron", "ron@domain.com");
        User bob = new User("bob", "bob@domain.com");

        entityManager.persist(alex);
        entityManager.persist(bob);
        entityManager.persist(ron);
        entityManager.flush();

        List<User> allUsers = (List<User>) userRepository.findAll();

        assertThat(allUsers).hasSize(3).extracting(User::getName).containsOnly(alex.getName(), ron.getName(), bob.getName());
    }
}
