package com.example.lab6.controller;

import com.example.lab6.model.GastoLavanderia;
import com.example.lab6.service.GastoLavanderiaService;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/")
public class GastoLavanderiaController {

    @Autowired
    private GastoLavanderiaService service;

    @GetMapping()
    public String main(Model model) {
        List<GastoLavanderia> gastos = service.obtenerGastos();
        model.addAttribute("gastos", gastos);
        model.addAttribute("gasto", new GastoLavanderia());
        return "gastos";
    }

    @PostMapping("/registrarGasto")
    public String registrarGasto(@ModelAttribute GastoLavanderia gasto) {
        service.registrarGasto(gasto);
        return "redirect:/";
    }

    @GetMapping("/editarGasto/{id}")
    public String editarGasto(@PathVariable("id") Integer id, Model model) {
        service.buscarGasto(id).ifPresent(gasto -> model.addAttribute("gasto", gasto));
        return "gasto";  // Esta es la vista para editar el gasto
    }

    @PostMapping("/guardarEdicion")
    public String guardarEdicion(@ModelAttribute GastoLavanderia gasto) {
        service.editarGasto(gasto);
        return "redirect:/";
    }

    @GetMapping("/eliminarGasto/{id}")
    public String eliminarGasto(@PathVariable("id") Integer id) {
        service.eliminarGasto(id);
        return "redirect:/";  // Redirige al listado de gastos o a la página principal
    }

    @GetMapping("/reporte/pdf")
    public void generarReportePdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_gastos.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        try (Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {
            document.add(new Paragraph("Reporte de Gastos de Lavandería").setBold().setFontSize(18));

            Table table = new Table(6);
            table.addCell("ID");
            table.addCell("Empleado");
            table.addCell("Insumo");
            table.addCell("Cantidad Usada");
            table.addCell("Costo Insumo");
            table.addCell("Fecha");

            service.obtenerGastos().forEach(gasto -> {
                table.addCell(gasto.getIdGasto().toString());
                table.addCell(gasto.getEmpleado());
                table.addCell(gasto.getInsumo());
                table.addCell(String.valueOf(gasto.getCantidadUsada()));
                table.addCell(String.format("%.2f", gasto.getCostoInsumo()));
                table.addCell(gasto.getFecha().toString());
            });

            document.add(table);
        }
    }

    @GetMapping("/reporte/excel")
    public void generarReporteExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=gastos_reporte.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Gastos");

            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Empleado", "Insumo", "Cantidad Usada", "Costo Insumo", "Fecha"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (GastoLavanderia gasto : service.obtenerGastos()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(gasto.getIdGasto());
                row.createCell(1).setCellValue(gasto.getEmpleado());
                row.createCell(2).setCellValue(gasto.getInsumo());
                row.createCell(3).setCellValue(gasto.getCantidadUsada());
                row.createCell(4).setCellValue(gasto.getCostoInsumo());
                row.createCell(5).setCellValue(gasto.getFecha().toString());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}
