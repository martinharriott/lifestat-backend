package com.nafrospec.lifestat.Model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
public class GoalRequest {
    private String name;
    private String description;
    private String statName;
    private int statValue;
    private String dueDate;
}
