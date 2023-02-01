package sweng.penelope.controllers;

import java.nio.file.FileSystemException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import sweng.penelope.Responses;
import sweng.penelope.auth.RSAUtils;
import sweng.penelope.entities.ApiKey;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.services.StorageService;

/**
 * <code>ApiKeyController</code> handles all APIKeys endpoints.
 */
@Controller
@RequestMapping(path = "/api/apikeys")
@Api(tags = "ApiKey operations")
public class ApiKeyController {
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private StorageService storageService;

    private static final String[] CHARS = "abcdefghijklmnoprstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private static final int IDENTITY_LENGTH = 10;

    /**
     * Generates a secure random {@link String}.
     * 
     * @param length       The desired length for the string.
     * @param secureRandom A {@link SecureRandom} instance.
     * @return The random {@link String}.
     */
    private String generateString(int length, SecureRandom secureRandom) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(CHARS[secureRandom.nextInt(CHARS.length)]);
        }

        return builder.toString();
    }

    /**
     * ApiKey creation endpoint.
     * @param admin Whether the new key should have admin priviledges.
     * @param ownerName Human friendly name of the key's owner.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Creates a new ApiKey")
    @ApiParam(name = "admin", value = "Whether the new key should have admin priviledges")
    @PostMapping(path = "/new")
    public ResponseEntity<String> createNewApiKey(@RequestParam Boolean admin, @RequestParam String ownerName) {
        ApiKey apiKey = new ApiKey();
        apiKey.setAdmin(admin);
        apiKey.setOwnerName(ownerName);

        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            String identity;

            // Make sure identity is unique
            while (true) {
                identity = generateString(IDENTITY_LENGTH, secureRandom);

                if (!apiKeyRepository.findById(identity).isPresent())
                    break;
            }

            KeyPair keyPair = RSAUtils.generateKeys();

            // Store key
            if (!storageService.storeKey(keyPair.getPrivate(), identity))
                throw new FileSystemException("Could not store key");

            PublicKey publicKey = keyPair.getPublic();
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            apiKey.setIdentity(identity);

            apiKeyRepository.save(apiKey);

            // Reply with identity:publick key
            return ResponseEntity.ok().body(String.format("%s:%s%n", identity, publicKeyBase64));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ApiKey removal endpoint.
     * @param identity Identity corresponding to the ApiKey to remove
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Removes an existing ApiKey")
    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeApiKey(@RequestParam String identity) {
        Optional<ApiKey> requestedKeyToBeDeleted = apiKeyRepository.findById(identity);

        if (requestedKeyToBeDeleted.isPresent()) {
            ApiKey keyToBeDeleted = requestedKeyToBeDeleted.get();

            if (storageService.removeKey(keyToBeDeleted.getIdentity())) {
                apiKeyRepository.delete(keyToBeDeleted);

                return ResponseEntity.ok().body(String.format("Key %s deleted.%n", identity));
            } else {
                return ResponseEntity.internalServerError().build();
            }
        }

        Responses responses = new Responses();
        return responses.notFound(String.format("Key %s was not found. Nothing to do here...%n", identity));
    }

    @PatchMapping(path = "/addCampus")
    public ResponseEntity<String> addCampusToKey(@RequestParam Long campusId, @RequestParam String identity) {
        Optional<ApiKey> requestKey = apiKeyRepository.findById(identity);
        Optional<Campus> requestCampus = campusRepository.findById(campusId);

        Responses responses = new Responses();

        if (requestKey.isEmpty())
            return responses.notFound(String.format("Public key %s not found. Nothing to do here...%n", identity));

        if (requestCampus.isEmpty())
            return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));

        ApiKey apiKey = requestKey.get();
        Campus campus = requestCampus.get();

        Set<Campus> campusesSet = apiKey.getCampuses();
        campusesSet.add(campus);

        apiKey.setCampuses(campusesSet);

        apiKeyRepository.save(apiKey);

        return ResponseEntity.ok().body(String.format("Campus %d added to key %s.%n", campusId, identity));
    }

    @PatchMapping(path = "/removeCampus")
    public ResponseEntity<String> removeCampusFromKey(@RequestParam Long campusId, @RequestParam String identity) {
        Optional<ApiKey> requestKey = apiKeyRepository.findById(identity);
        Optional<Campus> requestCampus = campusRepository.findById(campusId);

        Responses responses = new Responses();

        if (requestKey.isEmpty())
            return responses.notFound(String.format("Public key %s not found. Nothing to do here...%n", identity));

        if (requestCampus.isEmpty())
            return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", campusId));

        ApiKey apiKey = requestKey.get();
        Campus campus = requestCampus.get();

        Set<Campus> campusesSet = apiKey.getCampuses();

        if (campusesSet.contains(campus)) {
            campusesSet.remove(campus);

            apiKey.setCampuses(campusesSet);

            apiKeyRepository.save(apiKey);

            return ResponseEntity.ok().body(String.format("Campus %d removed from key %s.%n", campusId, identity));
        }

        return responses.notFound(String.format("Key %s does not have rights on campus %d.%n", identity, campusId));
    }
}
