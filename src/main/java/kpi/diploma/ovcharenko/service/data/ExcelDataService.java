package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
public interface ExcelDataService {
        List<Book> getExcelDataAsList(MultipartFile excelFilePath, int pageNum, String subject);

        void saveExcelData(List<Book> books);
}
