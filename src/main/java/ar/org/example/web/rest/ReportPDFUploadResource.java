package ar.org.example.web.rest;

import ar.org.example.service.FileUploadService;
import ar.org.example.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * REST controller for managing Pdf File Upload.
 */
@RestController
@RequestMapping("/api")
public class ReportPDFUploadResource {

    private final Logger log = LoggerFactory.getLogger(ReportPDFUploadResource.class);

    @Value("${minio.server.pdf-bucket-name}")
    private String bucketName;

    private final FileUploadService fileUploadService;

    public ReportPDFUploadResource(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * POST  /report-pdf-upload : Update pdf file.
     *
     * @param file          the file to upload
     * @param provisionalId the id report
     * @return the ResponseEntity with status 201 (Created) and with body the objectName
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(value = "/report-pdf-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@Valid
                                             @RequestParam Long provisionalId,
                                             @RequestBody MultipartFile file) throws URISyntaxException {
        log.debug("REST request to update file");
        String objectName = fileUploadService.upload(bucketName, file);
        publishFileUpdate(provisionalId, objectName);
        return ResponseEntity
                .created(new URI("/api/report-pdf-upload/" + objectName))
                .headers(HeaderUtil.updateFileCreationAlert(bucketName, objectName))
                .body(new HashMap() {{
                    put("objectName", objectName);
                }});
    }

    /**
     * GET  /report-pdf-share : Get url to pdf file.
     *
     * @param objectName the file name to share
     * @return the ResponseEntity with status 200 (OK) and with body the link, or with status 404 (Not Found)
     */
    @GetMapping("/report-pdf-share")
    public ResponseEntity<Object> shareFile(@Valid
                                            @RequestParam String objectName) {
        log.debug("REST request to get shareable link File : {}", objectName);
        String link = fileUploadService.getShareableLink(bucketName, objectName);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.downloadFileCreationAlert(bucketName, objectName))
                .body(new HashMap() {{
                    put("url", link);
                }});
    }

    /**
     * Notification a file update.
     *
     * @param provisionalId the provisionalId to notification
     * @param objectName    the objectName file update
     */
    private void publishFileUpdate(Long provisionalId, String objectName) {
        fileUploadService.notification(provisionalId, objectName);
    }

}
