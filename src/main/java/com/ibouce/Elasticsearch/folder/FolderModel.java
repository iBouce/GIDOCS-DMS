package com.ibouce.Elasticsearch.folder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibouce.Elasticsearch.document.model.DocumentMetadataModel;
import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.user.Models.UserModel;
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
@Table(name = "folder")
public class FolderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "parent")
    private Long parent;

    @ToString.Exclude
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<FolderModel> children;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<DocumentMetadataModel> documents;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupModel group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

}
