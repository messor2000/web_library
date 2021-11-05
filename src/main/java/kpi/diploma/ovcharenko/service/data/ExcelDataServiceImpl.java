package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.repo.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Aleksandr Ovcharenko
 */
@Slf4j
@Service
public class ExcelDataServiceImpl implements IExcelDataService {

    @Value("${app.upload.file:${user.home}}")
    public String excelFilePath;

    @Autowired
    BookRepository bookRepository;

    Workbook workbook;

    @Override
    public List<Book> getExcelDataAsList() {
        List<String> list = new ArrayList<>();

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

//        log.info("File selected: " + excelFilePath);

        // Create the Workbook
        try {
            workbook = WorkbookFactory.create(new File(excelFilePath));
        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }

        // Retrieving the number of sheets in the Workbook
        log.info("Workbook has '" + workbook.getNumberOfSheets() + "' Sheets");

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

        // Getting number of columns in the Sheet
        int noOfColumns = sheet.getRow(0).getLastCellNum();
        log.info("Sheet has '" + noOfColumns + "' columns");

        // Using for-each loop to iterate over the rows and columns
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = dataFormatter.formatCellValue(cell);
                list.add(cellValue);
            }
        }

        // filling excel data and creating list as List<Book>
        List<Book> bookList = createList(list, noOfColumns);

        // Closing the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            log.error(String.valueOf(e));
//            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    public void saveExcelData(List<Book> books) {
        bookRepository.saveAll(books);
//        books = bookRepository.saveAll(books);
//        return books.size();
    }

    private List<Book> createList(List<String> excelData, int noOfColumns) {

        ArrayList<Book> bookList = new ArrayList<>();

        int i = noOfColumns;
        do {
            Book book = new Book();

            if (excelData.get(i).equals("")) {
                break;
            }

            book.setAuthor(excelData.get(i + 1));
            book.setBookName(excelData.get(i + 2));

            if (Objects.equals(excelData.get(i + 3), "")) {
                book.setYear(0);
            }
            book.setYear(Integer.parseInt(excelData.get(i + 3)));
            book.setSubject("Автоматика и телемеханика");

            bookList.add(book);

            log.info("Book saved: " + book);

            i = i + noOfColumns;
        } while (i < excelData.size());
        return bookList;
    }
}

