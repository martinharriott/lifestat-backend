package com.nafrospec.lifestat.db;

import com.nafrospec.lifestat.Model.Goal;
import com.nafrospec.lifestat.Model.Stat;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalRepository extends CrudRepository<Goal, Integer> {
    List<Goal> findByUserId(Integer id);
    List<Goal> findByUserIdAndCompleted(Integer id, boolean completed);
}
