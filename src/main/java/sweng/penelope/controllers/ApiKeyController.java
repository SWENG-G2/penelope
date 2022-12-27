package sweng.penelope.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sweng.penelope.Responses;
import sweng.penelope.entities.ApiKey;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.CampusRepository;

@Controller
@RequestMapping(path = "/api/apikeys")
public class ApiKeyController {
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private CampusRepository campusRepository;

    private static final String[] CHARS = "abcdefghijklmnoprstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private static final int PUBLIC_KEY_LENGTH = 10;
    private static final int PRIVATE_KEY_LENGTH = 20;

    private String generateString(int length, SecureRandom secureRandom) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < PUBLIC_KEY_LENGTH; i++) {
            builder.append(CHARS[secureRandom.nextInt(length)]);
        }

        return builder.toString();
    }

    @PostMapping(path = "/new")
    public ResponseEntity<String> createNewApiKey(@RequestParam Boolean admin, @RequestParam String ownerName) {
        ApiKey apiKey = new ApiKey();
        apiKey.setAdmin(admin);
        apiKey.setOwnerName(ownerName);

        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            String publicKey;
            String privateKey;

            while (true) {
                publicKey = generateString(PUBLIC_KEY_LENGTH, secureRandom);

                if (!apiKeyRepository.findById(publicKey).isPresent())
                    break;
            }

            privateKey = generateString(PRIVATE_KEY_LENGTH, secureRandom);

            apiKey.setKey(publicKey);
            apiKey.setSecret(privateKey);

            apiKeyRepository.save(apiKey);

            return ResponseEntity.ok().body(String.format("%s:%s%n", publicKey, privateKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeApiKey(@RequestParam String publicKey) {
        Optional<ApiKey> requestedKeyToBeDeleted = apiKeyRepository.findById(publicKey);

        if (requestedKeyToBeDeleted.isPresent()) {
            ApiKey keyToBeDeleted = requestedKeyToBeDeleted.get();

            apiKeyRepository.delete(keyToBeDeleted);

            return ResponseEntity.ok().body(String.format("Key %s deleted.%n", publicKey));
        }

        Responses responses = new Responses();
        return responses.notFound(String.format("Key %s was not found. Nothing to do here...%n", publicKey));
    }

    @PatchMapping(path = "/addCampus")
    public ResponseEntity<String> addCampusToKey(@RequestParam Long campusId, @RequestParam String publicKey) {
        Optional<ApiKey> requestKey = apiKeyRepository.findById(publicKey);
        Optional<Campus> requestCampus = campusRepository.findById(campusId);

        Responses responses = new Responses();

        if (requestKey.isEmpty())
            return responses.notFound(String.format("Public key %s not found. Nothing to do here...%n", publicKey));

        if (requestCampus.isEmpty())
            return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));

        ApiKey apiKey = requestKey.get();
        Campus campus = requestCampus.get();

        Set<Campus> campusesSet = apiKey.getCampuses();
        campusesSet.add(campus);

        apiKey.setCampuses(campusesSet);

        apiKeyRepository.save(apiKey);

        return ResponseEntity.ok().body(String.format("Campus %d added to key %s.%n", campusId, publicKey));
    }

    @PatchMapping(path = "/removeCampus")
    public ResponseEntity<String> removeCampusFromKey(@RequestParam Long campusId, @RequestParam String publicKey) {
        Optional<ApiKey> requestKey = apiKeyRepository.findById(publicKey);
        Optional<Campus> requestCampus = campusRepository.findById(campusId);

        Responses responses = new Responses();

        if (requestKey.isEmpty())
            return responses.notFound(String.format("Public key %s not found. Nothing to do here...%n", publicKey));

        if (requestCampus.isEmpty())
            return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));

        ApiKey apiKey = requestKey.get();
        Campus campus = requestCampus.get();

        Set<Campus> campusesSet = apiKey.getCampuses();

        if (campusesSet.contains(campus)) {
            campusesSet.remove(campus);

            apiKey.setCampuses(campusesSet);

            apiKeyRepository.save(apiKey);

            return ResponseEntity.ok().body(String.format("Campus %d removed from key %s.%n", campusId, publicKey));
        }

        return responses.notFound(String.format("Key %s does not have rights on campus %d.%n", publicKey, campusId));
    }
}
