package chat.enitity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "mcq")
public class MCQ {
    @Id
    private String id;
    private String community; // MERN, DSA, Java
    private String question;
    private List<String> options;
    private String correctAnswer;
    private LocalDate date; // Ensures one MCQ per day per community
}
