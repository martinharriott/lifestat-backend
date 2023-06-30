package com.nafrospec.lifestat.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name="users", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Integer id;

    @Getter
    @Setter
    @NonNull
    private String name;
    @Getter
    @Setter
    @NonNull
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = false)
    @Getter
    @Setter
    private List<Stat> stats;

    @Getter
    @Setter
    private boolean hasSetup;

    public User (final String name, final String password) {
        this.name = name;
        this.password = password;
        stats = defaultStats();
    }

    private List<Stat> defaultStats() {
        final List<Stat> defaultStats = new ArrayList<Stat>();
        defaultStats.add(new Stat("Strength", 0, 1000, this));
        defaultStats.add(new Stat("Knowledge", 0, 1000, this));
        defaultStats.add(new Stat("Resources", 0, 1000,this));
        defaultStats.add(new Stat("Health", 0, 1000,this));
        defaultStats.add(new Stat("Charisma", 0, 1000, this));
        return defaultStats;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<GrantedAuthority>();
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
