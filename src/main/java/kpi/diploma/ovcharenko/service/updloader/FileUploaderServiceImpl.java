package kpi.diploma.ovcharenko.service.updloader;

import kpi.diploma.ovcharenko.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

/**
 * @author Aleksandr Ovcharenko
 */
@Slf4j
@Service
public class FileUploaderServiceImpl implements IFileUploaderService {

    @Value("${app.upload.dir:${user.home}}")
    private String uploadDir;

    @Override
    public void uploadFile(MultipartFile file) {
        try {
            Path copyLocation = Paths
                    .get(uploadDir + File.separator + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));

            log.info("copy location " + copyLocation);
            log.info("exist location " + uploadDir);


            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("changed location " + uploadDir);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }
}
