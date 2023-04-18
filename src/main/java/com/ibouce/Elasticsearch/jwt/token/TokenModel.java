package com.ibouce.Elasticsearch.jwt.token;

import com.ibouce.Elasticsearch.user.Models.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "token")
public class TokenModel {

    @Id
    @GeneratedValue
    public Long id;

    //@Column(unique = true)
    @Column(name = "token")
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    @Column(name = "revoked")
    public boolean revoked;

    @Column(name = "expired")
    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserModel user;
}