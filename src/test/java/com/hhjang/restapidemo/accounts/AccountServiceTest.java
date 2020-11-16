package com.hhjang.restapidemo.accounts;

import com.hhjang.restapidemo.MockMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountServiceTest extends MockMvcTest {

    @Autowired
    AccountService service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저가 정상적으로 저장되는지 확인하는 코드")
    public void findByUsername() {
        // Given
        String username = "hh-jang@gmail.com";
        String password = "hh-jang1";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.service.saveAccount(account);

        // When
        UserDetailsService userDetailsService = service;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 유저를 검색 시 UsernameNotFoundException 발생을 확인하는 코드")
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