package com.ibouce.Elasticsearch.user.Models;

import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.permission.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissionModel, Long> {
    List<UserPermissionModel> findByGroupId(Long groupId);

    //UserPermissionModel findByUserAndGroupAndPermission(UserModel user, GroupModel group, PermissionModel permission);
    UserPermissionModel findByUserAndGroup(UserModel user, GroupModel group);
}