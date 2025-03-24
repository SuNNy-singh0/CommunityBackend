package chat.enitity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 private List<Integer> monthlyPerformance;
     private String linkedin;
	 private String github;
	 private List<String> skills;
	 private LocalDate date;
	 private int coins = 0; // Default to 0
	    private Set<String> mcqAttempts = new HashSet<>(); // Stores attempts in format: "YYYY-MM-DD-MERN"

	    // Add MCQ Attempt for a specific community
	    public void addMcqAttempt(String community, LocalDate today) {
	        this.mcqAttempts.add(today + "-" + community);
	    }

	    // Check if MCQ was already attempted for the day
	    public boolean hasAttemptedMcq(String community, LocalDate today) {
	        return mcqAttempts.contains(today + "-" + community);
	    }

	    // Add coins (30 per MCQ attempt)
	    public void addCoins(int amount) {
	        this.coins += amount;
	    }
	    public void pushDailyCoinsToMonthly() {
	        if (this.monthlyPerformance == null) {
	            this.monthlyPerformance = new ArrayList<>();
	        }
	        this.monthlyPerformance.add(this.coins); // Store today's coins
	    }
	    
}
