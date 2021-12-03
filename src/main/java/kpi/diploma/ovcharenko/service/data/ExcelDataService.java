package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelDataService {
        void getExcelDataAsList(MultipartFile excelFilePath, int pageNum, String subject);

//        void saveExcelData(List<Book> books);
}
