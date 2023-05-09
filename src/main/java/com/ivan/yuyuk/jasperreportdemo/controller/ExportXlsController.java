package com.ivan.yuyuk.jasperreportdemo.controller;

import com.ivan.yuyuk.jasperreportdemo.models.DocumentExportModel;
import com.ivan.yuyuk.jasperreportdemo.services.ExportService;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExportXlsController {
    private final ExportService exportService;

    public ExportXlsController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/exportXls")
    public ResponseEntity<?> exportXls() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = exportService.exportModelToXls(generateFakeData(),
                    DocumentExportModel.class);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + "Test1.xls")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private Collection<DocumentExportModel> generateFakeData() {
        return new ArrayList<>(Arrays.asList(
                new DocumentExportModel(1, "Doc1", "Ююкин И.А."),
                new DocumentExportModel(2, "Doc2", "Ююкин И.А."),
                new DocumentExportModel(3, "Doc3", "Ююкин И.А."),
                new DocumentExportModel(4, "Doc4", "Ююкин И.А."),
                new DocumentExportModel(5, "Doc5", "Ююкин И.А."),
                new DocumentExportModel(6, "Doc6", "Ююкин И.А.")
        ));
    }
}
