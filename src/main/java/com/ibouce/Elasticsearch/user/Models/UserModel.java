package com.ibouce.Elasticsearch.user.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibouce.Elasticsearch.jwt.token.TokenModel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user")
public class UserModel implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "jobTitle")
    private String jobTitle;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "lastActive")
    private LocalDateTime lastActive;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<TokenModel> tokens;

    /*@JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<GroupModel> groups;*/

    /*@ManyToMany(mappedBy = "users")//, fetch = FetchType.LAZY
    private Set<GroupModel> groups = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<PermissionModel> permission = new HashSet<>();*/

    /*@JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FolderModel> folders;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DocumentMetadataModel> documents;
*/

    @JsonIgnoreProperties({"user"})
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserGroupModel> groups = new HashSet<>();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
