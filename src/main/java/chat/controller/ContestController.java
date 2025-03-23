package chat.controller;

import chat.enitity.Contest;
import chat.repository.ContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contests")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ContestController {

    @Autowired
    private ContestRepository contestRepository;

    // 1️⃣ Create a New Contest
    @PostMapping("/create")
    public ResponseEntity<String> createContest(@RequestBody Contest contest) {
        contestRepository.save(contest);
        return ResponseEntity.ok("Contest added successfully.");
    }

    // 2️⃣ Get All Contests
    @GetMapping("/all")
    public ResponseEntity<List<Contest>> getAllContests() {
        return ResponseEntity.ok(contestRepository.findAll());
    }

//    // 3️⃣ Get Contest by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getContestById(@PathVariable String id) {
//        Optional<Contest> contest = contestRepository.findById(id);
//        return contest.map(ResponseEntity::ok).orElse(ResponseEntity.status(404).body("Contest not found"));
//    }
}
