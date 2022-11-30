package sweng.penelope.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sweng.penelope.Responses;
import sweng.penelope.entities.ApiKey;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.CampusRepository;

@Controller
@RequestMapping(path = "/api/campus")
public class CampusController {
    private Responses responses = new Responses();

    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @PostMapping(path = "/new")
    public ResponseEntity<String> newCampus(@RequestParam String name, @RequestParam String apiKey) {
        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);

        // Request came from sysadmin, create campus
        if (requestKey.isPresent() && Boolean.TRUE.equals(requestKey.get().getAdmin())) {
            ApiKey authorKey = requestKey.get();
            Campus campus = new Campus();
            campus.setName(name);
            campus.setAuthor(authorKey.getOwnerName());

            campusRepository.save(campus);

            return responses
                    .ok(String.format("New campus \"%s\" (id: %d) stored in database.%n", name, campus.getId()));
        } else // Unauthorised request
            return responses.unauthorised();
    }

    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> deleteCampus(@RequestParam Long id, @RequestParam String apiKey) {
        Optional<ApiKey> receivedApiKey = apiKeyRepository.findById(apiKey);

        // Request came from sysadmin, delete campus
        if (receivedApiKey.isPresent() && Boolean.TRUE.equals(receivedApiKey.get().getAdmin())) {
            if (campusRepository.existsById(id)) {
                campusRepository.deleteById(id);
                return responses.ok(String.format("Campus %d deleted.%n", id));
            } else
                return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", id));
        } else // Unauthorised request
            return responses.unauthorised();
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Campus> getAllCampuses() {
        return campusRepository.findAll();
    }

}
