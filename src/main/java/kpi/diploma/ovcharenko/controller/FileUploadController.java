package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.exception.StorageFileNotFoundException;
import kpi.diploma.ovcharenko.service.data.IExcelDataService;
import kpi.diploma.ovcharenko.service.updloader.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Aleksandr Ovcharenko
 */
@Slf4j
@Controller
@RequestMapping(value = "/files")
public class FileUploadController {

    @Autowired
    IStorageService storageService;

    @Autowired
    IExcelDataService excelService;

    @Autowired
    public FileUploadController(IStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/upload")
    public String listUploadedFiles(Model model) {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadPage";
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("pageNum") String pageNum,
                                   @RequestParam("subject") String subject ,RedirectAttributes redirectAttributes) {

        storageService.store(file);

        List<Book> excelDataAsList = excelService.getExcelDataAsList(file, Integer.parseInt(pageNum), subject);

        excelService.saveExcelData(excelDataAsList);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/files/upload";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<StorageFileNotFoundException> handleStorageFileNotFound(StorageFileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }


}
