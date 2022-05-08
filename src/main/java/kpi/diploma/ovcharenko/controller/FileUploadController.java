package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.exception.StorageFileNotFoundException;
import kpi.diploma.ovcharenko.service.book.tags.BookTagService;
import kpi.diploma.ovcharenko.service.data.ExcelDataService;
import kpi.diploma.ovcharenko.service.updloader.StorageService;
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = "/files")
public class FileUploadController {

    private final StorageService storageService;
    private final ExcelDataService excelDataService;
    private final BookTagService bookTagService;

    @Autowired
    public FileUploadController(StorageService storageService, ExcelDataService excelDataService, BookTagService bookTagService) {
        this.storageService = storageService;
        this.excelDataService = excelDataService;
        this.bookTagService = bookTagService;
    }

    @GetMapping("/upload")
    public String listUploadedFiles(Model model) {
        Set<String> tags = bookTagService.findAllTagNames();

        model.addAttribute("tags", tags);
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
                                   @RequestParam("category") String category, @RequestParam("section") String section,
                                   @RequestParam("tag") String tag, RedirectAttributes redirectAttributes) {
        storageService.store(file);

        Set<BookTag> bookTags = tagSetOfTagsFromString(tag);

        excelDataService.getExcelDataAsList(file, Integer.parseInt(pageNum), category, section, bookTags);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/files/upload";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<StorageFileNotFoundException> handleStorageFileNotFound(StorageFileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    private static Set<BookTag> tagSetOfTagsFromString(String tags) {
        String[] split  = tags.split("[ ,]+");
        Set<BookTag> bookTags = new HashSet<>();

        for (String tag: split) {
            bookTags.add(new BookTag(tag));
        }

        return bookTags;
    }
}
