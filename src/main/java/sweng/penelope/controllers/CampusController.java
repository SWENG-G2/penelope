package sweng.penelope.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;
import sweng.penelope.Responses;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.ApiKeyRepository;
import sweng.penelope.repositories.CampusRepository;

/**
 * <code>CampusController</code> handles all Campus endpoints.
 */
@Api(tags = "Campus operations")
@Controller
@RequestMapping(path = "/api/campus")
public class CampusController {
    private Responses responses = new Responses();

    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private CacheManager cacheManager;

    /**
     * Creates a new campus
     * 
     * @param name           The campus name
     * @param authentication {@link Authentication} autowired
     * @return {@link ResponseEntity}
     */
    @PostMapping(path = "/new")
    @ApiOperation("Creates a new campus")
    public ResponseEntity<String> newCampus(@ApiParam("The campus name") @RequestParam String name,
            @ApiIgnore Authentication authentication) {
        Campus campus = new Campus();
        String author = ControllerUtils.getAuthorName(authentication, apiKeyRepository);
        campus.setName(name);
        campus.setAuthor(author);

        campusRepository.save(campus);

        CacheUtils.evictCache(cacheManager, CacheUtils.CAMPUSES_LIST, null);

        return responses
                .ok(String.format("New campus \"%s\" (id: %d) stored in database.%n", name, campus.getId()));

    }

    /**
     * Deletes a campus
     * 
     * @param id The campus ID
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Deletes a campus")
    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> deleteCampus(@ApiParam("The cammpus ID") @RequestParam Long id) {
        if (campusRepository.existsById(id)) {
            campusRepository.deleteById(id);

            CacheUtils.evictCache(cacheManager, CacheUtils.CAMPUSES_LIST, null);

            return responses.ok(String.format("Campus %d deleted.%n", id));
        } else
            return responses.notFound(String.format("Campus %d not found. Nothing to do here...%n", id));
    }
}
