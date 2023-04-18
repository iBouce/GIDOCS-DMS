package com.ibouce.Elasticsearch.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibouce.Elasticsearch.folder.FolderModel;
import com.ibouce.Elasticsearch.user.Models.UserGroupModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "groupe")
public class GroupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @JsonIgnoreProperties({"members"})
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<UserGroupModel> members = new HashSet<>();

    /*@JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<FolderModel> folders;*/

    /*@JsonIgnore
    @ManyToMany
    @JoinTable(name = "group_user", joinColumns = {@JoinColumn(name = "groupe_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<UserModel> users = new HashSet<>();*/

    /*@OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
    private Set<PermissionModel> permissions = new HashSet<>();*/

}