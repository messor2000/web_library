package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.service.data.IExcelDataService;
import kpi.diploma.ovcharenko.service.updloader.IFileUploaderService;
import kpi.diploma.ovcharenko.service.updloader.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
@Slf4j
@Controller
@RequestMapping(value = "/file")
public class BookController {
    @Autowired
    IFileUploaderService fileService;

    @Autowired
    IExcelDataService excelService;

    private IStorageService storageService;

    @Autowired
    BookRepository bookRepository;

    @RequestMapping(value = "/info")
    public String index() {
        return "uploadPage";
    }

//    @PostMapping("/upload")
//    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
//
//        fileService.uploadFile(file);
//
//        List<Book> excelDataAsList = excelService.getExcelDataAsList();
//        try {
//            excelService.saveExcelData(excelDataAsList);
//        } catch (NumberFormatException e) {
//            log.error(String.valueOf(e));
//        }
//
//        redirectAttributes.addFlashAttribute("message",
//                "You have successfully uploaded '" + file.getOriginalFilename() + "' !");
//
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return "redirect:/";
//    }

//    @PostMapping("/")
//    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
//        storageService.store(file);
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() + "!");
//
//        return "redirect:/";
//    }


//    @PostMapping("/save")
//    public String saveExcelData(Model model, @RequestParam(value = "file") MultipartFile file) {
//        List<Book> excelDataAsList = excelService.getExcelDataAsList(file);
//        try {
//            excelService.saveExcelData(excelDataAsList);
//
////            int noOfRecords = excelService.saveExcelData(excelDataAsList);
////            model.addAttribute("noOfRecords", noOfRecords);
//        } catch (NumberFormatException e) {
//            log.error(String.valueOf(e));
//        }
//        return "redirect:/";
//    }


//    @PostMapping("/save")
//    public String saveExcelData(Model model, @RequestParam(value = "file") MultipartFile file) {
//        List<Book> excelDataAsList = excelService.getExcelDataAsList(file);
//        try {
//            excelService.saveExcelData(excelDataAsList);
//
////            int noOfRecords = excelService.saveExcelData(excelDataAsList);
////            model.addAttribute("noOfRecords", noOfRecords);
//        } catch (NumberFormatException e) {
//            log.error(String.valueOf(e));
//        }
//        return "redirect:/";
//    }
}
