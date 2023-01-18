package sweng.penelope.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private CacheManager cacheManager;

    @PostMapping(path = "{campusId}/new")
    public ResponseEntity<String> newDuck(@RequestParam String name, @RequestParam String listImageURL,
            @RequestParam String heroImageURL,
            @RequestParam String soundURL,
            @RequestParam String aboutMe, @RequestParam String aboutMeVideoURL, @RequestParam String location,
            @RequestParam String locationImageURL, @RequestParam String diet, @RequestParam String dietImageURL,
            @PathVariable Long campusId,
            Authentication authentication) {

        Optional<Campus> campusRequest = campusRepository.findById(campusId);

        if (campusRequest.isPresent()) {
            Campus campus = campusRequest.get();
            String author = ControllerUtils.getAuthorName(authentication, apiKeyRepository);

            if (name.length() > 20)
                return ResponseEntity.unprocessableEntity().body("The bird's name cannot exceed 20 characters");

            Bird bird = new Bird(name, listImageURL, heroImageURL, soundURL, aboutMe, aboutMeVideoURL, location,
                    locationImageURL,
                    diet, dietImageURL, campus, author);

            birdRepository.save(bird);

            CacheUtils.evictCache(cacheManager, CacheUtils.CAMPUSES, campusId);

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
            Long previousCampus = bird.getCampus().getId();

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

            Long currentCampus = bird.getCampus().getId();
            if (!currentCampus.equals(previousCampus))
                CacheUtils.evictCache(cacheManager, CacheUtils.CAMPUSES, currentCampus);

            CacheUtils.evictCache(cacheManager, CacheUtils.CAMPUSES, previousCampus);

            CacheUtils.evictCache(cacheManager, CacheUtils.BIRDS, bird.getId());

            return ResponseEntity.ok().body(String.format("Bird \"%s\" updated%n", bird.getName()));
        } else
            return responses.notFound(String.format("Bird %d not found. Nothing to do here...%n", id));
    }

    @DeleteMapping(path = "{campusId}/remove")
    public ResponseEntity<String> removeDuck(@RequestParam Long id,
            @PathVariable Long campusId) {
        Optional<Bird> requestDuck = birdRepository.findById(id);

        if (requestDuck.isPresent()) {
            Bird bird = requestDuck.get();

            CacheUtils.evictCache(cacheManager, CacheUtils.CAMPUSES, bird.getCampus().getId());
            CacheUtils.evictCache(cacheManager, CacheUtils.BIRDS, bird.getId());

            birdRepository.delete(bird);

            return ResponseEntity.ok().body(String.format("Bird %d removed from database.%n", id));
        }
        return responses.notFound(String.format("Bird %d not found. Nothing to do here...%n", id));
    }
}
