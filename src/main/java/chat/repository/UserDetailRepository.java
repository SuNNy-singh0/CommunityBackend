package chat.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.UserDetail;

public interface UserDetailRepository extends MongoRepository<UserDetail, String>{
   Optional<UserDetail> findByName(String name);
}
