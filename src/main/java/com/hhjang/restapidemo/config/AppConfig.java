package com.hhjang.restapidemo.config;

import com.hhjang.restapidemo.accounts.Account;
import com.hhjang.restapidemo.accounts.AccountRole;
import com.hhjang.restapidemo.accounts.AccountService;
import com.hhjang.restapidemo.common.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner run() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account admin = Account.builder()
                        .email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
                        .roles(Set.of(AccountRole.ADMIN))
                        .build();

                accountService.saveAccount(admin);

                Account user = Account.builder()
                        .email(appProperties.getGeneralUsername())
                        .password(appProperties.getGeneralUserPassword())
                        .roles(Set.of(AccountRole.USER))
                        .build();

                accountService.saveAccount(user);
            }
        };
    }
}
