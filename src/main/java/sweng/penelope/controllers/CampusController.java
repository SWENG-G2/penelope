package sweng.penelope.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;
import sweng.penelope.Responses;
import sweng.penelope.entities.Campus;
import sweng.penelope.repositories.CampusRepository;

/**
 * <code>CampusController</code> handles all Campus endpoints.
 */
@Api(tags = "Campus operations")
@Controller
@RequestMapping(path = "/api/campus")
@ApiImplicitParams({
    @ApiImplicitParam(paramType = "header", name = "Credentials", required = true, dataType = "java.lang.String")
})
public class CampusController {
    private Responses responses = new Responses();

    @Autowired
    private CampusRepository campusRepository;
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
        String author = ControllerUtils.getAuthorName(authentication);
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
        Optional<Campus> requestCampus = campusRepository.findById(id);

        return requestCampus.map(campus -> {
            campusRepository.delete(campus);

            return ResponseEntity.ok(String.format("Campus %d deleted.%n", id));
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
