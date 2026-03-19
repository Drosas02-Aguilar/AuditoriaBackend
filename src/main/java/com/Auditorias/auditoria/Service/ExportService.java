package com.Auditorias.auditoria.Service;

import com.Auditorias.auditoria.Entity.Usuarios;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.opencsv.CSVWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportService {
    
    @Autowired
    private UsuarioService usuarioService;
    
    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public byte[] ExportarCSV() throws Exception {
        List<Usuarios> usuario = usuarioService.ObtenerTodosActivos();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
        CSVWriter csvWriter = new CSVWriter(osw,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
        );
        
        String[] encabezado = {
            "ID", "NOMBRE", "CORREO", "ROL", "ESTADO", "FECHA REGISTRO"
        };
        csvWriter.writeNext(encabezado);
        
        for (Usuarios usu : usuario) {
            String[] fila = {
                String.valueOf(usu.getIdusuario()),
                usu.getNombre(),
                usu.getCorreo(),
                usu.getRol(),
                usu.getActivo() == 1 ? "Activo" : "Inactivo",
                usu.getFecharegistro().format(FORMATO_FECHA)
            };
            csvWriter.writeNext(fila);
            
        }
        
        csvWriter.flush();
        csvWriter.close();
        return baos.toByteArray();
    }
    
    public byte[] exportarPDF() throws Exception {
        List<Usuarios> usuario = usuarioService.ObtenerTodosActivos();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(),
                36, 36, 40, 40);
        PdfWriter.getInstance(documento, baos);
        documento.open();
        
        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA,
                18, Font.BOLD, new BaseColor(88, 86, 214));
        
        Paragraph titulo = new Paragraph("Reporte de Usuarios Activos", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(6f);
        documento.add(titulo);
        
        Font fuenteSub = new Font(Font.FontFamily.HELVETICA,
                9, Font.ITALIC, BaseColor.GRAY);
        Paragraph subtitulo = new Paragraph(
                "Generado: " + LocalDateTime.now().format(FORMATO_FECHA)
                + "   |   Total registros: " + usuario.size(), fuenteSub);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(14f);
        documento.add(subtitulo);
        
        LineSeparator linea = new LineSeparator(1f, 100f, new BaseColor(
                88, 86, 214), Element.ALIGN_CENTER, -4f);
        documento.add(new Chunk(linea));
        documento.add(Chunk.NEWLINE);
        
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1f, 3f, 4.5f, 2f, 1.8f, 3f});
        tabla.setSpacingBefore(10f);
        
        AgregarEncabezadosTabla(tabla);
        
        boolean par = false;
        for (Usuarios usu : usuario) {
            AgregarFilaUsuario(tabla, usu, par);
            par = !par;
        }
        
        documento.add(tabla);
        
        Font fuentePie = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC,
                BaseColor.GRAY);
        Paragraph pie = new Paragraph(
                "Sistema de Gestión de Usuarios  —  Reporte generado automáticamente",
                fuentePie);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(14f);
        documento.add(pie);
        
        documento.close();
        return baos.toByteArray();
        
    }
    
    private void AgregarEncabezadosTabla(PdfPTable tabla) {
        Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,
                BaseColor.WHITE);
        
        BaseColor colorHeader = new BaseColor(88, 86, 214);
        String[] columnas = {"ID", "Nombre", "Correo", "Rol", "Estado", "Fecha Registro"};
        
        for (String col : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(col, fuenteHeader));
            celda.setBackgroundColor(colorHeader);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(9f);
            celda.setBorderColor(new BaseColor(70, 68, 180));
            tabla.addCell(celda);
        }
        
    }
    
    private void AgregarFilaUsuario(PdfPTable tabla, Usuarios usuarios, boolean par) {
        
        BaseColor colorFila = par
                ? new BaseColor(238, 237, 254)
                : BaseColor.WHITE;
        
        Font fuenteDato = new Font(Font.FontFamily.HELVETICA, 9,
                Font.NORMAL, new BaseColor(50, 50, 50));
        
        PdfPCell celdaId = new PdfPCell(
                new Phrase(String.valueOf(usuarios.getIdusuario()), fuenteDato));
        celdaId.setHorizontalAlignment(Element.ALIGN_CENTER);
        EstilizarCelda(celdaId, colorFila);
        tabla.addCell(celdaId);
        
        PdfPCell celdaNombre = new PdfPCell(new Phrase(usuarios.getNombre(), fuenteDato));
        EstilizarCelda(celdaNombre, colorFila);
        tabla.addCell(celdaNombre);
        
        PdfPCell celdaCorreo = new PdfPCell(new Phrase(usuarios.getCorreo(), fuenteDato));
        EstilizarCelda(celdaCorreo, colorFila);
        tabla.addCell(celdaCorreo);
        
        Font fuenteRol = usuarios.getRol().equals("ADMIN")
                ? new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                        new BaseColor(88, 86, 214))
                : new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL,
                        new BaseColor(30, 90, 160));
        
        PdfPCell celdaRol = new PdfPCell(new Phrase(usuarios.getRol(), fuenteRol));
        celdaRol.setHorizontalAlignment(Element.ALIGN_CENTER);
        EstilizarCelda(celdaRol, colorFila);
        tabla.addCell(celdaRol);
        
        boolean activo = usuarios.getActivo() == 1;
        Font fuenteEstado = activo
                ? new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                        new BaseColor(22, 101, 52)) : new Font(
                        Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(
                                153, 27, 27));
        
        PdfPCell celdaEstado = new PdfPCell(new Phrase(activo ? "Activo" : "Inactivo",
                fuenteEstado));
        celdaEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
        EstilizarCelda(celdaEstado, colorFila);
        tabla.addCell(celdaEstado);
        
        PdfPCell celdaFecha = new PdfPCell(new Phrase(usuarios.getFecharegistro().
                format(FORMATO_FECHA), fuenteDato));
        celdaFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
        EstilizarCelda(celdaFecha, colorFila);
        tabla.addCell(celdaFecha);
    }
    
    private void EstilizarCelda(PdfPCell celda, BaseColor colorFondo) {
        celda.setBackgroundColor(colorFondo);
        celda.setPadding(7f);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorderColor(new BaseColor(220, 218, 245));
        celda.setBorderWidth(0.5f);
    }
    
}
