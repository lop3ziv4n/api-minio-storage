package ar.org.example.service;

import ar.org.example.notification.FileUploadOutputEventNotification;
import ar.org.example.notification.dto.FileUploadMessageDTO;
import ar.org.example.service.errors.UploaderException;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing FileUpload.
 */
@Service
public class FileUploadService {

    private final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${minio.server.endpoint}")
    private String endpoint;

    @Value("${minio.server.port}")
    private int port;

    @Value("${minio.server.access-key}")
    private String accessKey;

    @Value("${minio.server.secret-key}")
    private String secretKey;

    @Value("${minio.server.default-bucket-name}")
    private String defaultBucketName;

    @Value("${minio.server.expiry}")
    private int expiry;

    private final FileUploadOutputEventNotification fileUploadOutputEventNotification;

    public FileUploadService(FileUploadOutputEventNotification fileUploadOutputEventNotification) {
        this.fileUploadOutputEventNotification = fileUploadOutputEventNotification;
    }

    /**
     * Upload a file.
     *
     * @param file       the file to upload
     * @param bucketName the bucket file path to upload
     * @return the upload object name
     */
    public String upload(String bucketName, MultipartFile file) {
        String objectName = getObjectName();
        try {
            MinioClient minioClient = getMinioClient();
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
            minioClient.putObject(bucketName, objectName, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (MinioException | NoSuchAlgorithmException | XmlPullParserException | InvalidKeyException | IOException e) {
            log.error(e.getMessage(), e);
            throw new UploaderException(e.getMessage(), objectName, "Uploading File");
        }
        return objectName;
    }

    /**
     * Download a file.
     *
     * @param bucketName the bucket file path to download
     * @param objectName the file name to download
     * @return the file loaded
     */
    public InputStream download(String bucketName, String objectName) {
        InputStream inputStream;
        try {
            MinioClient minioClient = getMinioClient();
            minioClient.statObject(bucketName, objectName);
            inputStream = minioClient.getObject(bucketName, objectName);
        } catch (MinioException | NoSuchAlgorithmException | XmlPullParserException | InvalidKeyException | IOException e) {
            log.error(e.getMessage(), e);
            throw new UploaderException(e.getMessage(), objectName, "Download File");
        }
        return inputStream;
    }

    /**
     * Remove a file.
     *
     * @param bucketName the bucket file path to remove
     * @param objectName the file name to remove
     */
    public void remove(String bucketName, String objectName) {
        try {
            MinioClient minioClient = getMinioClient();
            minioClient.statObject(bucketName, objectName);
            minioClient.removeObject(bucketName, objectName);
        } catch (MinioException | NoSuchAlgorithmException | XmlPullParserException | InvalidKeyException | IOException e) {
            log.error(e.getMessage(), e);
            throw new UploaderException(e.getMessage(), objectName, "Remove File");
        }
    }

    /**
     * Get shareable link.
     *
     * @param bucketName the bucket file path to share
     * @param objectName the file name to share
     * @return the link shared of the object name
     */
    public String getShareableLink(String bucketName, String objectName) {
        String url;
        try {
            MinioClient minioClient = getMinioClient();
            minioClient.statObject(bucketName, objectName);
            url = minioClient.presignedGetObject(bucketName, objectName, expiry);
        } catch (MinioException | NoSuchAlgorithmException | XmlPullParserException | InvalidKeyException | IOException e) {
            log.error(e.getMessage(), e);
            throw new UploaderException(e.getMessage(), objectName, "Share File");
        }
        return url;
    }

    /**
     * Get all the Object.
     *
     * @param bucketName the bucket file path to share
     * @return the list of object
     */
    public List<String> findAllObject(String bucketName) {
        List<String> objects = new ArrayList<>();
        try {
            MinioClient minioClient = getMinioClient();
            minioClient.listObjects(bucketName).forEach(item -> {
                try {
                    objects.add(item.get().objectName());
                } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException e) {
                    log.error(e.getMessage(), e);
                    throw new UploaderException(e.getMessage(), null, "Find All Object");
                }
            });
        } catch (MinioException | XmlPullParserException e) {
            log.error(e.getMessage(), e);
            throw new UploaderException(e.getMessage(), null, "Find All Object");
        }
        return objects;
    }

    /**
     * Get bucket name.
     *
     * @param bucketName the bucketName of the file upload
     */
    public String getBucketName(String bucketName) {
        return bucketName != null ? bucketName : defaultBucketName;
    }

    /**
     * Get object name.
     */
    private String getObjectName() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get minio client.
     */
    private MinioClient getMinioClient() throws InvalidEndpointException, InvalidPortException {
        return new MinioClient(endpoint, port, accessKey, secretKey, false);
    }

    /**
     * Notification a file upload.
     *
     * @param provisionalId the provisionalId of the entity
     * @param objectName    the name of the file upload
     */
    public void notification(Long provisionalId, String objectName) {
        FileUploadMessageDTO fileUploadMessageDTO = new FileUploadMessageDTO();
        fileUploadMessageDTO.setProvisionalId(provisionalId);
        fileUploadMessageDTO.setObjectName(objectName);
        fileUploadOutputEventNotification.sendMessage(fileUploadMessageDTO);
    }
}
