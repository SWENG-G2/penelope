package sweng.penelope.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sweng.penelope.services.StorageService;

@Controller
@RequestMapping(path = "/")
public class FileDownloadController {
    private final StorageService storageService;

    @Autowired
    public FileDownloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    private ResponseEntity<Resource> provideResponse(Resource file, String fileName) {
        if (file != null) {
            MediaType mediaType;

            String fileType = fileName.split("\\.")[1]; // Escape the dot

            switch (fileType) {
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
                default:
                case "xml":
                    mediaType = MediaType.APPLICATION_XML;
                    break;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // inline = display as response body
                    .body(file);
        }

        return ResponseEntity.internalServerError().body(null);
    }

    @GetMapping("/{fileHome}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveCampusXMLOld(@PathVariable String fileHome,
            @PathVariable String fileName) {
        Resource file = storageService.loadAsResource(fileHome, fileName);

        return provideResponse(file, fileName);
    }

    @GetMapping("/{fileHome}/{fileSubDir}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveBlob(@PathVariable String fileHome, @PathVariable String fileSubDir,
            @PathVariable String fileName) {
        Path fileHomePath = Paths.get(fileHome, fileSubDir);
        Resource file = storageService.loadAsResource(fileHomePath.toString(), fileName);

        return provideResponse(file, fileName);
    }

    @GetMapping(path = "/bird/{birdId}")
    public ResponseEntity<Resource> serveCampusXML(@PathVariable Long birdId) {
        Resource resource = storageService.loadAsResourceFromDB(false, birdId);

        if (resource != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // inline = display as response body
                    .body(resource);
        }

        return ResponseEntity.internalServerError().body(null);
    }
}
