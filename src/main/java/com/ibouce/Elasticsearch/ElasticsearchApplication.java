package com.ibouce.Elasticsearch;

import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.group.GroupService;
import com.ibouce.Elasticsearch.user.Models.Roles;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import com.ibouce.Elasticsearch.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

//@SpringBootApplication
public class ElasticsearchApplication {

    /*
    @Value("${directory.root}")
    String root;

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, GroupService groupService) {
        return args -> {

            File rootFolder = new File(root);
            if (!rootFolder.exists()) {
                System.out.println("-------- Root Folder created in " + rootFolder.getAbsolutePath());
                rootFolder.mkdir();
            }

            userService.saveUser(new UserModel(null, "Administrator", "admin@gidocs.dz", "Admin", "admin", "admin", Roles.SUPER_ADMIN, true, null, null, null, null));
            userService.saveUser(new UserModel(null, "User", "user@gidocs.dz", "User", "user", "user", Roles.USER, true, null, null, null, null));
            groupService.saveGroup(new GroupModel(null, "Direction", null, null, null));
            groupService.saveGroup(new GroupModel(null, "Department", null, null, null));

        };
    }

    */

    /*@Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:9300"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }*/

    //useful for debug, print elastic search details
    /*private void printElasticSearchInfo() {

        System.out.println("--ElasticSearch--");
        Client client = es.getClient();
        Map<String, String> asMap = client.settings().getAsMap();

        asMap.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
        System.out.println("--ElasticSearch--");
    }*/

}
