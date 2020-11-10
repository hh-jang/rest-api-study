package com.hhjang.restapidemo.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
