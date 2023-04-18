package com.ibouce.Elasticsearch.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibouce.Elasticsearch.user.Models.UserPermissionModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "permission")
public class PermissionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canUpload")
    private boolean canUpload;

    @Column(name = "canDownload")
    private boolean canDownload;

    @Column(name = "canView")
    private boolean canView;

    @Column(name = "canAdd")
    private boolean canAdd;

    @Column(name = "canEdit")
    private boolean canEdit;

    @Column(name = "canDelete")
    private boolean canDelete;

    @JsonIgnore
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    private List<UserPermissionModel> userPermissions;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private GroupModel groupe;*/

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;*/

}

