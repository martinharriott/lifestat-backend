package com.nafrospec.lifestat;

import com.nafrospec.lifestat.Model.Stat;
import com.nafrospec.lifestat.Model.User;
import com.nafrospec.lifestat.Model.request.AuthRequest;
import com.nafrospec.lifestat.Model.response.LoginResponse;
import com.nafrospec.lifestat.Security.JWTGenerator;
import com.nafrospec.lifestat.db.StatRepository;
import com.nafrospec.lifestat.db.UserRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
@RequestMapping(value = "auth", produces = "application/json")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatRepository statRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        Map<String, String> body = new HashMap<>();
        body.put("ping", "pinged");
        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/validate", consumes = "application/json")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        Boolean isTokenValid = jwtGenerator.validateToken(token);
        if (!isTokenValid) return ResponseEntity.badRequest().body(new HashMap<>());

        final var name = jwtGenerator.getUsernameFromJWT(token);
        final var user = userRepository.getByName(name);

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("hasSetup", ""+user.get().isHasSetup());

        return ResponseEntity.ok(body);
    }
//
    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {

        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUserName(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user=(User)authentication.getPrincipal();
        String jwtToken = jwtGenerator.generateToken(authentication);

        LoginResponse response = new LoginResponse();
        response.setToken(jwtToken);

        Map<String, String> body = new HashMap<>();
        body.put("token", jwtToken);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtToken).body(body);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> addNewUser(@RequestBody AuthRequest authRequest) {
        Map<String, String> body = new HashMap<>();
        if (userRepository.existsByName(authRequest.getUserName())) {
            System.out.println("bad request");
            body.put("response", "bad request");
            return ResponseEntity.badRequest().body(body);
        }

        User user = new User(authRequest.getUserName(), passwordEncoder.encode(authRequest.getPassword()));

        userRepository.save(user);
        for (Stat stat : user.getStats()) {
            statRepository.save(stat);
        }

        body.put("name", authRequest.getUserName());

        return ResponseEntity.ok(body);
    }
}
