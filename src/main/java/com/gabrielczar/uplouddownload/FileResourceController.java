package com.gabrielczar.uplouddownload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileResourceController {
    private final StorageService storageService;

    @Autowired
    public FileResourceController(StorageService storageService) {
        this.storageService = storageService;
    }

    // list name of files
    @GetMapping("/") @ResponseBody
    public List<String> listUploadedFiles() {
        return storageService
                .loadAll()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(FileResourceController.class,"serveFile",
                                path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());

    }

    // get file as resource
    @GetMapping("/files/{filename:.+}") @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    // save file
    @PostMapping("/")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
        return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.CREATED);
    }
}
