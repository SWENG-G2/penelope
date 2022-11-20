package sweng.penelope.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Campus campus;
}
