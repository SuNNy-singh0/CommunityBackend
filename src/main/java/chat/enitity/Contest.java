package chat.enitity;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "contest")
public class Contest {
    @Id
    private String id;
    private String heading;
    private String description;
    private LocalDate date;  // Changed from `Date` to `LocalDate`
    private LocalTime time;  // Changed from `Time` to `LocalTime`
    private int duration;
    private String difficultyLevel;
    private String prizes;
    private String communitytype;
    private String sourcelink;
}
