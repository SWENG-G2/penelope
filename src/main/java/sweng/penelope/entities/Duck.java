package sweng.penelope.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Duck {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String imageURL;
    private String description;

}
