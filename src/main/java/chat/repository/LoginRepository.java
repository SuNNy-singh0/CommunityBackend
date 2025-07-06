package chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.Login;

public interface LoginRepository extends MongoRepository<Login, String> {
//	 Login findByUsernameAndPassword(String username, String password);
	    
	   
	    Login findByUsername(String username);
	    Login findByEmail(String email);
}
