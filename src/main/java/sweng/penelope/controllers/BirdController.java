package sweng.penelope.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sweng.penelope.Responses;
import sweng.penelope.entities.Bird;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.BirdRepository;
import sweng.penelope.repositories.CampusRepository;

@Controller
@RequestMapping(path = "/api/birds")
public class BirdController {
    private Responses responses = new Responses();

    @Autowired
    private BirdRepository birdRepository;
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private CampusRepository campusRepository;

    @PostMapping(path = "{campusId}/new")
    public ResponseEntity<String> newDuck(@RequestParam String name, @RequestParam String heroImageURL,
            @RequestParam String soundURL,
            @RequestParam String aboutMe, @RequestParam String aboutMeVideoURL, @RequestParam String location,
            @RequestParam String locationImageURL, @RequestParam String diet, @RequestParam String dietImageURL,
            @PathVariable Long campusId,
            Authentication authentication) {

        Optional<Campus> campusRequest = campusRepository.findById(campusId);

        if (campusRequest.isPresent()) {
            Campus campus = campusRequest.get();
            String author = ControllerUtils.getAuthorName(authentication, apiKeyRepository);

            Bird bird = new Bird(name, heroImageURL, soundURL, aboutMe, aboutMeVideoURL, location, locationImageURL,
                    diet, dietImageURL, campus, author);

            birdRepository.save(bird);

            return ResponseEntity.ok().body(String.format("Bird \"%s\" created with id %d%n", name, bird.getId()));
        } else
            return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));
    }

    @PatchMapping(path = "{campusId}/edit")
    public ResponseEntity<String> updateDuck(
            @RequestParam Long id,
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> heroImageURL,
            @RequestParam Optional<String> soundURL,
            @RequestParam Optional<String> aboutMe, @RequestParam Optional<String> aboutMeVideoURL,
            @RequestParam Optional<String> location,
            @RequestParam Optional<String> locationImageURL, @RequestParam Optional<String> diet,
            @RequestParam Optional<String> dietImageURL,
            @PathVariable Long campusId,
            Authentication authentication) {

        Optional<Bird> requestBird = birdRepository.findById(id);
        if (requestBird.isPresent()) {
            Bird bird = requestBird.get();
            String author = ControllerUtils.getAuthorName(authentication, apiKeyRepository);

            // This is 7yo writing python code quality. Look into
            // https://www.baeldung.com/spring-data-partial-update#1-mapping-strategy
            if (name.isPresent())
                bird.setName(name.get());
            if (heroImageURL.isPresent())
                bird.setHeroImageURL(heroImageURL.get());
            if (soundURL.isPresent())
                bird.setSoundURL(soundURL.get());
            if (aboutMe.isPresent())
                bird.setAboutMe(aboutMe.get());
            if (aboutMeVideoURL.isPresent())
                bird.setAboutMeVideoURL(aboutMeVideoURL.get());
            if (location.isPresent())
                bird.setLocation(location.get());
            if (locationImageURL.isPresent())
                bird.setLocationImageURL(locationImageURL.get());
            if (diet.isPresent())
                bird.setDiet(diet.get());
            if (dietImageURL.isPresent())
                bird.setDietImageURL(dietImageURL.get());

            String currentAuthors = bird.getAuthor();
            if (!currentAuthors.contains(author))
                currentAuthors += ", " + author;

            bird.setAuthor(currentAuthors);

            birdRepository.save(bird);
            return ResponseEntity.ok().body(String.format("Bird \"%s\" updated%n", bird.getName()));
        } else
            return responses.notFound(String.format("Bird %d not found. Nothing to do here...%n", id));
    }

    @GetMapping(path = "/all") // Get all the ducks
    public @ResponseBody Iterable<Bird> getAllDucks() {
        return birdRepository.findAll();
    }

    @DeleteMapping(path = "{campusId}/remove")
    public ResponseEntity<String> removeDuck(@RequestParam Long id,
            @PathVariable Long campusId) {
        Optional<Bird> requestDuck = birdRepository.findById(id);

        if (requestDuck.isPresent()) {
            Bird bird = requestDuck.get();
            birdRepository.delete(bird);

            return ResponseEntity.ok().body(String.format("Bird %d removed from database.%n", id));
        }
        return responses.notFound(String.format("Bird %d not found. Nothing to do here...%n", id));
    }
}
