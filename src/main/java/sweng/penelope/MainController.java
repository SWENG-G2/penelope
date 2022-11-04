package sweng.penelope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sweng.penelope.entities.Duck;
import sweng.penelope.repositories.DuckRepository;

@Controller
@RequestMapping(path = "/ducks")
public class MainController {
    @Autowired
    private DuckRepository duckRepository;

    @PostMapping(path = "/new") // POST requests handled at /duck/new
    public @ResponseBody String newDuck(@RequestParam String name, @RequestParam String description) {
        Duck duck = new Duck();
        duck.setDescription(description);
        duck.setName(name);

        duckRepository.save(duck);
        return String.format("New duck \"%s\"(id: %d) with description: \"%s\" stored in the database.%n", name, duck.getId(), description);
    }

    @GetMapping(path = "/all") // Get all the ducks
    public @ResponseBody Iterable<Duck> getAllDucks() {
        return duckRepository.findAll();
    }

    @DeleteMapping(path = "/remove")
    public @ResponseBody String removeDuck(@RequestParam Long id) {
        if (duckRepository.existsById(id)) {
            duckRepository.deleteById(id);
            return String.format("Duck %d deleted.%n", id);
        }
        return String.format("Duck %d not found. Nothing to do here...%n", id);
    }
}
