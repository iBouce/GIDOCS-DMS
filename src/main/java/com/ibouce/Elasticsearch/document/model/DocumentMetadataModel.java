package com.ibouce.Elasticsearch.document.model;

import com.ibouce.Elasticsearch.folder.FolderModel;
import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "document")
public class DocumentMetadataModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")//, unique = true, nullable = false
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private String size;

    @Column(name = "path")
    private String path;

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "folder_id")
    private FolderModel folder;

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupModel group;

}