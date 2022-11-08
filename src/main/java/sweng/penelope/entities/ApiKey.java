package sweng.penelope.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApiKey {
    @Id
    private String key;

    private String ownerName;    
}
