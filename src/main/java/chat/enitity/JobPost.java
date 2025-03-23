package chat.enitity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection  = "Job")
public class JobPost {
private String id;
private String postimagelink;
private String description;
private String Communitytype;
private LocalDate date;
private List<String> tag;
@Field("sourcelink")
private String Sourcelink;
}
