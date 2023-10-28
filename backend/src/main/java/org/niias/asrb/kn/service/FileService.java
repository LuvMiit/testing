package org.niias.asrb.kn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${filePath}")
    private String filePath;

    public void writeFile(String fileId, byte[] fileContent) {
        try {
            Path path = Paths.get(filePath, getSubFolder(fileId)).normalize();
            Files.createDirectories(path);
            Files.write(path.resolve(fileId), fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] readFile(String fileId) throws IOException {
        Path path = Paths.get(filePath, getSubFolder(fileId), fileId).normalize();
        boolean exists = Files.exists(path);
        return exists ? Files.readAllBytes(path) : null;
    }

    private String getSubFolder(String fileId) {
        return fileId.substring(0, 3);
    }
}
