package sweng.penelope.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private final String baseString;

    @Autowired
    public FileSystemStorageService(Environment environment) {
        this.baseString = environment.getProperty("storage.base-folder");
    }

    @Override
    public void init() {
        Path basePath = Paths.get(baseString);
        try {
            if (!Files.exists(basePath))
                Files.createDirectories(basePath);
        } catch (IOException ioException) {
            throw new StorageException("Could not create base path", ioException);
        }
    }

    @Override
    public void store(MultipartFile file) {
        // TODO: actually store
    }

    @Override
    public Stream<Path> loadAll() {
        // Don't allow this
        return null;
    }

    @Override
    public Path load(String fileHome, String fileName) {
        return Paths.get(baseString, fileHome, fileName);
    }

    @Override
    public Resource loadAsResource(String fileHome, String filename) {
        Path filePath = load(fileHome, filename);
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

}
