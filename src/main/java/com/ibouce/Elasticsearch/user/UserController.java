package com.ibouce.Elasticsearch.user;

import com.ibouce.Elasticsearch.jwt.model.JwtResponse;
import com.ibouce.Elasticsearch.user.Models.LoginModel;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    //@CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<LoginModel> login(@RequestBody UserModel user) {
        return ResponseEntity.ok(userService.loginUser(user));
    }

    @GetMapping("/login/{userId}")
    public ResponseEntity<Optional<UserModel>> findUser(@PathVariable Long userId) {
        Optional<UserModel> user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> findAllUsers() {
        List<UserModel> users = userService.findAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/save")
    public ResponseEntity<JwtResponse> saveUser(@RequestBody UserModel user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user));
    }

    @PutMapping("/users/update")
    public ResponseEntity<UserModel> updateUser(@RequestBody UserModel user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/search/{query}")
    public ResponseEntity<UserModel> searchUser(@PathVariable("query") String query) {
        UserModel user = userService.findUserByUsername(query);
        return ResponseEntity.ok(user);
    }

    //**************************************************************************************

     /*@PostMapping("/users/register")
    public ResponseEntity<JwtResponse> register(@RequestBody UserModel user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user));
    }*/

    /*@PostMapping("/users/login")
    public ResponseEntity<UserModel> login(@RequestBody UserModel loginRequest) {
        UserModel user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }*/

    /*@GetMapping("/users/{userId}")
    public ResponseEntity<Optional<UserModel>> findUserById(@PathVariable Long userId) {
        Optional<UserModel> user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }*/

}