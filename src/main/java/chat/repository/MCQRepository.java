package chat.repository;

import chat.enitity.MCQ;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface MCQRepository extends MongoRepository<MCQ, String> {
    Optional<MCQ> findByCommunityAndDate(String community, LocalDate date);
}
