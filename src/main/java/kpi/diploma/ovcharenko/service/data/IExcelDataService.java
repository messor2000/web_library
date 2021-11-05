package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
public interface IExcelDataService {
    List<Book> getExcelDataAsList(MultipartFile excelFilePath);

    void saveExcelData(List<Book> books);
}
