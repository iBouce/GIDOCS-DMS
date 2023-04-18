package com.ibouce.Elasticsearch.document;

//import com.ibouce.Elasticsearch.document.model.DocumentContentModel;
//import com.ibouce.Elasticsearch.document.repository.DocumentContentRepository;
import com.ibouce.Elasticsearch.document.model.DocumentMetadataModel;
import com.ibouce.Elasticsearch.document.repository.DocumentMetadataRepository;
import com.ibouce.Elasticsearch.folder.FolderModel;
import com.ibouce.Elasticsearch.folder.FolderRepository;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import com.ibouce.Elasticsearch.user.UserRepository;
import com.ibouce.Elasticsearch.util.FileUtil;
import com.ibouce.Elasticsearch.util.FolderUtil;
import com.ibouce.Elasticsearch.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    @Value("${directory.root}")
    private String rootDirectory;

    private final FolderUtil folderUtils;

    @Autowired
    private DocumentMetadataRepository documentMetadataRepository;

    //@Autowired
    //private DocumentContentRepository documentContentRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private final UserRepository userRepository;

    private final FileUtil fileUtil;
    private final PdfUtil pdfUtil;

    public List<DocumentMetadataModel> findAllDocuments() {
        return documentMetadataRepository.findAll();
    }

    public List<DocumentMetadataModel> findDocumentsByFolderId(Long userId, Long folderId) {
        /*if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Not found User with id = " + folderId);
        }
        if (!folderRepository.existsById(folderId)) {
            throw new ResourceNotFoundException("Not found Folder with id = " + folderId);
        }*/

        Optional<FolderModel> folder = folderRepository.findById(folderId);
        List<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findByAndFolderIdAndGroupId(folderId, folder.get().getGroup().getId());

        return documentMetadataModel;
    }

    public String uploadDocumentInBackground(MultipartFile file, Long folderId, Long userId) {
        new Thread(() -> {
            try {
                uploadDocument(file, folderId, userId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }

    public String uploadDocument(MultipartFile file, Long folderId, Long userId) throws IOException {
        // Check if the folder and user exist in their respective repositories
        if (!folderRepository.existsById(folderId)) {
            throw new IOException("Not found Folder with id = " + folderId);
        }
        if (!userRepository.existsById(userId)) {
            throw new IOException("Not found User with id = " + userId);
        }

        // Retrieve the folder and user models
        Optional<FolderModel> folder = folderRepository.findById(folderId);
        Optional<UserModel> user = userRepository.findById(userId);

        // Construct the path for the uploaded file
        String path = folder.get().getPath() + "/" + file.getOriginalFilename();

        // Transfer the file to the file system
        file.transferTo(new File(path).toPath());

        // Create and save a new DocumentMetadataModel object and set its properties
        DocumentMetadataModel documentMetadataModel = new DocumentMetadataModel();
        documentMetadataModel.setName(file.getOriginalFilename());
        documentMetadataModel.setType(FileUtil.getFileExtension(file));
        documentMetadataModel.setSize(String.valueOf(file.getSize()));
        documentMetadataModel.setPath(path);
        documentMetadataModel.setFolder(folder.get());
        documentMetadataModel.setUser(user.get());
        documentMetadataModel.setGroup(folder.get().getGroup());
        documentMetadataRepository.save(documentMetadataModel);

        // Save document content in Elasticsearch
        //DocumentContentModel documentContentModel = new DocumentContentModel();
        //documentContentModel.setId(documentMetadataModel.getId().toString());
        //documentContentModel.setContent(PdfUtil.extractTextFromPDF(path));
        //documentContentRepository.save(documentContentModel);

        return "Document uploaded successfully!";
    }

    public byte[] downloadDocument(Long documentId) throws IOException {
        if (!documentMetadataRepository.existsById(documentId)) {
            throw new IOException("Not found Document with id = " + documentId);
        }
        Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(documentId);
        String path = documentMetadataModel.get().getPath();
        byte[] document = Files.readAllBytes(new File(path).toPath());
        return document;
    }

    public byte[] previewDocument(Long documentId) throws IOException {
        if (!documentMetadataRepository.existsById(documentId)) {
            throw new IOException("Not found Document with id = " + documentId);
        }
        Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(documentId);
        String path = documentMetadataModel.get().getPath();
        byte[] document = Files.readAllBytes(new File(path).toPath());
        return document;
    }

    public DocumentMetadataModel saveDocumentMetadata(MultipartFile file, Long folderId, Long userId) throws IOException {

        if (!folderRepository.existsById(folderId)) {
            throw new IOException("Not found Folder with id = " + folderId);
        }

        Optional<FolderModel> folder = folderRepository.findById(folderId);
        Optional<UserModel> user = userRepository.findById(userId);

        // Save the file to the server
        DocumentMetadataModel documentMetadataModel = new DocumentMetadataModel();
        documentMetadataModel.setName(FileUtil.getFileNameWithoutExtension(file));
        documentMetadataModel.setType(FileUtil.getFileExtension(file));
        documentMetadataModel.setSize(String.valueOf(file.getSize()));
        documentMetadataModel.setPath(rootDirectory + "/" + folder.get().getPath());
        documentMetadataModel.setFolder(folder.get());
        documentMetadataModel.setUser(user.get());
        documentMetadataRepository.save(documentMetadataModel);

        //upload file to File System
        fileUtil.uploadFile(file, rootDirectory + "/" + folder.get().getPath());

        // Save document content in Elasticsearch
        //DocumentContentModel documentContentModel = new DocumentContentModel();
        //documentContentModel.setId(documentMetadataModel.getId().toString());

        // Extract text from PDF file
        String documentPath = rootDirectory + "/" + folder.get().getPath() + "/" + documentMetadataModel.getName() + "." + documentMetadataModel.getType();
        System.out.println(documentPath);
        String extractedText = pdfUtil.extractText(file);
        System.out.println(extractedText);

        //documentContentModel.setContent(extractedText);
        //documentContentRepository.save(documentContentModel);

        return documentMetadataModel;
    }

    public ResponseEntity<ByteArrayResource> downloadDocuments(Long documentId) throws IOException {
        if (!documentMetadataRepository.existsById(documentId)) {
            throw new IOException("Not found Document with id = " + documentId);
        }

        Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(documentId);
        System.out.println(documentMetadataModel.get().getName());
        String folderPath = documentMetadataRepository.findFolderPathByDocumentId(documentId);
        System.out.println(folderPath);

        String documentPath = rootDirectory + "/" + folderPath + "/" + documentMetadataModel.get().getName() + "." + documentMetadataModel.get().getType();

        return fileUtil.downloadFile(documentPath);
    }

    /*public DocumentMetadataModel saveDocumentMetadata(DocumentMetadataModel documentMetadataModel) {
        return documentMetadataRepository.save(documentMetadataModel);
    }*/

    /*public DocumentContentModel saveDocumentContent(DocumentContentModel documentContentModel) {
        return documentContentRepository.save(documentContentModel);
    }*/

    /*public DocumentMetadataModel saveDocument(File file) throws IOException, TikaException, SAXException {
        DocumentMetadataModel fileMetadata = new DocumentMetadataModel();
        fileMetadata.setName(file.getName());
        documentMetadataRepository.save(fileMetadata);

        DocumentContentModel fileContent = new DocumentContentModel();
        fileContent.setId(String.valueOf(fileMetadata.getId()));
        fileContent.setContent(TextParser.extract(file));
        documentContentRepository.index(fileContent);

        return fileMetadata;
    }*/

    public DocumentMetadataModel updateDocument(DocumentMetadataModel document) throws IOException {

        if (!documentMetadataRepository.existsById(document.getId())) {
            throw new IOException("Not found Document with id = " + document.getId());
        }

        // Get the target path for document
        String target = document.getPath();
        System.out.println(target);

        // Find the source path for document
        Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(document.getId());
        String source = documentMetadataModel.get().getPath();
        System.out.println(source);

        // Check if the source file exists and is not locked by another process
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            throw new IOException("File does not exist: " + source);
        }
        if (sourceFile.isFile() && !sourceFile.canWrite()) {
            throw new IOException("File is locked by another process: " + source);
        }

        // Check if the target file already exists and is not locked by another process
        File targetFile = new File(target);
        if (targetFile.exists() && !targetFile.canWrite()) {
            throw new IOException("File is locked by another process: " + target);
        }

        // Move the file from the source path to the target path
        Files.move(Path.of(target), Path.of(target), StandardCopyOption.REPLACE_EXISTING);
        System.out.println(source + " ------ " + target);
        folderUtils.renameFolder(source, target);
        folderUtils.moveFolder(source, target);

        // Update the document metadata in the repository
        document = documentMetadataRepository.save(document);

        return document;
    }

    public void deleteDocument(Long id) throws IOException {
        if (documentMetadataRepository.existsById(id)) {
            // Find the path for document
            Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(id);
            // Delete the file from the file system
            Files.delete(Path.of(documentMetadataModel.get().getPath()));
            //delete from MySQL
            documentMetadataRepository.deleteById(id);
            //delete from Elasticsearch
            //documentContentRepository.deleteById(id.toString());
        }
    }

    public List<DocumentMetadataModel> searchDocuments(String query) {
        List<DocumentMetadataModel> results = new ArrayList<>();
        /*List<DocumentContentModel> documentContentModels = documentContentRepository.findByContent(query);
        for (DocumentContentModel documentContent : documentContentModels) {
            Optional<DocumentMetadataModel> docMetadataOptional = documentMetadataRepository.findById(Long.valueOf(documentContent.getId()));
            if (docMetadataOptional.isPresent()) {
                results.add(docMetadataOptional.get());
            }
        }*/
        return results;
    }

    public byte[] loadPdfDocument(Long documentId) throws IOException {
        if (!documentMetadataRepository.existsById(documentId)) {
            throw new IOException("Not found Document with id = " + documentId);
        }
        Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(documentId);
        String folderPath = documentMetadataRepository.findFolderPathByDocumentId(documentId);
        String documentPath = rootDirectory + "/" + folderPath + "/" + documentMetadataModel.get().getName() + "." + documentMetadataModel.get().getType();

        File file = new File(documentPath);
        byte[] content = Files.readAllBytes(file.toPath());
        return content;
    }

    public String documentPath(Long documentId) throws IOException {
        if (!documentMetadataRepository.existsById(documentId)) {
            throw new IOException("Not found Document with id = " + documentId);
        }
        String folderPath = documentMetadataRepository.findFolderPathByDocumentId(documentId);
        String documentPath = rootDirectory + "/" + folderPath + "/";

        return documentPath;
    }

    public String documentName(Long documentId) throws IOException {
        if (!documentMetadataRepository.existsById(documentId)) {
            throw new IOException("Not found Document with id = " + documentId);
        }
        Optional<DocumentMetadataModel> documentMetadataModel = documentMetadataRepository.findById(documentId);
        String documentPath = documentMetadataModel.get().getName() + "." + documentMetadataModel.get().getType();

        return documentPath;
    }

    /*public List<DocumentMetadataModel> searchDocuments2(String query) {
        List<DocumentMetadataModel> results = new ArrayList<>();

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(query, "content"))
                .build();

        List<DocumentContentModel> documentContents = documentContentRepository.search(searchQuery).getContent();
        for (DocumentContentModel documentContent : documentContents) {
            Optional<DocumentMetadataModel> docMetadataOptional = documentMetadataRepository.findById(Long.valueOf(documentContent.getId()));
            if (docMetadataOptional.isPresent()) {
                results.add(docMetadataOptional.get());
            }
        }
        return results;
    }*/

}