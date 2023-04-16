package sweng.penelope.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import sweng.penelope.entities.Campus;
import sweng.penelope.entities.DataManager;
import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.repositories.DataManagerRepository;

/**
 * <code>DataManagerController</code> handles all DataManager (user) endpoints.
 */
@Controller
@RequestMapping(path = "/api/users")
@Api(tags = "DataManager operations")
@ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", name = "Credentials", required = true, dataType = "java.lang.String")
})
public class DataManagerController {
    @Autowired
    private DataManagerRepository dataManagerRepository;
    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * DataManager creation endpoint.
     * 
     * @param username Human friendly name of the DataManager's owner (i.e. email).
     * @param password DataManager's password.
     * @param sysadmin Whether the new user should have sysadmin priviledges.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("DataManager creation endpoint.")
    @PostMapping(path = "/new")
    public ResponseEntity<String> createNewUser(
            @ApiParam(value = "Human friendly name of the DataManager's owner (i.e. email).") @RequestParam String username,
            @ApiParam(value = "DataManager's password.") @RequestParam String password,
            @ApiParam("Whether the new user should have sysadmin priviledges.") @RequestParam(required = false) Boolean sysadmin) {
        DataManager user = new DataManager();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        if (sysadmin != null)
            user.setSysadmin(sysadmin.booleanValue());

        dataManagerRepository.save(user);

        return ResponseEntity.ok().body("User created");
    }

    /**
     * DataManager removal endpoint.
     * 
     * @param username DataManager's username to remove.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("DataManager removal endpoint.")
    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeUser(
            @ApiParam(value = "DataManager's username to remove.") @RequestParam String username) {
        return dataManagerRepository.findById(username).map(user -> {
            dataManagerRepository.delete(user);

            return ResponseEntity.ok().body("User deleted");
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Grants permissions to access resources under a certain campus to a
     * DataManager.
     * 
     * @param username The DataManager's username.
     * @param campusID The id of the campus the resources belong to.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Grants permissions to access resources under a certain campus to a DataManager.")
    @PatchMapping(path = "/addCampus")
    public ResponseEntity<String> addCampusRight(
            @ApiParam(value = "The DataManager's username.") @RequestParam String username,
            @ApiParam("The id of the campus the resources belong to.") @RequestParam Long campusID) {
        return dataManagerRepository.findById(username).map(user -> {
            return campusRepository.findById(campusID).map(campus -> {
                Set<Campus> campuses = user.getCampuses();
                campuses.add(campus);

                user.setCampuses(campuses);
                dataManagerRepository.save(user);

                return ResponseEntity.ok().body("Rights granted to user");
            }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Removes permissions to access resources under a certain campus from a
     * DataManager.
     * 
     * @param username The DataManager's username.
     * @param campusID The id of the campus the resources belong to.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Removes permissions to access resources under a certain campus from a DataManager.")
    @PatchMapping(path = "/removeCampus")
    public ResponseEntity<String> removeCampusRight(
            @ApiParam(value = "The DataManager's username.") @RequestParam String username,
            @ApiParam("The id of the campus the resources belong to.") @RequestParam Long campusID) {
        return dataManagerRepository.findById(username).map(user -> {
            return campusRepository.findById(campusID).map(campus -> {
                Set<Campus> campuses = user.getCampuses();

                if (!campuses.contains(campus))
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);

                campuses.remove(campus);

                user.setCampuses(campuses);
                dataManagerRepository.save(user);

                return ResponseEntity.ok().body("Rights removed from user");
            }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
