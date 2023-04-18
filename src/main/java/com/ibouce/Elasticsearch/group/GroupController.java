package com.ibouce.Elasticsearch.group;

import com.ibouce.Elasticsearch.permission.PermissionModel;
import com.ibouce.Elasticsearch.user.Models.UserGroupModel;
import com.ibouce.Elasticsearch.user.Models.UserPermissionModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/groups")
    public ResponseEntity<List<GroupModel>> findAllGroups() {
        List<GroupModel> groups = groupService.findAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<GroupModel> findGroupById(@PathVariable(value = "id") Long id) {
        GroupModel group = groupService.findById(id);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/groups/save")
    public ResponseEntity<GroupModel> saveGroup(@RequestBody GroupModel group) {
        GroupModel createdGroup = groupService.saveGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @PutMapping("/groups/update")
    public ResponseEntity<GroupModel> updateGroup(@RequestBody GroupModel group) {
        GroupModel createdGroup = groupService.updateGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @DeleteMapping("/groups/delete/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/users")
    public ResponseEntity<List<UserPermissionModel>> getUsersByGroupId(@PathVariable Long groupId) {
        List<UserPermissionModel> users = groupService.getUsersAndPermissionsByGroupId(groupId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/groups/{groupId}/save")
    public ResponseEntity<UserGroupModel> saveUserToGroup(@PathVariable Long groupId, @PathVariable Long userId, @RequestBody PermissionModel permission) {
        UserGroupModel createdGroup = groupService.addUserToGroupWithPermissions(groupId, userId, permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @PutMapping("/users/{userId}/groups/{groupId}/permission/{permissionId}/update")
    public ResponseEntity<UserGroupModel> updateUserToGroup(@PathVariable Long userId,@PathVariable Long groupId, @PathVariable Long permissionId) {
        UserGroupModel createdGroup = groupService.updateUserToGroupWithPermissions(userId,groupId,permissionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @DeleteMapping("/users/groups/delete/{id}")
    public ResponseEntity<Void> deleteUserFromGroup(@PathVariable("id") Long id) {
        groupService.deleteUserToGroupWithPermissions(id);
        return ResponseEntity.noContent().build();
    }

    /*@DeleteMapping("/users/{userId}/groups/delete/{groupId}")
    public ResponseEntity<Void> deleteUserFromGroup(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }*/

    /*@GetMapping("/{groupId}/users/{userId}/permissions")
    public ResponseEntity<List<PermissionModel>> getPermissionsByUserIdAndGroupId(@PathVariable Long groupId, @PathVariable Long userId) {
        List<PermissionModel> permissions = groupService.getPermissionsByUserIdAndGroupId(groupId, userId);
        if (permissions == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permissions);
    }*/

}