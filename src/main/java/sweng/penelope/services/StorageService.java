package sweng.penelope.services;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sweng.penelope.xml.CommonXML;

public interface StorageService {
	void init();

	boolean store(String type, MultipartFile file);

	Stream<Path> loadAll();

	Path load(String fileHome, String filename);

	Resource loadAsResource(String fileHome, String fileName);

	Resource loadAsResourceFromDB(String type, Long id);

	void deleteAll();
}
