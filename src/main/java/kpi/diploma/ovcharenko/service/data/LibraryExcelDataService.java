package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.entity.BookModel;
import kpi.diploma.ovcharenko.repo.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class LibraryExcelDataService implements ExcelDataService {

    private final BookRepository bookRepository;

    @Autowired
    public LibraryExcelDataService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getExcelDataAsList(MultipartFile excelFilePath, int pageIndex, String subject) {
        List<BookModel> bookModels = new ArrayList<>();

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(excelFilePath.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        XSSFSheet worksheet = null;
        if (workbook != null) {
            worksheet = workbook.getSheetAt(pageIndex - 1);
        }

        int numberOfRows = getNumberOfNonEmptyCells(Objects.requireNonNull(worksheet), 2);

        for (int index = 0; index < numberOfRows; index++) {
            if (index > 0) {
                XSSFRow row = worksheet.getRow(index);

                BookModel book = new BookModel();
                book.setAuthor(getCellValue(row, 1));
                book.setBookName(getCellValue(row, 2));
                book.setYear(convertStringToInt(getCellValue(row, 3)));
                if (subject.equals("-")) {
                    book.setSubject("Без категории");
                } else {
                    book.setSubject(subject);
                }
                book.setAmount(1);
                book.setDescription("---");

                bookModels.add(book);
            }
        }

        bookModels = removeDuplicates(bookModels);

        List<Book> books = new ArrayList<>();

        if (!bookModels.isEmpty()) {
            bookModels.forEach(x -> {
                Book book = new Book();
                book.setAuthor(x.getAuthor());
                book.setBookName(x.getBookName());
                book.setYear(x.getYear());
                book.setSubject(x.getSubject());
                book.setAmount(x.getAmount());
                book.setDescription(x.getDescription());
                books.add(book);
            });
        }

        return books;
    }

    @Override
    public void saveExcelData(List<Book> books) {
        bookRepository.saveAll(books);
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

    private static int getNumberOfNonEmptyCells(XSSFSheet sheet, int columnIndex) {
        int numberOfNonEmptyCells = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                XSSFCell cell = row.getCell(columnIndex);
                if (cell != null && cell.getCellType() != CellType.BLANK && !cell.getRawValue().trim().isEmpty()) {
                    numberOfNonEmptyCells++;
                }
            }
        }
        return numberOfNonEmptyCells;
    }

    private static List<BookModel> removeDuplicates(List<BookModel> books) {
        Set<BookModel> filteredBooks = new HashSet<>();
        for (BookModel bookModel: books) {
            if (!filteredBooks.add(bookModel)) {
                BookModel book = filteredBooks.stream().filter(data -> Objects.equals(data, bookModel)).findFirst().get();
                book.setAmount(book.getAmount() + 1);
            }
        }

        return new ArrayList<>(filteredBooks);
    }
}

