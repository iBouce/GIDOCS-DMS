package com.ibouce.Elasticsearch.folder;

import com.ibouce.Elasticsearch.jwt.model.JwtResponse;
import com.ibouce.Elasticsearch.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController {

    @Autowired
    FolderService folderService;

    @Autowired
    private final UserRepository userRepository;

    /*@GetMapping("/folders")
    public ResponseEntity<List<FolderModel>> findAllFolders() {
        List<FolderModel> folders = folderService.getAllFolders();
        return ResponseEntity.ok(folders);
    }*/

    @GetMapping("/users/{userId}/folders")
    public ResponseEntity<List<FolderModel>> findFoldersOfUserInGroup(@PathVariable(value = "userId") Long userId) {
        List<FolderModel> folders = folderService.findFoldersOfUserInGroup(userId);
        return ResponseEntity.ok(folders);
    }

    /*@GetMapping("/users/{userId}/folders")
    public ResponseEntity<FolderModel> findFoldersByUser(@PathVariable(value = "userId") Long userId) {
        FolderModel folders = folderService.getFoldersByUser(userId);
        return ResponseEntity.ok(folders);
    }*/

    @GetMapping("/folders/{id}")
    public ResponseEntity<Optional<FolderModel>> findFolderById(@PathVariable(value = "id") Long id) {
        Optional<FolderModel> folder = folderService.findFolderById(id);
        return ResponseEntity.ok(folder);
    }

    @GetMapping("/folders/{folderId}")
    public ResponseEntity<List<FolderModel>> findFoldersByParent(@PathVariable long folderId) {
        List<FolderModel> folders = folderService.getFoldersByParent(folderId);
        return ResponseEntity.ok(folders);
    }

    @PostMapping("/users/{userId}/folders/save")
    public ResponseEntity<FolderModel> saveFolder(@PathVariable(value = "userId") Long userId, @RequestBody FolderModel folder) {
        return ResponseEntity.status(HttpStatus.CREATED).body(folderService.saveFolder(userId, folder));
    }

    @PutMapping("/folders/update")
    public ResponseEntity<FolderModel> updateFolder(@RequestBody FolderModel folder) {
        FolderModel updatedFolder = folderService.updateFolder(folder);
        return ResponseEntity.ok(updatedFolder);
    }

    @DeleteMapping("/users/{userId}/folders/delete/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable("userId") Long userId, @PathVariable("folderId") Long folderId) {
        folderService.deleteFolder(userId, folderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/folders/search/{query}")
    public ResponseEntity<FolderModel> searchFolder(@PathVariable("query") String query) {
        FolderModel user = folderService.findFolderByName(query);
        return ResponseEntity.ok(user);
    }

}
