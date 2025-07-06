package chat.controller;

import chat.enitity.MCQ;
import chat.repository.MCQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/mcq")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MCQCreateController {

    @Autowired
    private MCQRepository mcqRepository;

    // 1️⃣ Upload MCQ (Admin Only)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadMCQ(@RequestBody MCQ mcq) {
    	LocalDate mcqDate = (mcq.getDate() != null) ? mcq.getDate() : LocalDate.now();
        Optional<MCQ> existingMCQ = mcqRepository.findByCommunityAndDate(mcq.getCommunity(), mcqDate);

        if (existingMCQ.isPresent()) {
            return ResponseEntity.badRequest().body("MCQ already uploaded for " + mcq.getCommunity() + " today.");
        }

        mcq.setDate(mcqDate);
        mcqRepository.save(mcq);
        return ResponseEntity.ok("MCQ uploaded successfully for " + mcq.getCommunity());
    }

    @GetMapping("/daily/{community}")
    public ResponseEntity<?> getDailyMCQ(@PathVariable String community) {
        Optional<MCQ> mcq = mcqRepository.findByCommunityAndDate(community, LocalDate.now());

        if (mcq.isPresent()) {
            return ResponseEntity.ok(mcq.get());
        } else {
            return ResponseEntity.status(404).body("No MCQ found for " + community + " today.");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllMCQs() {
        try {
            return ResponseEntity.ok(mcqRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching MCQs: " + e.getMessage());
        }
    }
 // 3️⃣ Delete MCQ by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMCQ(@PathVariable String id) {
        if (!mcqRepository.existsById(id)) {
            return ResponseEntity.status(404).body("MCQ with ID " + id + " not found.");
        }

        mcqRepository.deleteById(id);
        return ResponseEntity.ok("MCQ with ID " + id + " deleted successfully.");
    }


}
