package chat.controller;

import java.time.LocalDateTime;
import java.text.DecimalFormat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.util.StringUtils;

import chat.enitity.Message;
import chat.enitity.Room;
import chat.payload.MessageRequest;
import chat.repository.RoomRepository;

import chat.service.S3StorageService;

@Controller
@CrossOrigin(origins = {"http://localhost:5173", "https://asliengineers.vercel.app","https://asliengineers.com"})
public class ChatController {
	private final RoomRepository repo;
	private final S3StorageService storageService;

	public ChatController(RoomRepository repo, S3StorageService storageService) {
		this.repo = repo;
		this.storageService = storageService;
	}

	@MessageMapping("/sendmessage/{roomId}/websocket")
	@SendTo("/topic/room/{roomId}")
	public Message sendmessage(
			@DestinationVariable String roomId,
			@RequestBody MessageRequest request
	) throws Exception {
		// Validate message content
		if (request.getContent() == null || request.getContent().trim().isEmpty()) {
			throw new IllegalArgumentException("Message content cannot be empty");
		}

		Room room = repo.findByRoomId(roomId);
		if (room == null) {
			throw new RuntimeException("Room not found");
		}

		// Create message with trimmed content
		Message message = new Message();
		message.setContent(request.getContent().trim());
		message.setSender(request.getSender());
		message.setTimestamp(LocalDateTime.now());
		message.setMessageType("text");
		
		// Save message
		room.getMessage().add(message);
		repo.save(room);
		
		return message;
	}

	@PostMapping("/api/chat/{roomId}/upload")
	public ResponseEntity<?> handleFileUpload(
			@PathVariable String roomId,
			@RequestParam("file") MultipartFile file,
			@RequestParam("sender") String sender) {
		try {
			Room room = repo.findByRoomId(roomId);
			if (room == null) {
				return ResponseEntity.badRequest().body("Room not found");
			}

			String fileName = file.getOriginalFilename();
			String contentType = file.getContentType();
			String fileSize = formatFileSize(file.getSize());
			String messageType = contentType != null && contentType.startsWith("image/") ? "image" : "file";

			// Upload to S3 and get signed URL
			String fileUrl = storageService.uploadFile(file);

			Message message = new Message(
				sender,
				fileName,
				fileUrl,
				fileSize,
				contentType,
				messageType
			);

			room.getMessage().add(message);
			repo.save(room);

			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Could not upload file: " + e.getMessage());
		}
	}

	private String formatFileSize(long size) {
		if (size <= 0) return "0 B";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
