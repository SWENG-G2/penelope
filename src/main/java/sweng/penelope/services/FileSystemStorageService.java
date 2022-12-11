package sweng.penelope.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sweng.penelope.entities.Bird;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.BirdRepository;
import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.xml.BirdXML;
import sweng.penelope.xml.CampusXML;
import sweng.penelope.xml.CampusesListXML;
import sweng.penelope.xml.CommonXML;
import sweng.penelope.xml.XMLConfiguration;

@Service
public class FileSystemStorageService implements StorageService {

    private final String baseString;

    @Autowired
    private BirdRepository birdRepository;
    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    public FileSystemStorageService(Environment environment) {
        this.baseString = environment.getProperty("storage.base-folder");
    }

    @Override
    public void init() {
        Path basePath = Paths.get(baseString);
        Path videoPath = basePath.resolve("video");
        Path audioPath = basePath.resolve("audio");
        Path imagePath = basePath.resolve("image");
        try {
            if (!Files.exists(basePath))
                Files.createDirectories(basePath);
            if (!Files.exists(videoPath))
                Files.createDirectories(videoPath);
            if (!Files.exists(audioPath))
                Files.createDirectories(audioPath);
            if (!Files.exists(imagePath))
                Files.createDirectories(imagePath);
        } catch (IOException ioException) {
            throw new StorageException("Could not create base directories structure", ioException);
        }
    }

    @Override
    public boolean store(String type, MultipartFile file) {
        Path destinationPath = Paths.get(baseString, type, file.getOriginalFilename());
        try {
            file.transferTo(destinationPath);
            return true;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<Path> loadAll() {
        // Don't allow this
        return null;
    }

    @Override
    public Path load(String type, String fileName) {
        return Paths.get(baseString, type, fileName);
    }

    @Override
    public Resource loadAsResource(String type, String filename) {
        Path filePath = load(type, filename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable())
                return resource;
        } catch (MalformedURLException malformedURLException) {
            throw new StorageException("Invalid file name", malformedURLException);
        }
        return null;
    }

    @Override
    public void deleteAll() {
        // Don't do this
    }

    private BirdXML getBird(Long id) {
        BirdXML birdXML = null;

        Optional<Bird> requestBird = birdRepository.findById(id);
        if (requestBird.isPresent()) {
            Bird bird = requestBird.get();
            XMLConfiguration xmlConfiguration = new XMLConfiguration(bird.getAuthor(), bird.getName(), id);
            birdXML = new BirdXML(xmlConfiguration);

            birdXML.addHeroSlide(bird.getSoundURL(), bird.getHeroImageURL());
        }

        return birdXML;
    }

    private CampusXML getCampus(Long id) {
        CampusXML campusXML = null;

        Optional<Campus> requestCampus = campusRepository.findById(id);
        if (requestCampus.isPresent()) {
            Campus campus = requestCampus.get();
            XMLConfiguration xmlConfiguration = new XMLConfiguration(campus.getAuthor(), campus.getName(), id);
            campusXML = new CampusXML(xmlConfiguration);

            Iterator<Bird> birdsIterator = campus.getBirds().iterator();
            while (birdsIterator.hasNext()) {
                Bird bird = birdsIterator.next();

                campusXML.addBird(bird.getName(), bird.getAboutMe(), bird.getId(), bird.getHeroImageURL());
            }
        }

        return campusXML;
    }

    private CampusesListXML getCampusesList() {
        XMLConfiguration xmlConfiguration = new XMLConfiguration("The Penelope Team", "Campuses list", -1L);
        CampusesListXML campusesListXML = new CampusesListXML(xmlConfiguration);
        Iterator<Campus> campusIterator = campusRepository.findAll().iterator();

        while (campusIterator.hasNext()) {
            Campus campus = campusIterator.next();
            campusesListXML.addCampus(campus.getName(), campus.getId());
        }

        return campusesListXML;
    }

    @Override
    public Resource loadAsResourceFromDB(String type, Long id) {
        CommonXML xml = null;
        if (type.equals("campus"))
            xml = getCampus(id);
        else if (type.equals("bird"))
            xml = getBird(id);
        else
            xml = getCampusesList();

        if (xml != null) {
            byte[] bytesArray = xml.getBytes();
            if (bytesArray != null) {
                return new ByteArrayResource(bytesArray);
            }
        }
        return null;
    }

}
