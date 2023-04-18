package com.ibouce.Elasticsearch.user;

import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
    UserModel findByUsernameContaining(String username);
    //UserModel findByUsernameAndPassword(String username, String password);

    //List<UserModel> findByGroups(GroupModel group);

    //void deleteUserGroupByUserAndGroup(UserModel user, GroupModel group);
}