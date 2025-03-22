package chat.enitity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
@Document(collection  = "userdetail")
public class UserDetail {
	@Id
	private String id;
	 private String name; // Foreign key linked to Login table
	 private String Description;
     private String resumeUrl;
	 private String profilePicUrl;
	 private String lastContest;
	 private int monthlyPerformance;
     private String linkedin;
	 private String github;
	 private List<String> skills;
	 private LocalDate date;
	
     
}
