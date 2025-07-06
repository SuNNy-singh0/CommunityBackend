package chat.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.JobPost;

public interface JobRepository extends MongoRepository<JobPost, String>{
	 List<JobPost> findByTagIn(List<String> skills); // Find jobs by user skills
	    List<JobPost> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
