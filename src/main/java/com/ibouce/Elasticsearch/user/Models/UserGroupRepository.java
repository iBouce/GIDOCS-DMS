package com.ibouce.Elasticsearch.user.Models;

import com.ibouce.Elasticsearch.group.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupModel, Long> {
    List<UserGroupModel> findByGroupId(Long groupId);
    List<UserGroupModel>  findByUserId(Long userId);

    UserGroupModel findByUserAndGroup(UserModel user, GroupModel group);
}