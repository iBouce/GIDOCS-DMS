package com.ibouce.Elasticsearch.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUtil {

    public static String extract(MultipartFile file) throws TikaException, IOException, SAXException {
        Parser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        FileInputStream inputStream = new FileInputStream(file.getName());
        ParseContext context = new ParseContext();
        parser.parse(inputStream, handler, metadata, context);
        System.out.println(handler);
        return handler.toString();
    }

    public static String readFileContent(MultipartFile file) {
        Tika tika = new Tika();
        try {
            return tika.parseToString(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (TikaException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        int i = originalFileName.lastIndexOf(".");
        return i > 0 ? originalFileName.substring(i + 1) : null;
    }

    public static String getFileNameWithoutExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        int dotIndex = originalFileName.lastIndexOf(".");
        return (dotIndex == -1) ? originalFileName : originalFileName.substring(0, dotIndex);
    }

    public ResponseEntity<String> uploadFile(MultipartFile file, String folderPath) {
        try {
            // Save the file to disk
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folderPath + "/" + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
        return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
    }

    public ResponseEntity<ByteArrayResource> downloadFile(String filePath) throws IOException {
        File file = new File(filePath);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    /*public void downloadFile(String sourceFilePath, String destinationFilePath) throws IOException {
        try (Socket socket = new Socket("localhost", 9000);
             OutputStream outputStream = new FileOutputStream(destinationFilePath);
             InputStream inputStream = socket.getInputStream()) {
            outputStream.write(sourceFilePath.getBytes());

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }*/


}
