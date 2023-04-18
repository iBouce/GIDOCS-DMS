package com.ibouce.Elasticsearch.user;

import com.ibouce.Elasticsearch.folder.FolderRepository;
import com.ibouce.Elasticsearch.folder.FolderService;
import com.ibouce.Elasticsearch.jwt.model.JwtResponse;
import com.ibouce.Elasticsearch.jwt.service.JwtService;
import com.ibouce.Elasticsearch.jwt.token.TokenModel;
import com.ibouce.Elasticsearch.jwt.token.TokenRepository;
import com.ibouce.Elasticsearch.jwt.token.TokenType;
import com.ibouce.Elasticsearch.user.Models.LoginModel;
import com.ibouce.Elasticsearch.user.Models.UserGroupModel;
import com.ibouce.Elasticsearch.user.Models.UserGroupRepository;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserGroupRepository userGroupRepository;

    @Value("${directory.root}")
    private String rootDirectory;

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final FolderService folderService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> user = userRepository.findByUsername(username);
        return user.map(u -> new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), getAuthorities(u)))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(),
                getAuthority(user.get()));
    }*/

    private Set<GrantedAuthority> getAuthorities(UserModel user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        return authorities;
    }

    public List<UserModel> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id);
    }

    public JwtResponse saveUser(UserModel user) {

        // Save the user to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastActive(LocalDateTime.now());
        userRepository.save(user);

        //Generate Token
        var jwt = jwtService.generateToken(user);
        saveUserToken(user, jwt);
        return JwtResponse.builder().token(jwt).build();
    }

    public UserModel updateUser(UserModel user) {
        Optional<UserModel> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            // Save the user to the database
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setLastActive(LocalDateTime.now());
            return userRepository.save(user);
        }

        return null;
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            // Delete the folder from Database
            userRepository.deleteById(id);
        }
    }

    public UserModel findUserByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }

    public LoginModel loginUser(UserModel user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        var selectedUser = userRepository.findByUsername(user.getUsername()).orElseThrow();
        var jwt = jwtService.generateToken(selectedUser);
        revokeAllUserTokens(selectedUser);
        saveUserToken(selectedUser, jwt);
        JwtResponse.builder().token(jwt).build();
        System.out.println(jwt);

        //UserGroupModel groups = userGroupRepository.findByUserId(selectedUser.getId());

        //System.out.println("--- " + groups);

        LoginModel userDTO = new LoginModel();
        userDTO.setUser(selectedUser);
        userDTO.setToken(jwt);

        return userDTO;
    }

    private void saveUserToken(UserModel user, String jwtToken) {
        var token = TokenModel.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserModel user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /*
    public UserModel getCurrentUser(String token) throws UsernameNotFoundException {
        Optional<UserModel> userModel = userRepository.findByUsername(jwtService.extractUsername(token));
        return userModel.get();
    }

    public UserModel login(String username, String password) {
        UserModel user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null && user.isEnabled()) {
            return user;
        } else {
            return null;
        }
    }

    public JwtResponse registerUser(UserModel user) {
        UserModel savedUser = new UserModel();
        savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        saveUserToken(savedUser, jwt);
        return JwtResponse.builder().token(jwt).build();
    }
    */

}