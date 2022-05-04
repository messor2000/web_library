package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.book.status.Status;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
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
    public void getExcelDataAsList(MultipartFile excelFilePath, int pageIndex, String category, String section) {
        List<Book> books = new ArrayList<>();

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

                Book book = new Book();

                book.setAuthor(getCellValue(row, 1));
                book.setBookName(getCellValue(row, 2));
                book.setYear(convertStringToInt(getCellValue(row, 3)));

                BookCategory bookCategory = new BookCategory();
                bookCategory.setCategory(category);
                book.addCategory(bookCategory);
                book.setAmount(1);
                book.setSection(section);

                BookStatus bookStatus = new BookStatus(Status.FREE);
                book.setStatus(bookStatus);
                books.add(book);
            }
        }

        books = removeDuplicates(books);

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

    private static List<Book> removeDuplicates(List<Book> books) {
        Set<Book> filteredBooks = new HashSet<>();
        for (Book book: books) {
            if (!filteredBooks.add(book)) {
                Book updatedBook = filteredBooks.stream().filter(data -> Objects.equals(data, book)).findFirst().get();
                updatedBook.setAmount(updatedBook.getAmount() + 1);
                BookStatus bookStatus = new BookStatus(Status.FREE);
                updatedBook.setStatus(bookStatus);
            }
        }

        return new ArrayList<>(filteredBooks);
    }
}

