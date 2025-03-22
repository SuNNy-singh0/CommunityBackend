package chat.controller;

import java.util.Optional;

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
import chat.service.GoogleCloudStorageService;

@RestController
@RequestMapping("/usercontrol")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

	@Autowired
	private UserDetailRepository userrepo;
	
	@Autowired
	private GoogleCloudStorageService googleservice;
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

	        String resumeUrl = googleservice.uploadFile(file, "resumes/");
	        UserDetail userDetails = userDetailsOpt.get();
	        userDetails.setResumeUrl(resumeUrl);
	        userrepo.save(userDetails);

	        return ResponseEntity.ok("Resume uploaded successfully.");
	    }
	  
	  @PostMapping("/{name}/upload-profile-pic")
	    public ResponseEntity<?> uploadProfilePic(@PathVariable String name, @RequestParam("file") MultipartFile file) {
	        Optional<UserDetail> userDetailsOpt = userrepo.findByName(name);
	        if (userDetailsOpt.isEmpty()) {
	            return ResponseEntity.status(404).body("User details not found");
	        }

	        String profilePicUrl = googleservice.uploadFile(file, "profile-pics/");
	        UserDetail userDetails = userDetailsOpt.get();
	        userDetails.setProfilePicUrl(profilePicUrl);
	        userrepo.save(userDetails);

	        return ResponseEntity.ok("Profile picture uploaded successfully.");
	    }
	
}
