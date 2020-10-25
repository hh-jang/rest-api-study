package com.hhjang.restapidemo.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    public static final String ROLE_PREFIX = "ROLE_";

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(account.getEmail(), account.getPassword(), authoritied(account.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authoritied(Set<AccountRole> roles) {
        return roles.stream()
                .map(accountRole -> new SimpleGrantedAuthority(ROLE_PREFIX + accountRole.name()))
                .collect(Collectors.toSet());
    }
}
