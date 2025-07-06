package chat.controller;

import chat.enitity.Contest;
import chat.enitity.JobPost;
import chat.repository.ContestRepository;
import chat.service.S3StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contests")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ContestController {

    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private S3StorageService s3Service;
    // 1️⃣ Create a New Contest
//    @PostMapping("/create")
//    public ResponseEntity<String> createContest(@RequestBody Contest contest) {
//        contestRepository.save(contest);
//        return ResponseEntity.ok("Contest added successfully.");
//    }
    @PostMapping("/post")
    public ResponseEntity<?> createJob(@RequestParam(value = "file", required = false) MultipartFile file,
                                       @RequestParam("description") String description,
                                       @RequestParam("communitytype") String communityType,
                                       @RequestParam("date")  LocalDate date,
                                       @RequestParam("time")  LocalTime time,
                                       @RequestParam("heading") String heading,
                                       @RequestParam("duration") Integer duration,
                                       @RequestParam("difficultyLevel") String difficultyLevel,
                                       @RequestParam("prizes") String prizes
                                       ) { // Fixed sourcelink param
        try {
            String imageUrl = ""; // Default empty if no file is uploaded

            // Upload only if an image is provided
            if (file != null && !file.isEmpty()) {
                imageUrl = s3Service.uploadFile(file);
            }

            // Create and save job post
            Contest contest= new Contest();
            contest.setHeading(heading);
            contest.setDescription(description);
            contest.setDate(date);
            contest.setTime(time);
            contest.setDuration(duration);
            contest.setDifficultyLevel(difficultyLevel);
            contest.setPrizes(prizes);
            contest.setCommunitytype(communityType);
            contest.setSourcelink(imageUrl);
              // Fixed missing assignment

            contestRepository.save(contest);
            return ResponseEntity.ok("Job posted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading job post: " + e.getMessage());
        }
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable String id) {
        try {
            Optional<Contest> Contestoptional = contestRepository.findById(id);
            if (Contestoptional.isPresent()) {
                Contest contestPost = Contestoptional.get();

               

                contestRepository.deleteById(id);
                return ResponseEntity.ok("Job post deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Job post not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting job post: " + e.getMessage());
        }
    }
}
