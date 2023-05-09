package com.ivan.yuyuk.jasperreportdemo.services;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.ListLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.ivan.yuyuk.jasperreportdemo.models.AbstractExportModel;
import com.ivan.yuyuk.jasperreportdemo.models.JReportColumn;
import com.ivan.yuyuk.jasperreportdemo.models.JReportTableName;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

    public ByteArrayOutputStream exportModelToXls(Collection<? extends AbstractExportModel> data, Class<?> typeClass) throws
                                                                                                                            JRException,
                                                                                                                            IllegalAccessException {
        FastReportBuilder reportBuilder = buildReport(typeClass);
        setUpColumns(reportBuilder, typeClass);

        List<HashMap<String, Object>> rowsDataList = prepareData(data);

        DynamicReport dynamicReport = reportBuilder.build();
        JasperPrint finalReport = DynamicJasperHelper.generateJasperPrint(dynamicReport,
                new ClassicLayoutManager(),
                rowsDataList);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JRXlsExporter xlsExporter = new JRXlsExporter();
        ExporterInput exporterInput = new SimpleExporterInput(finalReport);
        OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(bos);
        xlsExporter.setExporterOutput(exporterOutput);
        xlsExporter.setExporterInput(exporterInput);

        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setWhitePageBackground(true);
        configuration.setRemoveEmptySpaceBetweenColumns(true);
        xlsExporter.setConfiguration(configuration);

        xlsExporter.exportReport();
        return bos;
    }

    private FastReportBuilder buildReport(Class<?> typeClass) {
        Font font = Font.TIMES_NEW_ROMAN_BIG_BOLD;
        Style titleStyle = new StyleBuilder(false).setFont(font).build();
        JReportTableName declaredAnnotation = typeClass.getDeclaredAnnotation(JReportTableName.class);
        FastReportBuilder reportBuilder = new FastReportBuilder();
        reportBuilder.setTitle(declaredAnnotation.value())
                .setPrintColumnNames(true)
                .setIgnorePagination(true)
                .setDefaultStyles(titleStyle,titleStyle,titleStyle,titleStyle)
                .setUseFullPageWidth(true);
        return reportBuilder;
    }

    private void setUpColumns(FastReportBuilder report, Class<?> typeClass) {
        Field[] getDeclaredFields = typeClass.getDeclaredFields();
        for (Field field : getDeclaredFields) {
            JReportColumn declaredAnnotation = field.getDeclaredAnnotation(JReportColumn.class);
            if (declaredAnnotation != null) {
                AbstractColumn newColumn =
                        ColumnBuilder.getNew()
                                .setColumnProperty(declaredAnnotation.order() + "#", field.getType().getName())
                                .setTitle(declaredAnnotation.name())
                                .build();
                report.addColumn(newColumn);
            }
        }
    }

    private List<HashMap<String, Object>> prepareData(Collection<? extends AbstractExportModel> data) throws
                                                                                                      IllegalAccessException {
        List<HashMap<String, Object>> rowsDataList = new ArrayList<>();

        for (AbstractExportModel model : data) {
            HashMap<String, Object> rowHashMap = new HashMap<>();
            Field[] fields = model.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                JReportColumn declaredAnnotation = field.getDeclaredAnnotation(JReportColumn.class);
                if (declaredAnnotation != null) {
                    rowHashMap.put(declaredAnnotation.order() + "#", field.get(model));
                }
            }
            rowsDataList.add(rowHashMap);
        }
        return rowsDataList;
    }
}
