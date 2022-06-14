package kpi.diploma.ovcharenko.service.excel.reader;

import kpi.diploma.ovcharenko.entity.book.BookTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ExcelReaderService {
        void getExcelDataAsList(MultipartFile excelFilePath, int pageNum, String subject, String section, Set<BookTag> tags);
}
