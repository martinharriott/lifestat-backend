package com.nafrospec.lifestat.db;

import com.nafrospec.lifestat.Model.Stat;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatRepository extends CrudRepository<Stat, Integer> {
    @Override
    List<Stat> findAll();

    List<Stat> findByUserId(Integer id);

    Stat findByNameAndUserId(String name, Integer id);
}
