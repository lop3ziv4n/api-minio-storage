package ar.org.example.notification;


import ar.org.example.notification.dto.FileUploadMessageDTO;
import ar.org.example.notification.source.FileUploadSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(FileUploadSource.class)
public class FileUploadOutputEventNotification {

    private final Logger log = LoggerFactory.getLogger(FileUploadOutputEventNotification.class);

    private final FileUploadSource fileUploadSource;

    public FileUploadOutputEventNotification(FileUploadSource fileUploadSource) {
        this.fileUploadSource = fileUploadSource;
    }

    public void sendMessage(FileUploadMessageDTO fileUploadMessageDTO) {
        log.debug("Request to send Notification FileUploadMessage : {}", fileUploadMessageDTO);
        fileUploadSource.output().send(MessageBuilder.withPayload(fileUploadMessageDTO).build());
    }
}
