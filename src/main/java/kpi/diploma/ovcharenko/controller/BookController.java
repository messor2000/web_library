package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.service.data.IExcelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Aleksandr Ovcharenko
 */
@Slf4j
@Controller
public class BookController {
    @Autowired
    IExcelDataService excelService;

    @Autowired
    BookRepository bookRepository;

}
