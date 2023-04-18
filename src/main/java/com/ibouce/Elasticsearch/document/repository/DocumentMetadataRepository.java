package com.ibouce.Elasticsearch.document.repository;

import com.ibouce.Elasticsearch.document.model.DocumentMetadataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadataModel, Long> {

    List<DocumentMetadataModel> findByAndFolderIdAndGroupId(Long folderId, Long groupId);

    @Query("SELECT f.path FROM FolderModel f JOIN f.documents d WHERE d.id = :documentId")
    String findFolderPathByDocumentId(@Param("documentId") Long documentId);
}