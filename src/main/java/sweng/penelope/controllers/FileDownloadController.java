package sweng.penelope.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import sweng.penelope.services.StorageService;

@Controller
public class FileDownloadController {
    private final StorageService storageService;

    @Autowired
    public FileDownloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    private ResponseEntity<Resource> provideResponse(Resource resource, MediaType mediaType) {
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // inline = display as response body
                .body(resource);
    }

    private ResponseEntity<Resource> provideXMLResponse(Resource resource) {
        if (resource != null) {
            return provideResponse(resource, MediaType.APPLICATION_XML);
        }

        return ResponseEntity.internalServerError().body(null);
    }

    private ResponseEntity<Resource> provideAssetResponse(Resource resource) {
        if (resource != null) {
            String contentType;
            try {
                contentType = Files.probeContentType(Paths.get(resource.getURI()));
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(null);
            }
            MediaType mediaType = MediaType.parseMediaType(contentType);
            return provideResponse(resource, mediaType);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/bird/{birdId}")
    @Cacheable("birds")
    public ResponseEntity<Resource> serveBirdXML(@PathVariable Long birdId) {
        Resource resource = storageService.loadAsResourceFromDB("bird", birdId);

        return provideXMLResponse(resource);
    }

    @GetMapping(path = "/campus/{campusId}")
    @Cacheable("campuses")
    public ResponseEntity<Resource> serveCampusXML(@PathVariable Long campusId) {
        Resource resource = storageService.loadAsResourceFromDB("campus", campusId);

        return provideXMLResponse(resource);
    }

    @GetMapping(path = "/campus/list")
    @Cacheable("campusesList")
    public ResponseEntity<Resource> serveCampusesListXML() {
        Resource resource = storageService.loadAsResourceFromDB("campusList", null);

        return provideXMLResponse(resource);
    }

    @GetMapping(path = "/{type}/{fileName}")
    @Cacheable("assets")
    public ResponseEntity<Resource> serveAsset(@PathVariable String type, @PathVariable String fileName) {
        Resource resource = storageService.loadAsResource(type, fileName);

        return provideAssetResponse(resource);
    }
}
