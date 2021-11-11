package kpi.diploma.ovcharenko.service.data;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.entity.BookModel;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.service.updloader.StorageService;
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
import java.util.List;
import java.util.Objects;

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

                book.setSubject(subject);

                book.setAmount(1);

                bookModels.add(book);
            }
        }

//        System.out.println(allBooks.size());
//        allBooks = checkDuplicatesInArray(allBooks);
//        System.out.println(allBooks.size());

        List<Book> books = new ArrayList<>();

        if (!bookModels.isEmpty()) {
            bookModels.forEach(x -> {
                Book book = new Book();
                book.setAuthor(x.getAuthor());
                book.setBookName(x.getBookName());
                book.setYear(x.getYear());
                book.setSubject(x.getSubject());
                book.setAmount(x.getAmount());
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

//    private static List<BookModel> checkDuplicatesInArray(List<BookModel> bookModels) {
//        ArrayList<BookModel> newBookList = new ArrayList<>();
//
//        for (BookModel book : bookModels) {
//            if (newBookList.contains(book)) {
//
////                BookModel newBook = new BookModel();
////                newBook.setBookName(book.getBookName());
////                newBook.setAuthor(book.getAuthor());
////                newBook.setYear(book.getYear());
////                newBook.setSubject(book.getSubject());
////                newBook.setAmount(book.getAmount() + 1);
////
////                newBookList.add(newBook);
////                newBookList.remove(book);
////                int currentAmount = book.getAmount();
//                book.setAmount(book.getAmount() + 1);
//            }
//            newBookList.add(book);
//        }
//
//        return newBookList;
//    }

//    private static List<BookModel> checkDuplicatesInArray(List<BookModel> bookModels) {
//
//        ArrayList<BookModel> newBookList = new ArrayList<>();
//
//        for (int i = 0; i < bookModels.size(); i++) {
//            BookModel firstBookModel = bookModels.get(i);
//            for (int j = i + 1; j < bookModels.size(); j++) {
//                BookModel secondBookModel = bookModels.get(j);
//                if (firstBookModel.getBookName().equals(secondBookModel.getBookName())) {
//                    secondBookModel.setAmount(firstBookModel.getAmount() + 1);
//                    newBookList.add(secondBookModel);
//                    newBookList.remove(firstBookModel);
//                }
//                newBookList.add(secondBookModel);
//            }
//        }
//
//        return newBookList;
//    }
}

