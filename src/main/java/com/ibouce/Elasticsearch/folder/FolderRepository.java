package com.ibouce.Elasticsearch.folder;

import com.ibouce.Elasticsearch.group.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<FolderModel, Long> {

    Optional<FolderModel> findById(Long folderId);
    FolderModel findByNameContaining(String query);
    List<FolderModel> findByParent(Long parentId);
    FolderModel findByUserIdAndParentIsNull(Long userId);

    FolderModel findByGroupId(Long id);

    FolderModel findByGroupIdAndParentIsNull(Long id);

    List<FolderModel> findByParentIsNull();

    List<FolderModel> findByGroupIn(List<GroupModel> groups);
    List<FolderModel> findByGroupInAndParentIsNull(List<GroupModel> groups);

    FolderModel findGroupIdById(Long folderId);
}