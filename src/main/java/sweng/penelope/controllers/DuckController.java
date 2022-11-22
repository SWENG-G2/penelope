package sweng.penelope.controllers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import sweng.penelope.entities.Duck;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.repositories.DuckRepository;
import sweng.penelope.xml.CampusXML;
import sweng.penelope.xml.DuckXML;
import sweng.penelope.xml.SlideNotFoundException;
import sweng.penelope.xml.XMLConfiguration;

@Controller
@RequestMapping(path = "/ducks")
public class DuckController {
    private static final String IMAGE_SLIDE = "imageSlide";
    private Responses responses = new Responses();

    @Autowired
    private DuckRepository duckRepository;
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private Environment environment;

    @PostMapping(path = "/new") // POST requests handled at /duck/new
    public ResponseEntity<String> newDuck(@RequestParam String name, @RequestParam String description,
            @RequestParam Long campusId,
            @RequestParam String apiKey) {

        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);

        // Request came from user with valid api key, create the duck
        if (requestKey.isPresent()) {
            ApiKey authorKey = requestKey.get();
            Optional<Campus> campusRequest = campusRepository.findById(campusId);

            if (campusRequest.isPresent()) {
                Campus campus = campusRequest.get();

                Duck duck = new Duck();
                duck.setDescription(description);
                duck.setName(name);
                duck.setCampus(campus);

                duckRepository.save(duck);

                try {
                    DuckXML duckXML = new DuckXML(duck.getName(), authorKey.getOwnerName());
                    duckXML.addSlide("1000", "1000", IMAGE_SLIDE);
                    duckXML.addImage(IMAGE_SLIDE, "imageURL", "800", "800", "100", "100");
                    duckXML.addAudio(IMAGE_SLIDE, "audioURL", "950", "0");

                    // Check directory structure exists
                    String baseFolder = environment.getProperty("storage.base-folder");
                    Path penelopeBaseFolder = Paths.get(baseFolder);
                    Path ducksFolder = Paths.get(baseFolder + String.format("ducks/%s", campus.getName()));
                    if (!Files.exists(penelopeBaseFolder)) {
                        Files.createDirectories(penelopeBaseFolder);
                    }
                    if (!Files.exists(ducksFolder)) {
                        Files.createDirectories(ducksFolder);
                    }

                    // Write duck xml
                    String fileName = String.format("%d.xml", duck.getId());
                    OutputFormat format = OutputFormat.createPrettyPrint();
                    BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(ducksFolder.toString(), fileName),
                            StandardCharsets.UTF_8);
                    XMLWriter xmlWriter = new XMLWriter(bufferedWriter, format);
                    xmlWriter.write(duckXML.getDocument());
                    xmlWriter.close();

                    // Update Campus xml
                    XMLConfiguration xmlConfiguration = new XMLConfiguration(authorKey.getOwnerName(), campus.getName(),
                            campusId);
                    CampusXML campusXML = new CampusXML(
                            environment, xmlConfiguration);
                    campusXML.addDuck(name, description, duck.getId(), "imageURL");
                    campusXML.write();
                } catch (SlideNotFoundException slideNotFoundException) {
                    slideNotFoundException.printStackTrace();
                    return responses.internalServerError("Something went wrong when generating the XML file.\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    return responses.internalServerError("File system error\n");
                }

                String responseMessage = String.format(
                        "New duck \"%s\"(id: %d) with description: \"%s\" stored in the database.%n",
                        name,
                        duck.getId(), description);

                return responses.ok(responseMessage);
            } else
                return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));
        } else // Unauthorised request
            return responses.unauthorised();
    }

    @GetMapping(path = "/all") // Get all the ducks
    public @ResponseBody Iterable<Duck> getAllDucks() {
        return duckRepository.findAll();
    }

    @GetMapping(path = "/campus")
    public ResponseEntity<String> getDucksListByCampus(@RequestParam Long campusId) {
        return responses.ok("");
    }

    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeDuck(@RequestParam Long id, @RequestParam String apiKey) {
        Optional<Duck> requestDuck = duckRepository.findById(id);
        Optional<ApiKey> requestKey = apiKeyRepository.findById(apiKey);
        // Request came from user with valid api key, remove the duck
        if (requestKey.isPresent()) {
            if (requestDuck.isPresent()) {
                Duck duck = requestDuck.get();
                ApiKey authorKey = requestKey.get();
                Campus campus = duck.getCampus();
                duckRepository.delete(duck);

                try {
                    XMLConfiguration xmlConfiguration = new XMLConfiguration(authorKey.getOwnerName(), campus.getName(),
                            campus.getId());
                    CampusXML campusXML = new CampusXML(
                            environment, xmlConfiguration);
                    campusXML.removeDuck(id);
                    campusXML.write();

                    // Remove duck XML
                    String baseFolder = environment.getProperty("storage.base-folder");
                    Path duckPath = Paths.get(baseFolder + String.format("ducks/%s/%d.xml", campus.getName(), id));
                    Files.deleteIfExists(duckPath);
                } catch (SlideNotFoundException | IOException exception) {
                    exception.printStackTrace();
                    return responses.notFound("Could not remove duck from XML records.\n");
                }
                return responses.ok(String.format("Duck %d removed from database and XML records.%n", id));
            }
            return responses.notFound(String.format("Duck %d not found. Nothing to do here...%n", id));
        } else // Unauthorised request
            return responses.unauthorised();
    }
}
