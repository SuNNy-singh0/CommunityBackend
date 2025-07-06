package chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.Contest;

public interface ContestRepository extends MongoRepository<Contest, String> {

}
