package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.book.BookTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ExcelDataService {
        void getExcelDataAsList(MultipartFile excelFilePath, int pageNum, String subject, String section, Set<BookTag> tags);
}
