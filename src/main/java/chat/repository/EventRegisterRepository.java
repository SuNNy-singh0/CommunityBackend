package chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.EventRegister;

public interface EventRegisterRepository extends MongoRepository<EventRegister, String> {

	
}
