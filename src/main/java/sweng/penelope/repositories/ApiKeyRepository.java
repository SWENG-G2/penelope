package sweng.penelope.repositories;

import org.springframework.data.repository.CrudRepository;

import sweng.penelope.entities.ApiKey;

/**
 * <code>ApiKeyRepository</code> is a {@link CrudRepository} which handles {@link ApiKey}
 */
public interface ApiKeyRepository extends CrudRepository<ApiKey, String> {
    
}
