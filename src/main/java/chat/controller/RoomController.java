package chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chat.enitity.Message;
import chat.enitity.Room;
import chat.repository.RoomRepository;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@CrossOrigin("http://localhost:5173/")
public class RoomController {

    private final RoomRepository repo;

    // Constructor injection of RoomRepo
    public RoomController(RoomRepository repo) {
    	this.repo = repo;
    }

    // Create room endpoint
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody String roomId) {
        // Check if the room already exists
       if(repo.findByRoomId(roomId)!=null) {
    	   return ResponseEntity.badRequest().body("room already Exist");
       }
       Room room  = new Room();
       room.setRoomId(roomId);
       repo.save(room);
       return ResponseEntity.ok(room);
    }

   

	// Get room details endpoint
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        // Retrieve the room by roomId
        Room room = repo.findByRoomId(roomId);  
        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }
        return ResponseEntity.ok(room);
    }

    // Get messages from the room with pagination
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        
        // Get the list of messages for the room
        Room room  = repo.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().build();  // Return empty response if no messages are found
        }
        List<Message> message = room.getMessage();
        // Implement pagination logic
        int start = page * size;
        int end = Math.min((page + 1) * size, message.size());  // Ensure we don't exceed the size of the list

        List<Message> paginatedMessages = message.subList(start, end);
        return ResponseEntity.ok(paginatedMessages);
    }
}
