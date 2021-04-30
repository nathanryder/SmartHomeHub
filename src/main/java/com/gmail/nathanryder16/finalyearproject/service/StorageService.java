package com.gmail.nathanryder16.finalyearproject.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public interface StorageService {

    void init();

    void store(MultipartFile file);

    Path load(String filename);

    Resource loadAsResource();

}
