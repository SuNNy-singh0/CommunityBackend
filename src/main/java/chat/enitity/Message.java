package chat.enitity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private String messageType; // "text", "image", or "file"
    private String fileName;    // Original file name
    private String fileUrl;     // Google Cloud Storage URL
    private String fileSize;    // Size of the file
    private String mimeType;    // MIME type of the file

    // Constructor for text messages
    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.messageType = "text";
    }

    // Constructor for file/image messages
    public Message(String sender, String fileName, String fileUrl, String fileSize, String mimeType, String messageType) {
        this.sender = sender;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.messageType = messageType;
        this.timestamp = LocalDateTime.now();
    }
}
