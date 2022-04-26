package kpi.diploma.ovcharenko.service.data;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelDataService {
        void getExcelDataAsList(MultipartFile excelFilePath, int pageNum, String subject, String section);
}
