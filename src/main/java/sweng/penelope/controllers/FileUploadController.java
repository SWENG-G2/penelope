package sweng.penelope.controllers;

import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sweng.penelope.services.StorageService;

@Controller
@RequestMapping(path = "/api/file")
public class FileUploadController {
    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("{campusId}/new")
    public ResponseEntity<String> handleFileUpload(@RequestParam MultipartFile file, @RequestParam String type) {
        if ((type.equals("audio") || type.equals("video") || type.equals("image")) && file != null) {
            String originalfileName = file.getOriginalFilename();
            if (originalfileName != null && !originalfileName.contains("..")) {
                String fileName = Paths.get(originalfileName).getFileName().toString();
                if (storageService.store(type, file))
                    return ResponseEntity.ok().body(String.format("%s/%s", type, fileName));
                else
                    return ResponseEntity.internalServerError().body("Could not store file.");
            }
            return ResponseEntity.badRequest().body("File name cannot contain \"..\" and cannot be null");
        }

        return ResponseEntity.badRequest().body("File type is not supported");
    }
}
