package sweng.penelope.repositories;

import org.springframework.data.repository.CrudRepository;

import sweng.penelope.entities.Duck;

public interface DuckRepository extends CrudRepository<Duck, Long> {

}
