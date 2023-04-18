package com.ibouce.Elasticsearch.folder;

import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.group.GroupRepository;
import com.ibouce.Elasticsearch.permission.PermissionRepository;
import com.ibouce.Elasticsearch.user.Models.*;
import com.ibouce.Elasticsearch.user.UserRepository;
import com.ibouce.Elasticsearch.util.FolderUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FolderService {

    @Value("${directory.root}")
    private String rootDirectory;

    private final FolderUtil folderUtils;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserGroupRepository userGroupRepository;

    public List<FolderModel> findFoldersOfUserInGroup(Long userId) {
        List<UserGroupModel> userGroups = userGroupRepository.findByUserId(userId);

        List<GroupModel> groups = userGroups.stream()
                .map(UserGroupModel::getGroup)
                .collect(Collectors.toList());

        List<FolderModel> folders = folderRepository.findByGroupInAndParentIsNull(groups);

        // Fetch the children of each folder
        for (FolderModel folder : folders) {
            fetchChildren(folder);
        }

        return folders;
    }

    public FolderModel saveFolder(Long userId, FolderModel folder) {

        // Retrieve the user
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        // Retrieve the perùission
        UserPermissionModel userPermission = userPermissionRepository.findByUserAndGroup(user, folder.getGroup());
        // Check if the user has the permission to delete the folder
        if (!userPermission.getPermission().isCanAdd()) {
            throw new NoSuchElementException("Permission denied");
        }

        // Create the folder in the file system
        folderUtils.createFolder(folder.getPath());

        // Save the folder to the database
        return folderRepository.save(folder);
    }

    public FolderModel updateFolder(FolderModel folder) {
        if (folderRepository.existsById(folder.getId())) {
            Optional<FolderModel> folderModel = folderRepository.findById(folder.getId());

            // Rename OR Move the folder in the file system
            String oldPath = rootDirectory + "/" + folderModel.get().getPath();
            String newPath = rootDirectory + "/" + getFolderPath(folder.getParent()) + "/" + folder.getName();

            System.out.println(oldPath + " ------ " + newPath);
            folderUtils.renameFolder(oldPath, newPath);
            folderUtils.moveFolder(oldPath, newPath);

            return folderRepository.save(folder);
        }
        return null;
    }

    public void deleteFolder(Long userId, Long folderId) {

        // Retrieve the FolderModel by id
        FolderModel folder = folderRepository.findById(folderId).orElseThrow(() -> new NoSuchElementException("Folder with id " + folderId + " not found"));
        // Retrieve the user
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        // Retrieve the group from folder
        GroupModel group = folder.getGroup();
        // Retrieve the perùission
        UserPermissionModel userPermission = userPermissionRepository.findByUserAndGroup(user, group);
        // Check if the user has the permission to delete the folder
        if (!userPermission.getPermission().isCanDelete()) {
            throw new NoSuchElementException("Permission denied");
        }

        // Delete the folder from the database
        folderRepository.deleteById(folder.getId());

        // Delete the folder in the file system
        String folderPath = rootDirectory + "/" + folder.getPath();
        folderUtils.deleteFolder(folderPath);
    }


    private void fetchChildren(FolderModel folder) {
        List<FolderModel> children = folder.getChildren();
        if (children != null) {
            for (FolderModel child : children) {
                fetchChildren(child);
            }
        }
    }

    public List<Map<String, Object>> findFoldersOfUserInGroup2(Long userId) {
        List<UserGroupModel> userGroups = userGroupRepository.findByUserId(userId);
        List<GroupModel> groups = userGroups.stream()
                .map(UserGroupModel::getGroup)
                .collect(Collectors.toList());
        List<FolderModel> folders = folderRepository.findByGroupIn(groups);
        List<Map<String, Object>> result = new ArrayList<>();

        for (FolderModel folder : folders) {
            Map<String, Object> folderMap = new HashMap<>();
            folderMap.put("id", folder.getId());
            folderMap.put("name", folder.getName());
            folderMap.put("path", folder.getPath());
            folderMap.put("parent", folder.getParent());

            List<Map<String, Object>> children = new ArrayList<>();
            for (FolderModel child : folder.getChildren()) {
                Map<String, Object> childMap = new HashMap<>();
                childMap.put("id", child.getId());
                childMap.put("name", child.getName());
                childMap.put("path", child.getPath());
                childMap.put("parent", child.getParent());
                childMap.put("children", Collections.emptyList());
                childMap.put("group", Collections.singletonMap("id", child.getGroup().getId()));
                childMap.put("user", Collections.singletonMap("id", child.getUser().getId()));
                children.add(childMap);
            }

            folderMap.put("children", children);
            folderMap.put("group", Collections.singletonMap("id", folder.getGroup().getId()));
            result.add(folderMap);
        }

        return result;
    }

    /*public List<FolderModel> findFoldersOfUserInGroup(Long userId) {
        List<UserGroupModel> userGroups = userGroupRepository.findByUserId(userId);
        List<GroupModel> groups = userGroups.stream()
                .map(UserGroupModel::getGroup)
                .collect(Collectors.toList());
        return folderRepository.findByGroupIn(groups);
    }*/

    public FolderModel getFoldersByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Not found User with id = " + userId);
        }

        FolderModel rootFolder = folderRepository.findByUserIdAndParentIsNull(userId);
        if (rootFolder == null) {
            return null;
        }

        // Recursively populate the tree of folders
        populateFolderTree(rootFolder);

        return rootFolder;
    }

    private void populateFolderTree(FolderModel folder) {
        // Get the children of the current folder
        List<FolderModel> children = folder.getChildren();
        if (children == null) {
            return;
        }

        // Recursively populate the tree of each child folder
        for (FolderModel child : children) {
            populateFolderTree(child);
        }
    }

    public String getFolderPath(Long folderId) {
        // Get the folder by ID
        Optional<FolderModel> folder = folderRepository.findById(folderId);

        // If the folder is null or has no parent, return its name
        if (folder == null || folder.get().getParent() == null) {
            return folder.get().getName();
        }

        // Recursively get the folder path for the parent folder and append the current folder name
        return getFolderPath(folder.get().getParent()) + "/" + folder.get().getName();
    }

    public Optional<FolderModel> findFolderById(Long id) {
        return folderRepository.findById(id);
    }


    public List<FolderModel> getAllFolders() {
        return folderRepository.findAll();
    }

    public FolderModel getFolderById(Long id) {
        return folderRepository.findById(id).orElse(null);
    }

    public List<FolderModel> getFoldersByParent(Long parentId) {
        List<FolderModel> folders = new ArrayList<>();
        List<FolderModel> foldersList = folderRepository.findByParent(parentId);
        folders.addAll(foldersList);
        for (FolderModel folderModel : foldersList) {
            folders.addAll(getFoldersByParent(folderModel.getId()));
        }
        return folders;

        /*List<FolderModel> tree = new ArrayList<>();
        List<FolderModel> folders = folderRepository.findByParent(parentId);
        for (FolderModel folder : folders) {
            if (tree.contains(folder)) {
                continue;
            }
            tree.add(folder);
            tree.addAll(folderRepository.findByParent(folder.getId()));
        }
        return tree;*/

    }

    public FolderModel findFolderByName(String query) {
        return folderRepository.findByNameContaining(query);
    }
}