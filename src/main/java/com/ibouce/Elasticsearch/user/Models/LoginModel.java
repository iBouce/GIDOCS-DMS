package com.ibouce.Elasticsearch.user.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginModel {
    private UserModel user;
    private String token;
}
