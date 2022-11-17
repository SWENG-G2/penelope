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
import sweng.penelope.entities.Duck;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.DuckRepository;

@Controller
@RequestMapping(path = "/ducks")
public class DuckController {
    private Responses responses = new Responses();

    @Autowired
    private DuckRepository duckRepository;
    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @PostMapping(path = "/new") // POST requests handled at /duck/new
    public ResponseEntity<String> newDuck(@RequestParam String name, @RequestParam String description,
            @RequestParam String apiKey) {

        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);

        // Request came from user with valid api key, create the duck
        if (requestKey.isPresent()) {
            Duck duck = new Duck();
            duck.setDescription(description);
            duck.setName(name);

            duckRepository.save(duck);

            String responseMessage = String.format(
                    "New duck \"%s\"(id: %d) with description: \"%s\" stored in the database.%n",
                    name,
                    duck.getId(), description);

            return responses.ok(responseMessage);

        } else // Unauthorised request
            return responses.unauthorised();

    }

    @GetMapping(path = "/all") // Get all the ducks
    public @ResponseBody Iterable<Duck> getAllDucks() {
        return duckRepository.findAll();
    }

    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeDuck(@RequestParam Long id, @RequestParam String apiKey) {
        // Request came from user with valid api key, remove the duck
        if (apiKeyRepository.findById(apiKey).isPresent()) {
            if (duckRepository.existsById(id)) {
                duckRepository.deleteById(id);
                return responses.ok(String.format("Duck %d deleted.%n", id));
            }
            return responses.notFound(String.format("Duck %d not found. Nothing to do here...%n", id));
        } else // Unauthorised request
            return responses.unauthorised();
    }
}
