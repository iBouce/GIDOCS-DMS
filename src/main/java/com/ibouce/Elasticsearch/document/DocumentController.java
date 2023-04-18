package com.ibouce.Elasticsearch.document;

import com.ibouce.Elasticsearch.document.model.DocumentMetadataModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentController {

    @Autowired
    DocumentService documentService;

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentMetadataModel>> findAllDocuments() {
        List<DocumentMetadataModel> documents = documentService.findAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/users/{userId}/folders/{folderId}/documents")
    public ResponseEntity<List<DocumentMetadataModel>> findDocumentsByFolderId(@PathVariable(value = "userId") Long userId, @PathVariable(value = "folderId") Long folderId) {
        List<DocumentMetadataModel> documents = documentService.findDocumentsByFolderId(userId, folderId);
        return ResponseEntity.ok(documents);
    }

    /*@PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestBody MultipartFile file) {
        try {
            documentService.saveDocument((File) file);
            return ResponseEntity.ok("Document uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload document");
        }
    }*/

    @PostMapping("/users/{userId}/folders/{folderId}/documents/upload")
    public ResponseEntity<String> uploadDocument(@RequestBody MultipartFile file, @PathVariable Long folderId, @PathVariable Long userId) throws IOException {
        String document = documentService.uploadDocumentInBackground(file, folderId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(document);
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable Long documentId) throws IOException {
        byte[] document = documentService.downloadDocument(documentId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_PDF).body(document);
    }

    @GetMapping("/documents/{documentId}/preview")
    public ResponseEntity<?> previewDocument(@PathVariable Long documentId) throws IOException {
        byte[] document = documentService.previewDocument(documentId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_PDF).body(document);
    }

    @PutMapping("/documents/update")
    public ResponseEntity<DocumentMetadataModel> updateDocument(@RequestBody DocumentMetadataModel documentMetadataModel) throws IOException {
        DocumentMetadataModel updatedDocument = documentService.updateDocument(documentMetadataModel);
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/documents/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") Long id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/search/{query}")
    public ResponseEntity<List<DocumentMetadataModel>> searchDocument(@PathVariable("query") String query) {
        List<DocumentMetadataModel> documentMetadataModel = documentService.searchDocuments(query);
        return ResponseEntity.ok(documentMetadataModel);
    }

}
