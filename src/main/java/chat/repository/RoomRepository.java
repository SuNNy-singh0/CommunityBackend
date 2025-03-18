package chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import chat.enitity.Room;

public interface RoomRepository extends MongoRepository<Room, String> {
  Room findByRoomId(String RoomId);
}
