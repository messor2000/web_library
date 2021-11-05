package kpi.diploma.ovcharenko.service.updloader;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aleksandr Ovcharenko
 */
public interface IFileUploaderService {
    void uploadFile(MultipartFile file);
}
