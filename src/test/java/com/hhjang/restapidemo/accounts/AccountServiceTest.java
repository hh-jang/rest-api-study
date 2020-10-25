package com.hhjang.restapidemo.accounts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService service;

    @Autowired
    AccountRepository repository;

    @Test
    public void findByUsername() {
        // Given
        String username = "hh-jang@gmail.com";
        String password = "hh-jang1";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.repository.save(account);

        // When
        UserDetailsService userDetailsService = service;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test()
    public void findByUsernameFail() {
        // Given
        String notExistUsername = "notExistUsername@gmail.com";

        // When && Then
        assertThatThrownBy(() -> {
            service.loadUserByUsername(notExistUsername);
        })
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(notExistUsername);
    }
}