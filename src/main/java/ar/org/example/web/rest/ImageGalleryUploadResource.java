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
import java.util.List;

/**
 * REST controller for managing Image File Upload.
 */
@RestController
@RequestMapping("/api")
public class ImageGalleryUploadResource {

    private final Logger log = LoggerFactory.getLogger(ImageGalleryUploadResource.class);

    @Value("${minio.server.gallery-bucket-name}")
    private String bucketName;

    private final FileUploadService fileUploadService;

    public ImageGalleryUploadResource(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * POST  /image-gallery-upload : Update image to gallery.
     *
     * @param file the file to upload
     * @return the ResponseEntity with status 201 (Created) and with body the objectName
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(value = "/image-gallery-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@Valid
                                             @RequestBody MultipartFile file) throws URISyntaxException {
        log.debug("REST request to update image");
        String objectName = fileUploadService.upload(bucketName, file);
        return ResponseEntity
                .created(new URI("/api/image-gallery-upload/" + objectName))
                .headers(HeaderUtil.updateFileCreationAlert(bucketName, objectName))
                .body(new HashMap() {{
                    put("objectName", objectName);
                }});
    }

    /**
     * GET  /image-gallery-share : Get url to image file.
     *
     * @param objectName the file name to share
     * @return the ResponseEntity with status 200 (OK) and with body the link, or with status 404 (Not Found)
     */
    @GetMapping("/image-gallery-share")
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
     * GET  /image-gallery : get all the object.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of objects name in body
     */
    @GetMapping("/image-gallery")
    public ResponseEntity<Object> getAllObject() {
        log.debug("REST request to get all of object name");
        List<String> objects = fileUploadService.findAllObject(bucketName);
        return ResponseEntity
                .ok()
                .body(new HashMap() {{
                    put("objects", objects);
                }});
    }
}
