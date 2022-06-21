package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.excel.reader.LibraryExcelReaderService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@SpringBootTest
@AutoConfigureMockMvc
public class ExcelReaderServiceTests {
    @Autowired
    private LibraryExcelReaderService excelReaderService;
    @Autowired
    private BookService bookService;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(new File("/upload-dir"));

    final Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");

    @Test
    @SneakyThrows
    @DisplayName("should save list with one Book entity from excel file")
    public void getExcelDataAsListTest() {
        final File tempFile = tempFolder.newFile("TestSheet.xlsx");
        writeToExcelFileData(tempFile);

        FileInputStream input = new FileInputStream(tempFile);
        MultipartFile multipartFile = new MockMultipartFile("file", tempFile.getName(), "text/plain",
                IOUtils.toByteArray(input));

        excelReaderService.getExcelDataAsList(multipartFile, 0, "testCategory", "testSection",
                Collections.singleton(new BookTag("testTag")));

        Page<Book> books = bookService.getAllBooks(Pageable.unpaged());

        Book foundBook = books.stream().findFirst().get();

        assertAll(
                () -> assertEquals(foundBook.getBookName(), book.getBookName()),
                () -> assertEquals(foundBook.getAuthor(), book.getAuthor()),
                () -> assertEquals(foundBook.getYear(), book.getYear()));
    }

    private void writeToExcelFileData(File file) {
        try {
            String filename = file.getName();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("TestSheet");

            HSSFRow row = sheet.createRow(1);
            row.createCell(1).setCellValue(book.getAuthor());
            row.createCell(2).setCellValue(book.getBookName());
            row.createCell(3).setCellValue(book.getYear());

            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception ex) {
            log.trace(ex);
        }
    }
}
