package com.nafrospec.lifestat.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="stat")
@NoArgsConstructor
public class Stat {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Getter
    private String name;
    @Getter
    @Setter
    private int value;

    @Getter
    @Setter
    private int target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Stat(final String name, final int value, final int target, final User user) {
        this.name = name;
        this.value = value;
        this.target = target;
        this.user = user;
    }

    public int getDisplayValue() {
        return (int) Math.floor(100 * ((float) this.getValue() / this.getTarget()));
    }
}
