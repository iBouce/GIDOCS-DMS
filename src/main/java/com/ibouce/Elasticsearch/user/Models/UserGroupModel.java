package com.ibouce.Elasticsearch.user.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibouce.Elasticsearch.group.GroupModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_group")
public class UserGroupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonIgnoreProperties({"members"})
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ToString.Exclude
    @JsonIgnoreProperties({"members"})
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private GroupModel group;

}