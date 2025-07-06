package chat.controller;

import chat.enitity.MCQ;
import chat.enitity.UserDetail;
import chat.repository.MCQRepository;
import chat.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/mcq")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MCQController {

    @Autowired
    private UserDetailRepository userDetailRepository;
    
    @Autowired
    private MCQRepository mcqRepository;
    @PostMapping("/attempt")
    public ResponseEntity<String> attemptMCQ(@RequestHeader("username") String username,
                                             @RequestParam("community") String community,
                                             @RequestParam("answer") String userAnswer) {
        Optional<UserDetail> userOpt = userDetailRepository.findByName(username);
        Optional<MCQ> mcqOpt = mcqRepository.findByCommunityAndDate(community, LocalDate.now());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (mcqOpt.isEmpty()) {
            return ResponseEntity.status(404).body("No MCQ found for " + community + " today.");
        }

        UserDetail user = userOpt.get();
        MCQ mcq = mcqOpt.get();
        LocalDate today = LocalDate.now();

        // Check if already attempted today's MCQ for this community
        if (user.hasAttemptedMcq(community, today)) {
            return ResponseEntity.badRequest().body("Already attempted today's MCQ in " + community);
        }

        // ✅ Convert correctAnswer and userAnswer to Integer for comparison
        boolean isCorrect = Integer.parseInt(mcq.getCorrectAnswer()) == Integer.parseInt(userAnswer);
        int rewardCoins = isCorrect ? 30 : 10;

        // Log attempt and award coins
        user.addMcqAttempt(community, today);
        user.addCoins(rewardCoins);
        userDetailRepository.save(user);

        return ResponseEntity.ok("MCQ attempted successfully! " + (isCorrect ? "Correct! +30 Coins" : "Wrong! +10 Coins"));
    }

    @GetMapping("/attempt/status")
    public ResponseEntity<?> checkMCQAttempt(@RequestHeader("username") String username,
                                             @RequestParam("community") String community) {
        Optional<UserDetail> userOpt = userDetailRepository.findByName(username);

        if (userOpt.isPresent()) {
            UserDetail user = userOpt.get();
            LocalDate today = LocalDate.now();

            // Check if the user has attempted today's MCQ for the given community
            boolean hasAttempted = user.hasAttemptedMcq(community, today);

            if (hasAttempted) {
                return ResponseEntity.ok("User has already attempted today's MCQ in " + community);
            } else {
                return ResponseEntity.ok("User has NOT attempted today's MCQ in " + community);
            }
        }

        return ResponseEntity.status(404).body("User not found");
    }

}
