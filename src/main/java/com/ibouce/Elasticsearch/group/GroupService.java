package com.ibouce.Elasticsearch.group;

import com.ibouce.Elasticsearch.folder.FolderModel;
import com.ibouce.Elasticsearch.folder.FolderRepository;
import com.ibouce.Elasticsearch.folder.FolderService;
import com.ibouce.Elasticsearch.permission.PermissionModel;
import com.ibouce.Elasticsearch.permission.PermissionRepository;
import com.ibouce.Elasticsearch.user.Models.*;
import com.ibouce.Elasticsearch.user.UserRepository;
import com.ibouce.Elasticsearch.user.UserService;
import com.ibouce.Elasticsearch.util.FolderUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    @Value("${directory.root}")
    private String rootDirectory;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final FolderRepository folderRepository;

    @Autowired
    private final UserGroupRepository userGroupRepository;

    @Autowired
    private final UserPermissionRepository userPermissionRepository;

    @Autowired
    private final PermissionRepository permissionRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<GroupModel> findAllGroups() {
        return groupRepository.findAll();
    }

    public GroupModel findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }

    public GroupModel saveGroup(GroupModel group) {

        // Save the group to the database
        GroupModel groupModel = groupRepository.save(group);

        // Save the folder to the database
        FolderModel folder = new FolderModel();
        folder.setName(groupModel.getName());
        folder.setParent(null);
        folder.setPath(rootDirectory + "/" + groupModel.getId());
        folder.setGroup(groupModel);
        folderRepository.save(folder);

        // Create the folder in the file system
        String groupRootFolderPath = rootDirectory + "/" + groupModel.getId();
        if (FolderUtil.createFolder(groupRootFolderPath)) {
            logger.info("Created group root folder: {}", groupRootFolderPath);
        }

        return groupModel;
    }

    public GroupModel updateGroup(GroupModel group) {
        if (groupRepository.existsById(group.getId())) {
            return groupRepository.save(group);
        }
        return null;
    }

    public void deleteGroup(Long id) {
        if (groupRepository.existsById(id)) {

            // Delete the folder to the database
            FolderModel folder = folderRepository.findByGroupIdAndParentIsNull(id);
            folderRepository.delete(folder);

            // delete the folder in the file system
            String groupRootFolderPath = rootDirectory + "/" + id;
            if (FolderUtil.deleteFolder(groupRootFolderPath)) {
                logger.info("Delete group root folder: {}", groupRootFolderPath);
            }

            // Delete the group to the database
            groupRepository.deleteById(id);
        }
    }

    public List<UserPermissionModel> getUsersAndPermissionsByGroupId(Long groupId) {
        List<UserPermissionModel> groups = userPermissionRepository.findByGroupId(groupId);
        return groups;
    }

    public UserGroupModel addUserToGroupWithPermissions(Long groupId, Long userId, PermissionModel permission) {
        // Get the user, group, and permission objects from the repositories
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupModel group = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found"));
        //PermissionModel permission = permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found"));

        //folderService.saveFolder(user.getId(), group);

        // Create a UserGroupModel object to represent the user's membership in the group
        UserGroupModel userGroup = new UserGroupModel();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroupRepository.save(userGroup);

        PermissionModel permissionModel = permissionRepository.save(permission);

        // Create a UserPermissionModel object to represent the user's permissions in the group
        UserPermissionModel userPermission = new UserPermissionModel();
        userPermission.setUser(user);
        userPermission.setGroup(group);
        userPermission.setPermission(permissionModel);
        userPermissionRepository.save(userPermission);

        return userGroup;
    }

    public UserGroupModel updateUserToGroupWithPermissions(Long userId, Long groupId, Long permissionId) {
        /*UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupModel group = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found"));
        PermissionModel permission = permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found"));



        if (userPermissionRepository.existsById(group.getId())) {
            return userPermissionRepository.save();
        }*/
        return null;
    }

    public void deleteUserToGroupWithPermissions(Long id) {
        if (userPermissionRepository.existsById(id)) {
            userPermissionRepository.deleteById(id);
        }
    }

    public UserGroupModel saveUserToGroup(GroupModel group, Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupModel groupe = groupRepository.findById(group.getId()).orElseThrow(() -> new EntityNotFoundException("Group not found"));
        UserGroupModel userGroup = new UserGroupModel();
        userGroup.setUser(user);
        userGroup.setGroup(groupe);
        userRepository.save(user);
        return userGroup;

        /*Set<UserModel> users = groupe.getUsers();
        users.add(user);
        groupe.setUsers(users);
        return groupRepository.save(groupe);*/
    }

    /*public void removeUserFromGroup(Long groupId, Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupModel group = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found"));
        userRepository.deleteUserGroupByUserAndGroup(user, group);

    }*/
    /*UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupModel groupe = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found"));
        Set<UserModel> users = groupe.getUsers();
        users.remove(user);
        groupe.setUsers(users);
        return groupRepository.save(groupe);*/
    /*public List<PermissionModel> getPermissionOfUserInGroup(Long userId, Long groupId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<UserPermissionModel> userPermissions = user.getUserPermissions();
        List<PermissionModel> permissions = new ArrayList<>();
        for (UserPermissionModel userPermission : userPermissions) {
            if (userPermission.getGroup().getId().equals(groupId)) {
                permissions.add(userPermission.getPermission());
            }
        }
        return permissions;
    }*/
    /*public void addPermissionToUserInGroup(Long userId, Long groupId, Long permissionId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupModel group = groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found"));
        PermissionModel permission = permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found"));
        UserPermissionModel userPermission = new UserPermissionModel();
        userPermission.setUser(user);
        userPermission.setGroup(group);
        userPermission.setPermission(permission);
        userRepository.save(user);
    }*/
    /*public List<PermissionModel> getPermissionsByUserIdAndGroupId(Long groupId, Long userId) {
        GroupModel group = groupRepository.findById(groupId).orElse(null);
        UserModel user = userRepository.findById(userId).orElse(null);
        if (group == null || user == null) {
            return null;
        }
        return permissionRepository.findByUserAndGroup(user, group);
    }*/

}

