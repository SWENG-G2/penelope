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

import sweng.penelope.entities.Campus;
import sweng.penelope.entities.DataManager;
import sweng.penelope.repositories.CampusRepository;
import sweng.penelope.repositories.DataManagerRepository;

@Controller
@RequestMapping(path = "/api/users")
public class DataManagerController {
    @Autowired
    private DataManagerRepository dataManagerRepository;
    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "/new")
    public ResponseEntity<String> createNewUser(@RequestParam String username, @RequestParam String password,
            @RequestParam(required = false) Boolean sysadmin) {
        DataManager user = new DataManager();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        if (sysadmin != null)
            user.setSysadmin(sysadmin.booleanValue());

        dataManagerRepository.save(user);

        return ResponseEntity.ok().body("User created");
    }

    @DeleteMapping(path = "/remove")
    public ResponseEntity<String> removeUser(@RequestParam String username) {
        return dataManagerRepository.findById(username).map(user -> {
            dataManagerRepository.delete(user);

            return ResponseEntity.ok().body("User deleted");
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping(path = "/addCampus")
    public ResponseEntity<String> addCampusRight(@RequestParam String username, @RequestParam Long campusID) {
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

    @PatchMapping(path = "/removeCampus")
    public ResponseEntity<String> removeCampusRight(@RequestParam String username, @RequestParam Long campusID) {
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
