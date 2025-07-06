package chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import chat.enitity.EventRegister;
import chat.repository.EventRegisterRepository;

@RestController
@RequestMapping("/event")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class EventController {
	@Autowired
	private EventRegisterRepository repo;
	
	 @PostMapping("/create")
   public ResponseEntity<String> createContest(@RequestBody EventRegister event) {
       repo.save(event);
       return ResponseEntity.ok("Contest added successfully.");
   }
	 @GetMapping("/all")
	    public ResponseEntity<List<EventRegister>> getAllContests() {
	        return ResponseEntity.ok(repo.findAll());
	    }

}
