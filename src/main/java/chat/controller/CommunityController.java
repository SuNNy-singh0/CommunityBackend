package chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import chat.config.JwtUtil;
import chat.enitity.CommunityRecord;
import chat.repository.CommunityRecordRepository;

import java.util.List;

@RestController
@RequestMapping("/community")
@CrossOrigin(origins = {"http://localhost:5173", "https://asliengineers.vercel.app"}, allowCredentials = "true")
public class CommunityController {

    @Autowired
    private CommunityRecordRepository communityRecordRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void initialize() {
        try {
            // Create indexes for the collection
            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
            IndexOperations indexOps = mongoTemplate.indexOps(CommunityRecord.class);
            resolver.resolveIndexFor(CommunityRecord.class).forEach(indexOps::ensureIndex);

            // Create a test record if collection is empty
            long count = communityRecordRepository.count();
            if (count == 0) {
                CommunityRecord testRecord = new CommunityRecord();
                testRecord.setUsername("test");
                testRecord.setCommunitytype("test");
                CommunityRecord savedRecord = communityRecordRepository.save(testRecord);
                communityRecordRepository.deleteById(savedRecord.getId());
            }
        } catch (Exception e) {
            // Ignore initialization errors
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinCommunity(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CommunityRecord communityRecord) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid token format");
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.badRequest().body("Invalid token");
            }

            // Ensure communitytype is set
            if (communityRecord.getCommunitytype() == null || communityRecord.getCommunitytype().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Community type is required");
            }

            String communityType = communityRecord.getCommunitytype().trim();

            // Check if user is already in this specific community
            Query query = new Query(Criteria.where("username").is(username)
                    .and("communitytype").is(communityType));
            CommunityRecord existingRecord = mongoTemplate.findOne(query, CommunityRecord.class);
            
            if (existingRecord != null) {
                return ResponseEntity.badRequest().body("User is already a member of this community");
            }

            // Create a new record with all fields explicitly set
            CommunityRecord newRecord = new CommunityRecord();
            newRecord.setUsername(username);
            newRecord.setCommunitytype(communityType);
            
            CommunityRecord savedRecord = communityRecordRepository.save(newRecord);
            return ResponseEntity.ok(savedRecord);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error joining community: " + e.getMessage());
        }
    }

    @GetMapping("/records")
    public ResponseEntity<List<CommunityRecord>> getAllRecords() {
        try {
            List<CommunityRecord> records = communityRecordRepository.findAll();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
