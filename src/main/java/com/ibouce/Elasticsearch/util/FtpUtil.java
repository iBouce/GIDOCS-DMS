package com.ibouce.Elasticsearch.util;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FtpUtil {

    private FTPClient ftpClient;

    public FtpUtil() {
        ftpClient = new FTPClient();
    }

    public boolean connect(String host, int port, String username, String password) {
        try {
            ftpClient.connect(host, port);
            return ftpClient.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDirectory(String path) {
        try {
            return ftpClient.makeDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean uploadFile(String localFilePath, String serverFilePath) {
        FTPClient client = new FTPClient();
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            return ftpClient.storeFile(serverFilePath, new FileInputStream(localFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnect() {
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

