package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
public interface IExcelDataService {
    List<Book> getExcelDataAsList();

    void saveExcelData(List<Book> books);
}
