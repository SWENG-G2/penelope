package sweng.penelope.controllers;

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
import sweng.penelope.xml.BirdXML;

@Controller
public class FileDownloadController {
    private final StorageService storageService;

    @Autowired
    public FileDownloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    private ResponseEntity<Resource> provideXMLResponse(Resource resource) {
        if (resource != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // inline = display as response body
                    .body(resource);
        }

        return ResponseEntity.internalServerError().body(null);
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

    @GetMapping(path =  "/campus/list")
    @Cacheable("campusesList")
    public ResponseEntity<Resource> serveCampusesListXML() {
        Resource resource = storageService.loadAsResourceFromDB("campusList", null);

        return provideXMLResponse(resource);
    }
}
