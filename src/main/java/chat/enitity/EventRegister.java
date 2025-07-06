package chat.enitity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "contest")
public class EventRegister {
@Id
private String id;
private String name;
private String emailid;
private String phonenumber;
private String eventname;
}
