package ar.org.example.web.rest;


import ar.org.example.service.FileUploadService;
import ar.org.example.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * REST controller for managing File Upload.
 */
@RestController
@RequestMapping("/api")
public class FileUploadResource {

    private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);

    private final FileUploadService fileUploadService;

    public FileUploadResource(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * POST  /file-uploader : Update single file.
     *
     * @param file   the file to upload
     * @param folder the file path to upload
     * @return the ResponseEntity with status 201 (Created) and with body the objectName
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(value = "/file-uploader", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@Valid
                                             @RequestParam(required = false) String folder,
                                             @RequestBody MultipartFile file) throws URISyntaxException {
        log.debug("REST request to update file");
        String bucketName = fileUploadService.getBucketName(folder);
        String objectName = fileUploadService.upload(bucketName, file);
        return ResponseEntity
                .created(new URI("/api/file-uploader/" + objectName))
                .headers(HeaderUtil.updateFileCreationAlert(bucketName, objectName))
                .body(new HashMap() {{
                    put("objectName", objectName);
                }});
    }

    /**
     * GET  /file-download : Get the file.
     *
     * @param folder     the file path to download
     * @param objectName the file name to download
     * @return the ResponseEntity with status 200 (OK) and with body the file, or with status 404 (Not Found)
     */
    @GetMapping(value = "/file-download")
    public ResponseEntity<Object> downloadFile(@Valid
                                               @RequestParam String objectName,
                                               @RequestParam(required = false) String folder) {
        log.debug("REST request to get File : {}", objectName);
        String bucketName = fileUploadService.getBucketName(folder);
        Resource resource = new InputStreamResource(fileUploadService.download(bucketName, objectName));
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.downloadFileCreationAlert(bucketName, objectName))
                .body(resource);
    }

    /**
     * GET  /file-share : Get url to file.
     *
     * @param folder     the file path to share
     * @param objectName the file name to share
     * @return the ResponseEntity with status 200 (OK) and with body the link, or with status 404 (Not Found)
     */
    @GetMapping("/file-share")
    public ResponseEntity<Object> shareFile(@Valid
                                            @RequestParam String objectName,
                                            @RequestParam(required = false) String folder) {
        log.debug("REST request to get shareable link File : {}", objectName);
        String bucketName = fileUploadService.getBucketName(folder);
        String link = fileUploadService.getShareableLink(bucketName, objectName);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.downloadFileCreationAlert(bucketName, objectName))
                .body(new HashMap() {{
                    put("url", link);
                }});
    }

    /**
     * GET  /file-object : get all the object.
     *
     * @param folder the file path to the objects
     * @return the ResponseEntity with status 200 (OK) and the list of objects name in body
     */
    @GetMapping("/file-object")
    public ResponseEntity<Object> getAllObjectFile(@Valid
                                                   @RequestParam String folder) {
        log.debug("REST request to get all of object name");
        String bucketName = fileUploadService.getBucketName(folder);
        List<String> objects = fileUploadService.findAllObject(bucketName);
        return ResponseEntity
                .ok()
                .body(new HashMap() {{
                    put("objects", objects);
                }});
    }

    /**
     * DELETE  /file-remove : Remove the file.
     *
     * @param folder     the file path to remove
     * @param objectName the file name to remove
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/file-remove")
    public ResponseEntity<Void> removeFile(@Valid
                                           @RequestParam String objectName,
                                           @RequestParam(required = false) String folder) {
        log.debug("REST request to remove File : {}", objectName);
        String bucketName = fileUploadService.getBucketName(folder);
        fileUploadService.remove(bucketName, objectName);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.removeFileCreationAlert(bucketName, objectName))
                .build();
    }
}
