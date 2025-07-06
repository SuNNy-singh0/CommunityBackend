package chat.enitity;

import org.bson.types.ObjectId;
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
@Document(collection  = "logintable")
public class Login {
	 @Id
	 private String id = new ObjectId().toString(); 
private String username;
private String phonenumber;
private String password;
private String email;

}
