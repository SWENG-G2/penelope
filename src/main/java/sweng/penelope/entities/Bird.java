package sweng.penelope.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Bird {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bird information
    private String name;
    private String heroImageURL;
    private String soundURL;
    private String aboutMe;
    private String aboutMeVideoURL;
    private String location;
    private String locationImageURL;
    private String diet;
    private String dietImageURL;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Campus campus;

    // Metadata
    private String author;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @UpdateTimestamp
    private Date date;

    /**
     * A Bird entry in the database
     * 
     * @param name             Bird's name
     * @param heroImageURL     URL to hero image
     * @param soundURL         URL to bird's sound
     * @param aboutMe          Text content of "About me" section
     * @param aboutMeVideoURL  URL to "About me" video
     * @param location         Text content of "Location" section
     * @param locationImageURL URL to "Location" image
     * @param diet             Text content of "Diet" section
     * @param dietImageURL     URL to "Diet" video
     * @param campus           The campus the bird is related to
     * @param author           The author of this information
     */
    public Bird(String name, String heroImageURL, String soundURL, String aboutMe, String aboutMeVideoURL,
            String location, String locationImageURL, String diet, String dietImageURL, Campus campus, String author) {
        // SonarLint rule can be ignored since parameters are being passed as data to be
        // persisted on DB.
        this.name = name;
        this.heroImageURL = heroImageURL;
        this.soundURL = soundURL;
        this.aboutMe = aboutMe;
        this.aboutMeVideoURL = aboutMeVideoURL;
        this.location = location;
        this.locationImageURL = locationImageURL;
        this.diet = diet;
        this.dietImageURL = dietImageURL;
        this.campus = campus;
        this.author = author;
    }

    public Bird() {
        // Empty constructor as per JPA standard
    }
}
