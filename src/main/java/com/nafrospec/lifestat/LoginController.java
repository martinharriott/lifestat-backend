package com.nafrospec.lifestat;

import com.nafrospec.lifestat.Model.Stat;
import com.nafrospec.lifestat.Model.User;
import com.nafrospec.lifestat.Model.request.AuthRequest;
import com.nafrospec.lifestat.Model.response.LoginResponse;
import com.nafrospec.lifestat.db.StatRepository;
import com.nafrospec.lifestat.db.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatRepository statRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @GetMapping("/login")
//    public String loginPage(HttpSession session, Model model) {
//        model.addAttribute("name", "buddy");
//        return "login";
//    }

//    @PostMapping("/auth/login")
//    public @ResponseBody User performLogin(HttpServletRequest req, @RequestParam String username, @RequestParam String password) {
//        try {
//            req.login(username, password);
//            User user = userRepository.findByName(username).get();
//            return user;
//        }
//        catch (ServletException exception) {
//            System.out.println("bad credentials");
//            throw new ResponseStatusException(
//                    HttpStatus.UNAUTHORIZED, "Bad credentials", exception);
//        }
//    }

//    @PostMapping(value = "/auth-login", consumes = {"application/json"} )
//    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
//
//        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                authRequest.getUserName(), authRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        User user = (User) authentication.getPrincipal();
//        String jwtToken=jwtTokenProvider.generateToken(user);
//
//        LoginResponse response = new LoginResponse();
//        response.setToken(jwtToken);
//
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/")
    public String home(Model model) {
        final var name = SecurityContextHolder.getContext().getAuthentication().getName();
        final var user = userRepository.getByName(name).get();
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String password) {
        if (userRepository.existsByName(name)) {
            System.out.println("bad request");
            return registerPage();
        }

        User user = new User(name, passwordEncoder.encode(password));

        userRepository.save(user);
        for (Stat stat : user.getStats()) {
            statRepository.save(stat);
        }

        System.out.println("saved");
        return "login";
    }

    @PostMapping(value="/stats")
    public @ResponseBody String updateStats(@RequestBody User user) {
        System.out.println(user);
        return "index";
    }

    @GetMapping("/get-users")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}
