package kpi.diploma.ovcharenko.service.amazon;

import kpi.diploma.ovcharenko.entity.book.Book;
import org.springframework.web.multipart.MultipartFile;

public interface AmazonClient {
    void uploadImage(MultipartFile multipartFile, Long id);

    void changeFile(MultipartFile multipartFile, Long id);

    void uploadBookPdf(MultipartFile multipartFile, Long id);

    void downloadPdfFile(Book book);
}
