package com.ivan.yuyuk.jasperreportdemo.models;

@JReportTableName("Документы")
public class DocumentExportModel extends AbstractExportModel {
    @JReportColumn(order = 1, name = "Номер документа")
    private Integer docNumber;
    @JReportColumn(order = 2, name = "Название документа")
    private String name;

    @JReportColumn(order = 3, name = "Создатель")
    private String createdBy;

    public DocumentExportModel(Integer docNumber, String name, String createdBy) {
        this.docNumber = docNumber;
        this.name = name;
        this.createdBy = createdBy;
    }

    public Integer getDocNumber() {
        return docNumber;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
