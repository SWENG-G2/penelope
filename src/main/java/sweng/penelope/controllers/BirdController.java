package sweng.penelope.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sweng.penelope.Responses;
import sweng.penelope.entities.ApiKey;
import sweng.penelope.entities.Campus;
import sweng.penelope.entities.Bird;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.repositories.BirdRepository;
import sweng.penelope.xml.CampusXML;
import sweng.penelope.xml.BirdXML;
import sweng.penelope.xml.SlideNotFoundException;
import sweng.penelope.xml.XMLConfiguration;
import sweng.penelope.xml.XMLInitialisationException;

@Controller
@RequestMapping(path = "/api/ducks")
public class BirdController {
    private Responses responses = new Responses();

    @Autowired
    private BirdRepository birdRepository;
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private Environment environment;

    @PostMapping(path = "/new") // POST requests handled at /duck/new
    public ResponseEntity<String> newDuck(@RequestParam String name, @RequestParam String heroImageURL,
            @RequestParam String soundURL,
            @RequestParam String aboutMe, @RequestParam String aboutMeVideoURL, @RequestParam String location,
            @RequestParam String locationImageURL, @RequestParam String diet, @RequestParam String dietImageURL,
            @RequestParam Long campusId,
            @RequestParam String apiKey) {

        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);

        // Request came from user with valid api key, create the duck
        if (requestKey.isPresent()) {
            ApiKey authorKey = requestKey.get();
            Optional<Campus> campusRequest = campusRepository.findById(campusId);

            if (campusRequest.isPresent()) {
                Campus campus = campusRequest.get();

                Bird bird = new Bird(name, heroImageURL, soundURL, aboutMe, aboutMeVideoURL, location, locationImageURL,
                        diet, dietImageURL, campus, authorKey.getOwnerName());

                birdRepository.save(bird);

                // XML logic to be moved
                // XMLConfiguration duckXmlConfiguration = new
                // XMLConfiguration(authorKey.getOwnerName(), duck.getName(),
                // duck.getId());

                // Path ducksPath = Paths.get("ducks");
                // Path ducksCampusPath = Paths.get(campus.getName());
                // Path destinationPath = ducksPath.resolve(ducksCampusPath);

                // try {
                // DuckXML duckXML = new DuckXML(environment, duckXmlConfiguration,
                // destinationPath);
                // duckXML.addHeroSlide("audioURL", "imageURL");
                // duckXML.write();

                // // Update Campus xml
                // XMLConfiguration campusXmlConfiguration = new
                // XMLConfiguration(authorKey.getOwnerName(),
                // campus.getName(),
                // campusId);
                // CampusXML campusXML = new CampusXML(
                // environment, campusXmlConfiguration);
                // campusXML.addDuck(name, description, duck.getId(), "imageURL");
                // campusXML.write();
                // } catch (IOException ioException) {
                // ioException.printStackTrace();
                // return responses.internalServerError("File system error\n");
                // } catch (XMLInitialisationException xmlInitialisationException) {
                // xmlInitialisationException.printStackTrace();
                // return responses.internalServerError("XML initialisation error\n");
                // }

                // String responseMessage = String.format(
                // "New duck \"%s\"(id: %d) with description: \"%s\" stored in the database.%n",
                // name,
                // duck.getId(), description);

                // return responses.ok(responseMessage);
                return ResponseEntity.ok().body(String.format("Bird \"%s\" created with id %d%n", name, bird.getId()));
            } else
                return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));
        } else // Unauthorised request
            return responses.unauthorised();
    }

    @PatchMapping(path = "/edit")
    public ResponseEntity<String> updateDuck(
            @RequestParam Long id,
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> heroImageURL,
            @RequestParam Optional<String> soundURL,
            @RequestParam Optional<String> aboutMe, @RequestParam Optional<String> aboutMeVideoURL,
            @RequestParam Optional<String> location,
            @RequestParam Optional<String> locationImageURL, @RequestParam Optional<String> diet,
            @RequestParam Optional<String> dietImageURL,
            @RequestParam String apiKey) {
        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);

        // Request came from user with valid api key, create the duck
        if (requestKey.isPresent()) {
            ApiKey authorKey = requestKey.get();
            Optional<Bird> requestBird = birdRepository.findById(id);
            if (requestBird.isPresent()) {
                Bird bird = requestBird.get();

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

                bird.setAuthor(authorKey.getOwnerName());

                birdRepository.save(bird);
                return ResponseEntity.ok().body(String.format("Bird \"%s\" updated%n", bird.getName()));
            } else
                return responses.notFound(String.format("Bird %d not found. Nothing to do here...%n", id));
        }
        return responses.unauthorised();
    }

    @GetMapping(path = "/all") // Get all the ducks
    public @ResponseBody Iterable<Bird> getAllDucks() {
        return birdRepository.findAll();
    }

    @GetMapping(path = "/campus")
    public ResponseEntity<String> getDucksListByCampus(@RequestParam Long campusId) {
        return responses.ok("");
    }

    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeDuck(@RequestParam Long id, @RequestParam String apiKey) {
        Optional<Bird> requestDuck = birdRepository.findById(id);
        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);
        // Request came from user with valid api key, remove the duck
        if (requestKey.isPresent()) {
            if (requestDuck.isPresent()) {
                Bird bird = requestDuck.get();
                ApiKey authorKey = requestKey.get();
                Campus campus = bird.getCampus();
                birdRepository.delete(bird);

                // XML logic to be moved
                // try {
                // XMLConfiguration xmlConfiguration = new
                // XMLConfiguration(authorKey.getOwnerName(), campus.getName(),
                // campus.getId());
                // CampusXML campusXML = new CampusXML(
                // environment, xmlConfiguration);
                // campusXML.removeDuck(id);
                // campusXML.write();

                // // Remove duck XML
                // String baseFolder = environment.getProperty("storage.base-folder");
                // Path basePath = Paths.get(baseFolder);
                // Path ducksPath = Paths.get("ducks");
                // Path ducksCampusPath = Paths.get(campus.getName());
                // Path fileNamePath = Paths.get(String.format("%d.xml", id));
                // // Absolute path to file
                // Path filePath =
                // basePath.resolve(ducksPath.resolve(ducksCampusPath.resolve(fileNamePath)));
                // Files.deleteIfExists(filePath);
                // } catch (SlideNotFoundException slideNotFoundException) {
                // slideNotFoundException.printStackTrace();
                // return responses.notFound("Could not find requested duck in XML record.\n");
                // } catch (IOException ioException) {
                // ioException.printStackTrace();
                // return responses.internalServerError("File system error\n");
                // } catch (XMLInitialisationException xmlInitialisationException) {
                // xmlInitialisationException.printStackTrace();
                // return responses.internalServerError("XML initialisation error\n");
                // }
                return ResponseEntity.ok().body(String.format("Duck %d removed from database and XML records.%n", id));
            }
            return responses.notFound(String.format("Duck %d not found. Nothing to do here...%n", id));
        } else // Unauthorised request
            return responses.unauthorised();
    }
}
