package chat.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import chat.enitity.UserDetail;
import chat.repository.UserDetailRepository;
import chat.service.S3StorageService;

@RestController
@RequestMapping("/usercontrol")
@CrossOrigin(origins = {"http://localhost:5173", "https://asliengineers.vercel.app"}, allowCredentials = "true")
public class UserController {

	@Autowired
	private UserDetailRepository userrepo;
	
	@Autowired
	private S3StorageService googleservice;
	//get UserDetails
	
	@GetMapping("/{name}")
    public ResponseEntity<?> getUserDetails(@PathVariable String name) {
		  Optional<UserDetail> userDetailsOpt = userrepo.findByName(name);

		    if (userDetailsOpt.isPresent()) {
		        return ResponseEntity.ok(userDetailsOpt.get());
		    } else {
		        return ResponseEntity.status(404).body("User details not found");
		    }
    }
	
	// update UserDetail
	@PutMapping("/{name}")
    public ResponseEntity<?> updateUserDetails(@PathVariable String name, @RequestBody UserDetail updatedDetails) {
        Optional<UserDetail> userDetailsOpt = userrepo.findByName(name);

        if (userDetailsOpt.isPresent()) {
            UserDetail userDetail = userDetailsOpt.get();
            userDetail.setLastContest(updatedDetails.getLastContest());
            userDetail.setMonthlyPerformance(updatedDetails.getMonthlyPerformance());
            userDetail.setLinkedin(updatedDetails.getLinkedin());
            userDetail.setGithub(updatedDetails.getGithub());
            //userDetail.setCodingProfile(updatedDetails.getCodingProfile());
            userDetail.setDescription(updatedDetails.getDescription());
            userDetail.setSkills(updatedDetails.getSkills());

            userrepo.save(userDetail);
            return ResponseEntity.ok("User details updated successfully.");
        }
        return ResponseEntity.status(404).body("User details not found");
    }
	  @PostMapping("/{name}/upload-resume")
	    public ResponseEntity<?> uploadResume(@PathVariable String name, @RequestParam("file") MultipartFile file) {
	        Optional<UserDetail> userDetailsOpt = userrepo.findByName(name);
	        if (userDetailsOpt.isEmpty()) {
	            return ResponseEntity.status(404).body("User details not found");
	        }

	        String resumeUrl;
			try {
				resumeUrl = googleservice.uploadFile(file);
				   UserDetail userDetails = userDetailsOpt.get();
			        userDetails.setResumeUrl(resumeUrl);
			        userrepo.save(userDetails);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     

	        return ResponseEntity.ok("Resume uploaded successfully.");
	    }
	  
	  @PostMapping("/{name}/upload-profile-pic")
	    public ResponseEntity<?> uploadProfilePic(@PathVariable String name, @RequestParam("file") MultipartFile file) {
	        Optional<UserDetail> userDetailsOpt = userrepo.findByName(name);
	        if (userDetailsOpt.isEmpty()) {
	            return ResponseEntity.status(404).body("User details not found");
	        }

	        String profilePicUrl;
			try {
				profilePicUrl = googleservice.uploadFile(file);
				 UserDetail userDetails = userDetailsOpt.get();
			        userDetails.setProfilePicUrl(profilePicUrl);
			        userrepo.save(userDetails);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       

	        return ResponseEntity.ok("Profile picture uploaded successfully.");
	    }
	  @GetMapping("/top-users")
	  public ResponseEntity<List<UserDetail>> getTopUsers() {
	      List<UserDetail> topUsers = userrepo.findAll().stream()
	              .sorted((u1, u2) -> Integer.compare(u2.getCoins(), u1.getCoins())) // Sort by coins (highest first)
	              .limit(5) // Get top 5 users
	              .collect(Collectors.toList());

	      return ResponseEntity.ok(topUsers);
	  }
	 

	  @GetMapping("/user-rank/{name}")
	  public ResponseEntity<?> getUserRank(@PathVariable String name) {
	      List<UserDetail> allUsers = userrepo.findAll();

	      // Sort users by current coins (highest first)
	      List<UserDetail> sortedUsers = allUsers.stream()
	              .sorted((u1, u2) -> Integer.compare(u2.getCoins(), u1.getCoins()))
	              .collect(Collectors.toList());

	      // Find user's rank
	      int rank = -1;
	      UserDetail user = null;

	      for (int i = 0; i < sortedUsers.size(); i++) {
	          if (sortedUsers.get(i).getName().equals(name)) {
	              rank = i + 1; // 1-based index
	              user = sortedUsers.get(i);
	              break;
	          }
	      }

	      if (user == null) {
	          return ResponseEntity.status(404).body("User not found");
	      }

	      // Prepare response
	      return ResponseEntity.ok(
	          Map.of(
	              "rank", rank,
	              "totalParticipants", sortedUsers.size(),
	              "coins", user.getCoins() // Get current coin balance
	          )
	      );
	  }
	  @GetMapping("/user-pics")
	  public ResponseEntity<List<Map<String, String>>> getAllUserProfilePics() {
	      List<Map<String, String>> userPics = userrepo.findAll().stream()
	              .map(user -> Map.of(
	                      "name", user.getName(),
	                      "profilePicUrl", user.getProfilePicUrl() != null ? user.getProfilePicUrl() : "No Image"
	              ))
	              .collect(Collectors.toList());

	      return ResponseEntity.ok(userPics);
	  }

	  
}
