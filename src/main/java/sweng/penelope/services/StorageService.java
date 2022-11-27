package sweng.penelope.services;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();

	void store(MultipartFile file);

	Stream<Path> loadAll();

	Path load(String fileHome, String filename);

	Resource loadAsResource(String fileHome, String fileName);

	void deleteAll();
}
