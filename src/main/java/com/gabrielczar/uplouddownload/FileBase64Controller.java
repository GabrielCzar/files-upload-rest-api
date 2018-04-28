package com.gabrielczar.uplouddownload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FileBase64Controller {
    private StorageService storageService;

    @Autowired
    public FileBase64Controller(StorageService storageService) {
        this.storageService = storageService;
    }

    // Use same list

    // save content
    @PostMapping("/base64")
    public ResponseEntity<FileModel> saveContent(FileModel fileModel) {
        storageService.store(fileModel);
        return ResponseEntity.ok(fileModel);
    }

    @GetMapping(value = "/base64/image/{filename:.+}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<?> downloadJpeg(@PathVariable String filename) {
        byte[] content = storageService.loadAsBytes(filename);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping(value = "/base64/pdf/{filename:.+}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadPdfs(@PathVariable String filename) {
        byte[] content = storageService.loadAsBytes(filename);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping(value = "/base64/{filename:.+}")
    public ResponseEntity<?> downloadTexts(@PathVariable String filename) {
        byte[] content = storageService.loadAsBytes(filename);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

}
