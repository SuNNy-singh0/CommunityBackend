package chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.CommunityRecord;

public interface CommunityRecordRepository extends MongoRepository<CommunityRecord, String> {
    CommunityRecord findByUsername(String username);
}
