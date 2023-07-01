package com.nafrospec.lifestat;

import com.nafrospec.lifestat.Model.Stat;
import com.nafrospec.lifestat.Model.User;
import com.nafrospec.lifestat.Model.response.StatResponse;
import com.nafrospec.lifestat.db.StatRepository;
import com.nafrospec.lifestat.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/stat", produces = "application/json")
public class StatsController {
    @Autowired
    private StatRepository statRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping(value = "")
    public ResponseEntity getUserStats(@AuthenticationPrincipal User user) {
        List<StatResponse> res = new ArrayList<>();
        final var stats = statRepository.findByUserId(user.getId());
        for (final var stat : stats) {
            res.add(new StatResponse(stat.getName(), stat.getDisplayValue()));
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody List<Stat> getStatsByUserId(@PathVariable(required = false) String id) {
        return statRepository.findByUserId(Integer.parseInt(id));
    }

    @PostMapping(value = "/calculate")
    public ResponseEntity calculateStats(@AuthenticationPrincipal User user, @RequestBody Map<String, String> data) {
        final var knowledge = Integer.parseInt(data.get("knowledge"));
        final var strength = Integer.parseInt(data.get("strength"));
        final var resources = Integer.parseInt(data.get("resources"));
        final var health = Integer.parseInt(data.get("health"));
        final var charisma = Integer.parseInt(data.get("charisma"));

        final var expectedKnowledge = Integer.parseInt(data.get("knowledgeGoal"));
        final var expectedStrength = Integer.parseInt(data.get("strengthGoal"));
        final var expectedResources = Integer.parseInt(data.get("resourcesGoal"));
        final var expectedHealth = Integer.parseInt(data.get("healthGoal"));
        final var expectedCharisma = Integer.parseInt(data.get("charismaGoal"));

        final Stat knowledgeStat = new Stat("Knowledge", knowledge * 10, expectedKnowledge * 10, user);
        final Stat strengthStat = new Stat("Strength", strength * 10, expectedStrength * 10, user);
        final Stat resourcesStat = new Stat("Resources", resources * 10, expectedResources * 10, user);
        final Stat healthStat = new Stat("Health", health * 10, expectedHealth * 10, user);
        final Stat charismaStat = new Stat("Charisma", charisma * 10, expectedCharisma * 10, user);

        List<Stat> stats = new ArrayList<>();
        stats.add(knowledgeStat);
        stats.add(strengthStat);
        stats.add(resourcesStat);
        stats.add(healthStat);
        stats.add(charismaStat);

        List<Stat> currStats = statRepository.findByUserId(user.getId());

        for (Stat stat : currStats) {
            statRepository.delete(stat);
        }
        user.setStats(stats);
        for (Stat stat : user.getStats()) {
            statRepository.save(stat);
        }

        user.setHasSetup(true);
        userRepository.save(user);

        return ResponseEntity.ok(new HashMap<>());
    }
}
