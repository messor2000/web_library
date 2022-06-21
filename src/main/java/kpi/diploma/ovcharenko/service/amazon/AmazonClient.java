package kpi.diploma.ovcharenko.service.amazon;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonClient {
    void uploadImage(MultipartFile multipartFile, Long id);

    void changeFile(MultipartFile multipartFile, Long id);

    void changeUserImage(MultipartFile multipartFile, String email);

    void uploadBookPdf(MultipartFile multipartFile, Long id);
}
