package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorage implements StorageService {

    private Path uploads = Paths.get(".");

    @Override
    public void init() {
    }

    @Override
    public void store(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), uploads.resolve("credentials.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource() {
        try {
            Path file = uploads.resolve("credentials.json");
            Resource res = new UrlResource(file.toUri());

            if (res.exists()) {
                return res;
            } else {
                System.out.println("Failed to read file");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
