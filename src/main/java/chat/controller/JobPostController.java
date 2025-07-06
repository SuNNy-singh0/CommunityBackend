package chat.controller;

import chat.enitity.JobPost;

import chat.repository.JobRepository;
import chat.service.S3StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class JobPostController {

    @Autowired
    private JobRepository jobPostRepository;
    
    @Autowired
    private S3StorageService s3Service;

    // 1️⃣ Create Job Post (Admin Only)
    @PostMapping("/create")
    public ResponseEntity<?> createJob(@RequestParam(value = "file", required = false) MultipartFile file,
                                       @RequestParam("description") String description,
                                       @RequestParam("communitytype") String communityType,
                                       @RequestParam("tag") List<String> tag,
                                       @RequestParam("sourcelink") String sourcelink) { // Fixed sourcelink param
        try {
            String imageUrl = ""; // Default empty if no file is uploaded

            // Upload only if an image is provided
            if (file != null && !file.isEmpty()) {
                imageUrl = s3Service.uploadFile(file);
            }

            // Create and save job post
            JobPost jobPost = new JobPost();
            jobPost.setPostimagelink(imageUrl);
            jobPost.setDescription(description);
            jobPost.setCommunitytype(communityType);
            jobPost.setDate(LocalDate.now());
            jobPost.setTag(tag);
            jobPost.setSourcelink(sourcelink); // Fixed missing assignment

            jobPostRepository.save(jobPost);
            return ResponseEntity.ok("Job posted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading job post: " + e.getMessage());
        }
    }


    // 2️⃣ Get All Jobs
    @GetMapping("/all")
    public ResponseEntity<List<JobPost>> getAllJobs() {
        return ResponseEntity.ok(jobPostRepository.findAll());
    }

    // 3️⃣ Find Jobs by User Skills
    @GetMapping("/find-by-skills")
    public ResponseEntity<List<JobPost>> findJobsBySkills(@RequestParam List<String> skills) {
        List<JobPost> jobs = jobPostRepository.findByTagIn(skills);
        return ResponseEntity.ok(jobs);
    }

    // 4️⃣ Filter Jobs by Date Range
    @GetMapping("/filter-by-date")
    public ResponseEntity<List<JobPost>> filterJobsByDate(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<JobPost> jobs = jobPostRepository.findByDateBetween(startDate, endDate);
        return ResponseEntity.ok(jobs);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable String id) {
        try {
            Optional<JobPost> jobPostOptional = jobPostRepository.findById(id);
            if (jobPostOptional.isPresent()) {
                JobPost jobPost = jobPostOptional.get();

               

                jobPostRepository.deleteById(id);
                return ResponseEntity.ok("Job post deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Job post not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting job post: " + e.getMessage());
        }
    }
}
