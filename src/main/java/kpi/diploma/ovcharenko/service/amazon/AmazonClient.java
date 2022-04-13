package kpi.diploma.ovcharenko.service.amazon;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonClient {
    String upload(MultipartFile multipartFile, Long id);
}
