package com.gabrielczar.uplouddownload;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;
import java.util.stream.Stream;

@Service
public class StorageService {
    private final Path rootLocation;

    // The Bean configuration is in Main Class (UploudDownloadApplication.java)
    public StorageService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") String root) {
        this.rootLocation = init(root);
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file with relative path outside current directory " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    public boolean store(FileModel fileModel) {
        try {
            String base64 = fileModel.getData();
            String filename = fileModel.getFilename();

            byte[] data = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));

            Path destinationFile = load(filename);

            Files.write(destinationFile, data);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Path init(String root) {
        if (!Files.exists(Paths.get(root), LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectories(Paths.get(root));
            } catch (IOException e) {
                e.printStackTrace();
                // return path without access
            }
        }
        return Paths.get(root);
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public byte[] loadAsBytes(String filename) {
        File file = load(filename).toFile();
        try {
            InputStream stream = new FileInputStream(file);
            return FileCopyUtils.copyToByteArray(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
