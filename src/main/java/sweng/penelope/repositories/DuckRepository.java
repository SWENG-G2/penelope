package sweng.penelope.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import sweng.penelope.entities.Campus;
import sweng.penelope.entities.Duck;

public interface DuckRepository extends CrudRepository<Duck, Long> {
    List<Duck> findByCampus(Campus campus);
}
