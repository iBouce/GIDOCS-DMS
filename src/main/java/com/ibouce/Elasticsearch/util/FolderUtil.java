package com.ibouce.Elasticsearch.util;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FolderUtil {

    public static boolean createFolder(String folderPath) {
        File folderDir = new File(folderPath);
        if (!folderDir.exists()) {
            return folderDir.mkdirs();
        }
        return false;
    }

    public static boolean renameFolder(String oldFolderPath, String newFolderPath) {
        File oldFolderDir = new File(oldFolderPath);
        File newFolderDir = new File(newFolderPath);
        if (oldFolderDir.exists() && !newFolderDir.exists()) {
            oldFolderDir.renameTo(newFolderDir);
        }
        return false;
    }

    public static boolean moveFolder(String oldFolderPath, String newFolderPath) {
        File oldFolderDir = new File(oldFolderPath);
        File newFolderDir = new File(newFolderPath);
        if (oldFolderDir.exists() && !newFolderDir.exists()) {
            return oldFolderDir.renameTo(newFolderDir);
        }
        return false;
    }

    public static boolean deleteFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            return false;
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }
        return folder.delete();
    }

}
