package sweng.penelope.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApiKey {
    @Id
    private String key;

    private String secret;
    private String ownerName;

    @Column(columnDefinition = "boolean default false")
    private Boolean admin;

    @ManyToMany
    private Set<Campus> campuses = new HashSet<>();
}
