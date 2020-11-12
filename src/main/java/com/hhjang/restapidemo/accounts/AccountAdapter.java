package com.hhjang.restapidemo.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hhjang.restapidemo.accounts.AccountService.ROLE_PREFIX;

public class AccountAdapter extends User {

    private Account account;

    public AccountAdapter(Account account) {
        super(account.getEmail(), account.getPassword(), authrities(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authrities(Set<AccountRole> roles) {
        return roles.stream()
                .map(accountRole -> new SimpleGrantedAuthority(ROLE_PREFIX + accountRole.name()))
                .collect(Collectors.toSet());
    }

    public Account getAccount() {
        return account;
    }
}
