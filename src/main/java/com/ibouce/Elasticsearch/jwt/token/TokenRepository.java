package com.ibouce.Elasticsearch.jwt.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenModel, Long> {

    @Query(value = """
      select t from TokenModel t inner join UserModel u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<TokenModel> findAllValidTokenByUser(Long id);

    Optional<TokenModel> findByToken(String token);
}