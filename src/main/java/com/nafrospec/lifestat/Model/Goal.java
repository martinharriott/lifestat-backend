package com.nafrospec.lifestat.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="goal")
@NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Integer id;

    @Getter
    private String description;

    @Getter
    private String name;

    @Getter
    @Setter
    private boolean completed;

    @Getter
    private ZonedDateTime dateCreated;

    private String dateCreatedString;

    @Getter
    private ZonedDateTime dueDate;

    private String dueDateString;

    @Getter
    private String statName;

    @Getter
    @Setter
    private int statValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Goal(final String name, final String description, final User user, final boolean completed, final String statName, final int statValue, final ZonedDateTime dueDate) {
        this.name = name;
        this.description = description;
        this.completed = completed;
        this.user = user;
        this.statName = statName;
        this.statValue = statValue;
        this.dateCreated = ZonedDateTime.now();
        this.dueDate = dueDate;
    }

    public Goal(final Integer id, final String name, final String description, final User user, final boolean completed, final String statName, final int statValue, final ZonedDateTime dueDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.completed = completed;
        this.user = user;
        this.statName = statName;
        this.statValue = statValue;
        this.dateCreated = ZonedDateTime.now();
        this.dueDate = dueDate;
    }

    public Integer getUsersId() {
        return user.getId();
    }

    public String getDueDateString() {
        return dueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public String getDateCreatedString() {
        return dateCreated.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
