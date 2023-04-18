package com.ibouce.Elasticsearch.user.Models;

import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.permission.PermissionModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_permission")
public class UserPermissionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupModel group;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "permission_id")
    private PermissionModel permission;

}

