package ar.org.example.notification.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


public interface FileUploadSource {

    String OUTPUT = "file_upload_output";

    @Output(FileUploadSource.OUTPUT)
    MessageChannel output();
}
