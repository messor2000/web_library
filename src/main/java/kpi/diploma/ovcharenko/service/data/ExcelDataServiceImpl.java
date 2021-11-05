package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.entity.BookModel;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.service.updloader.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

//    @Value("${app.upload.file:${user.home}}")
//    public String excelFilePath;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    IExcelDataService excelService;

    @Autowired
    IStorageService storageService;

    @Override
    public List<Book> getExcelDataAsList(MultipartFile excelFilePath) {
        List<BookModel> allBooks = new ArrayList<>();

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(excelFilePath.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Read student data form excel file sheet1.
        XSSFSheet worksheet = null;
        if (workbook != null) {
            worksheet = workbook.getSheetAt(0);
        }
        for (int index = 0; index < Objects.requireNonNull(worksheet).getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                XSSFRow row = worksheet.getRow(index);

                BookModel book = new BookModel();
                book.setAuthor(getCellValue(row, 1));

                if (getCellValue(row, 2) == null) {
                    break;
                }
                book.setBookName(getCellValue(row, 2));

//                if (getCellValue(row, 3).equals("0")) {
//                    book.setYear("-");
//                }

                book.setYear(convertStringToInt(getCellValue(row, 4)));
                book.setSubject("Автоматика и телемеханика");
                allBooks.add(book);
            }
        }

        List<Book> bookList = new ArrayList<>();

        if (!allBooks.isEmpty()) {
            allBooks.forEach(x -> {
                Book book = new Book();
                book.setAuthor(x.getAuthor());
                book.setBookName(x.getBookName());
                book.setYear(x.getYear());
                book.setSubject(x.getSubject());
                bookList.add(book);
            });

//            bookRepository.saveAll(bookList);
        }

        return bookList;
    }

    private int convertStringToInt(String str) {
        int result = 0;
        if (str == null || str.isEmpty() || str.trim().isEmpty()) {
            return result;
        }
        result = Integer.parseInt(str);
        return result;
    }

    private String getCellValue(Row row, int cellNo) {
        DataFormatter formatter = new DataFormatter();
        Cell cell = row.getCell(cellNo);
        return formatter.formatCellValue(cell);
    }

    @Override
    public void saveExcelData(List<Book> books) {
        bookRepository.saveAll(books);
    }

    private String getPathToFile(MultipartFile fillName) {

        String rootPath = "/Applications/ProgrammingFolder/JavaProgramming/WebLibraryPson/upload-dir/";

        return storageService.load(rootPath + fillName.getOriginalFilename()).toString();
    }
}

