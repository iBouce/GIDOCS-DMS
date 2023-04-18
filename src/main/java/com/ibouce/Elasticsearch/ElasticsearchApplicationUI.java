package com.ibouce.Elasticsearch;

import com.ibouce.Elasticsearch.group.GroupModel;
import com.ibouce.Elasticsearch.group.GroupService;
import com.ibouce.Elasticsearch.user.Models.Roles;
import com.ibouce.Elasticsearch.user.Models.UserModel;
import com.ibouce.Elasticsearch.user.UserService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.node.NodeValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@RequiredArgsConstructor
public class ElasticsearchApplicationUI {

    @Value("${directory.root}")
    String root;

    @Value("${elasticsearch.bat.path}")
    private static String elasticsearchBatPath;

    private static Process elasticsearchProcess;

    public static void main(String[] args) throws NodeValidationException, IOException, InterruptedException {
        // Start Elasticsearch
        /*ProcessBuilder processBuilder = new ProcessBuilder("elasticsearch/bin/elasticsearch.bat");
        processBuilder.directory(new File("elasticsearch"));
        processBuilder.start();*/


        // Wait for Elasticsearch to start
        /*boolean isElasticsearchRunning = false;
        while (!isElasticsearchRunning) {
            try {
                URL url = new URL("http://localhost:9200/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();
                isElasticsearchRunning = connection.getResponseCode() == 200;
                connection.disconnect();
            } catch (IOException e) {
                // Elasticsearch is not yet running
                Thread.sleep(1000); // wait for 1 second before trying again
            }
        }*/

        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext context = SpringApplication.run(ElasticsearchApplicationUI.class, args);
        // Set the path to the Elasticsearch .bat file
        /*elasticsearchBatPath = context.getEnvironment().getProperty("elasticsearch.bat.path");

        // Start Elasticsearch
        startElasticsearch();*/

        startSystemTray(context);
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


    public static void startElasticsearch() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(elasticsearchBatPath);
        elasticsearchProcess = processBuilder.start();
    }

    public static void stopElasticsearch() {
        if (elasticsearchProcess != null) {
            elasticsearchProcess.destroy();
        }
    }

    private static void startSystemTray(ConfigurableApplicationContext context) {

        String ICON_PATH = "/static/icon/server.png";

        TrayIcon trayIcon;
        MenuItem statusSpringBootItem;
        MenuItem statusMySQLItem;
        MenuItem exitItem;

        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage(ElasticsearchApplicationUI.class.getResource(ICON_PATH));

            // Create the popup menu
            PopupMenu popupMenu = new PopupMenu();
            statusSpringBootItem = new MenuItem("Spring Boot Server is running...");
            statusMySQLItem = new MenuItem(checkMySQLStatus());
            exitItem = new MenuItem("Exit Server");
            exitItem.addActionListener(event -> {
                // Add shutdown hook to stop Elasticsearch when the application is stopped
                stopElasticsearch();
                //Runtime.getRuntime().addShutdownHook(new Thread(() -> elasticsearchProcess.destroy()));
                System.exit(0);
            });
            popupMenu.add(statusSpringBootItem);
            popupMenu.add(statusMySQLItem);
            popupMenu.add(exitItem);

            // Create the tray icon
            trayIcon = new TrayIcon(image, "Gi-DOCS", popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Gi-DOCS");

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
        } else {
            System.out.println("SystemTray is not supported.");
        }

    }

    public static String checkSpringBootStatus() {
        String SB_HOST = "spring.datasource.host";
        String SB_PORT = "spring.datasource.port";
        String status;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(System.getProperty(SB_HOST), Integer.parseInt(System.getProperty(SB_PORT))), 1000);
            socket.close();
            return status = "Spring Boot Server is running...";
        } catch (IOException e) {
            return status = "Spring Boot Server is stopped !";
        }
    }

    public static String checkMySQLStatus() {

        String MYSQL_HOST = "spring.datasource.url";
        String MYSQL_USERNAME = "spring.datasource.username";
        String MYSQL_PASSWORD = "spring.datasource.password";
        String status;
        try (Connection conn = DriverManager.getConnection(
                System.getProperty(MYSQL_HOST),
                System.getProperty(MYSQL_USERNAME),
                System.getProperty(MYSQL_PASSWORD))) {
            return status = "MySQL Server is running...";
        } catch (SQLException e) {
            return status = "MySQL Server is stopped !";
        }
    }


    /*@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/

    /*@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("")
                        .allowCredentials(true)
                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                        .maxAge(3600);
            }
        };
    }*/

    /*@Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
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

    /*@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/

    /*@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedMethods("*").allowCredentials(true);
            }
        };
    }*/

    /*@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        //corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        //corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}
