package com.mario.authservice.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String authority = "ROLE_USER";

    @ManyToMany
    @JoinTable(
            name = "role_users",
            joinColumns = @JoinColumn(name = "authorities_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<User> users;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
