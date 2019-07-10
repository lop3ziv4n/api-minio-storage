package ar.org.example.notification.dto;

import java.io.Serializable;

/**
 * A DTO for the File Upload Message Notification.
 */
public class FileUploadMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long provisionalId;

    private String objectName;

    public Long getProvisionalId() {
        return provisionalId;
    }

    public void setProvisionalId(Long provisionalId) {
        this.provisionalId = provisionalId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String toString() {
        return "FileUploadMessageDTO{" +
                "provisionalId=" + getProvisionalId() +
                ", objectName='" + getObjectName() + "'" +
                '}';
    }
}
