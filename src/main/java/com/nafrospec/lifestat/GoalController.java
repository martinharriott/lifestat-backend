package com.nafrospec.lifestat;

import com.nafrospec.lifestat.Model.Goal;
import com.nafrospec.lifestat.Model.Stat;
import com.nafrospec.lifestat.Model.User;
import com.nafrospec.lifestat.Model.request.GoalRequest;
import com.nafrospec.lifestat.db.GoalRepository;
import com.nafrospec.lifestat.db.StatRepository;
import com.nafrospec.lifestat.db.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(path = "/goal", produces = "application/json")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatRepository statRepository;

    @GetMapping("")
    public @ResponseBody List<Goal> getUserGoals(@RequestParam(required = false) Optional<Boolean> completed, @AuthenticationPrincipal User user) {
        List<Goal> goals;
        if (completed.isPresent()) {
            goals = goalRepository.findByUserIdAndCompleted(user.getId(), completed.get());
        }
        else goals = goalRepository.findByUserId(user.getId());

        for (Goal goal : goals) {
            Stat stat = statRepository.findByNameAndUserId(goal.getStatName(), user.getId());
            if (stat != null) goal.setStatValue((int) Math.floor(100 * ((float) goal.getStatValue() / stat.getTarget())));
        }
        return goals;
    }

    @GetMapping("/{id}")
    public @ResponseBody List<Goal> getGoalsByUserId(@PathVariable(required = false) String id, @RequestParam(required = false) boolean completed) {
        if (!completed) {
            return goalRepository.findByUserIdAndCompleted(Integer.parseInt(id), false);
        }
        return goalRepository.findByUserId(Integer.parseInt(id));
    }


    @PutMapping("")
    public ResponseEntity updateGoal(@AuthenticationPrincipal User user, @RequestBody Map<String, String> data) {
        final var id = Integer.valueOf(data.get("id"));
        final var name = data.get("name");
        final var description = data.get("description");
        final var statName = data.get("statName");
        final var dueDate = ZonedDateTime.parse(data.get("dueDate"), DateTimeFormatter.ISO_DATE_TIME);
        var statValue = Integer.parseInt(data.get("statValue"));

        List<Stat> currStats = statRepository.findByUserId(user.getId());

        Stat stat = statRepository.findByNameAndUserId(statName, user.getId());

        if (stat != null) statValue = Math.round(statValue / (float) 100 * stat.getTarget());

        Goal goal = new Goal(id, name, description, user, false, statName, statValue, dueDate);
        goalRepository.save(goal);
        goal.setStatValue(Integer.parseInt(data.get("statValue")));
        return ResponseEntity.ok(goal);
    }

    @PostMapping(consumes = "application/json")
    public @ResponseBody Goal createGoal(@RequestBody Map<String, String> data, @AuthenticationPrincipal User user) {
        System.out.println(data.get("name") + " " + data);

        Stat stat = statRepository.findByNameAndUserId(data.get("statName"), user.getId());
        List<Stat> currStats = statRepository.findByUserId(user.getId());

        int statValue = Integer.parseInt(data.get("statValue"));
        if (stat != null) statValue = Math.round(statValue / (float) 100 * stat.getTarget());

        ZonedDateTime dueDate = ZonedDateTime.parse(data.get("dueDate"), DateTimeFormatter.ISO_DATE_TIME);

        Goal goal = new Goal(data.get("name"), data.get("description"), user, false, data.get("statName"), statValue, dueDate);

        goalRepository.save(goal);
        goal.setStatValue(Integer.parseInt(data.get("statValue")));
        return goal;
    }

    @PostMapping(path = "/complete", consumes = "application/json")
    public @ResponseBody Stat completeGoal(@RequestBody Map<String, String> data) {
        if (data.containsKey("id")) {
            int id = Integer.parseInt(data.get("id"));
            Goal goal = goalRepository.findById(id).get();
            goal.setCompleted(true);

            Stat stat = statRepository.findByNameAndUserId(goal.getStatName(), goal.getUsersId());
            stat.setValue(stat.getValue() + goal.getStatValue());
            statRepository.save(stat);
            goalRepository.save(goal);
            return stat;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
    }

    @DeleteMapping(path = "", consumes = "application/json")
    public ResponseEntity deleteGoal(@RequestBody Map<String, String> data) {
        if (data.containsKey("id")) {
            int id = Integer.parseInt(data.get("id"));
            goalRepository.deleteById(id);
            return ResponseEntity.ok().body(new HashMap<>());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
    }
}
