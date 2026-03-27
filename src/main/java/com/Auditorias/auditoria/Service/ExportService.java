package com.Auditorias.auditoria.Service;

import com.Auditorias.auditoria.Entity.EventoAuditoria;
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

    @Autowired
    private AuditoriaService auditoriaService;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] ExportarUsuariosCSV() throws Exception {
        List<Usuarios> usuarios = usuarioService.ObtenerTodosActivos();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
        CSVWriter csvWriter = new CSVWriter(osw,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        String[] encabezado = {
            "ID", "NOMBRE", "CORREO", "ROL", "ESTADO", "FECHA REGISTRO"
        };
        csvWriter.writeNext(encabezado);

        for (Usuarios usuario : usuarios) {
            String[] fila = {
                String.valueOf(usuario.getIdusuario()),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo() == 1 ? "Activo" : "Inactivo",
                usuario.getFecharegistro().format(FORMATO_FECHA)
            };
            csvWriter.writeNext(fila);
        }

        csvWriter.flush();
        csvWriter.close();
        return baos.toByteArray();
    }

    public byte[] ExportarUsuariosPDF() throws Exception {
        List<Usuarios> usuarios = usuarioService.ObtenerTodosActivos();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(), 36, 36, 40, 40);
        PdfWriter.getInstance(documento, baos);
        documento.open();

        AgregarTituloPDF(documento, "Reporte de Usuarios Activos",
                "Total registros: " + usuarios.size());

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1f, 3f, 4.5f, 2f, 1.8f, 3f});
        tabla.setSpacingBefore(10f);

        String[] columnas = {"ID", "Nombre", "Correo", "Rol", "Estado", "Fecha Registro"};
        AgregarEncabezadosTabla(tabla, columnas);

        boolean par = false;
        for (Usuarios usuario : usuarios) {
            BaseColor colorFila = par
                    ? new BaseColor(238, 237, 254) : BaseColor.WHITE;

            Font fuenteDato = new Font(Font.FontFamily.HELVETICA, 9,
                    Font.NORMAL, new BaseColor(50, 50, 50));

            PdfPCell cId = new PdfPCell(
                    new Phrase(String.valueOf(usuario.getIdusuario()), fuenteDato));
            cId.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cId, colorFila);
            tabla.addCell(cId);

            PdfPCell cNombre = new PdfPCell(new Phrase(usuario.getNombre(), fuenteDato));
            EstilizarCelda(cNombre, colorFila);
            tabla.addCell(cNombre);

            PdfPCell cCorreo = new PdfPCell(new Phrase(usuario.getCorreo(), fuenteDato));
            EstilizarCelda(cCorreo, colorFila);
            tabla.addCell(cCorreo);

            Font fuenteRol = usuario.getRol().equals("ADMIN")
                    ? new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                            new BaseColor(88, 86, 214))
                    : new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL,
                            new BaseColor(30, 90, 160));
            PdfPCell cRol = new PdfPCell(new Phrase(usuario.getRol(), fuenteRol));
            cRol.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cRol, colorFila);
            tabla.addCell(cRol);

            boolean activo = usuario.getActivo() == 1;
            Font fuenteEstado = activo
                    ? new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                            new BaseColor(22, 101, 52))
                    : new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                            new BaseColor(153, 27, 27));
            PdfPCell cEstado = new PdfPCell(
                    new Phrase(activo ? "Activo" : "Inactivo", fuenteEstado));
            cEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cEstado, colorFila);
            tabla.addCell(cEstado);

            PdfPCell cFecha = new PdfPCell(
                    new Phrase(usuario.getFecharegistro().format(FORMATO_FECHA), fuenteDato));
            cFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cFecha, colorFila);
            tabla.addCell(cFecha);

            par = !par;
        }

        documento.add(tabla);
        AgregarPiePDF(documento);
        documento.close();
        return baos.toByteArray();
    }

    public byte[] ExportarAuditoriaCSV() throws Exception {
        List<EventoAuditoria> eventos = auditoriaService.ObtenerTodosEventos();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
        CSVWriter csvWriter = new CSVWriter(osw,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        String[] encabezado = {
            "ID", "TIPO EVENTO", "USUARIO INVOLUCRADO",
            "HORARIO EVENTO", "DESCRIPCION"
        };
        csvWriter.writeNext(encabezado);

        for (EventoAuditoria evento : eventos) {
            String[] fila = {
                String.valueOf(evento.getIdevento()),
                evento.getTipoevento(),
                evento.getUsuarioinvolucrado(),
                evento.getHorarioevento().format(FORMATO_FECHA),
                evento.getDescripcion()
            };
            csvWriter.writeNext(fila);
        }

        csvWriter.flush();
        csvWriter.close();
        return baos.toByteArray();
    }

    public byte[] ExportarAuditoriaPDF() throws Exception {
        List<EventoAuditoria> eventos = auditoriaService.ObtenerTodosEventos();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(), 36, 36, 40, 40);
        PdfWriter.getInstance(documento, baos);
        documento.open();

        AgregarTituloPDF(documento, "Reporte de Eventos de Auditoría",
                "Total eventos: " + eventos.size());

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1f, 3f, 3.5f, 2.5f, 5f});
        tabla.setSpacingBefore(10f);

        String[] columnas = {
            "ID", "Tipo Evento", "Usuario Involucrado",
            "Horario", "Descripción"
        };
        AgregarEncabezadosTabla(tabla, columnas);

        Font fuenteDato = new Font(Font.FontFamily.HELVETICA, 9,
                Font.NORMAL, new BaseColor(50, 50, 50));

        boolean par = false;
        for (EventoAuditoria evento : eventos) {
            BaseColor colorFila = par
                    ? new BaseColor(238, 237, 254) : BaseColor.WHITE;

            PdfPCell cId = new PdfPCell(
                    new Phrase(String.valueOf(evento.getIdevento()), fuenteDato));
            cId.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cId, colorFila);
            tabla.addCell(cId);

            Font fuenteTipo = ObtenerFuenteTipoEvento(evento.getTipoevento());
            PdfPCell cTipo = new PdfPCell(
                    new Phrase(evento.getTipoevento(), fuenteTipo));
            cTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cTipo, colorFila);
            tabla.addCell(cTipo);

            PdfPCell cUsuario = new PdfPCell(
                    new Phrase(evento.getUsuarioinvolucrado() != null
                            ? evento.getUsuarioinvolucrado() : "-", fuenteDato));
            EstilizarCelda(cUsuario, colorFila);
            tabla.addCell(cUsuario);

            PdfPCell cHorario = new PdfPCell(
                    new Phrase(evento.getHorarioevento().format(FORMATO_FECHA), fuenteDato));
            cHorario.setHorizontalAlignment(Element.ALIGN_CENTER);
            EstilizarCelda(cHorario, colorFila);
            tabla.addCell(cHorario);

            PdfPCell cDesc = new PdfPCell(
                    new Phrase(evento.getDescripcion() != null
                            ? evento.getDescripcion() : "-", fuenteDato));
            EstilizarCelda(cDesc, colorFila);
            tabla.addCell(cDesc);

            par = !par;
        }

        documento.add(tabla);
        AgregarPiePDF(documento);
        documento.close();
        return baos.toByteArray();
    }

    private void AgregarTituloPDF(Document doc,
                                   String titulo,
                                   String subtitulo) throws Exception {
        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 18,
                Font.BOLD, new BaseColor(88, 86, 214));
        Paragraph pTitulo = new Paragraph(titulo, fuenteTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        pTitulo.setSpacingAfter(6f);
        doc.add(pTitulo);

        Font fuenteSub = new Font(Font.FontFamily.HELVETICA, 9,
                Font.ITALIC, BaseColor.GRAY);
        Paragraph pSub = new Paragraph(
                "Generado: " + java.time.LocalDateTime.now().format(FORMATO_FECHA)
                + "   |   " + subtitulo, fuenteSub);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(14f);
        doc.add(pSub);

        LineSeparator linea = new LineSeparator(1f, 100f,
                new BaseColor(88, 86, 214), Element.ALIGN_CENTER, -4f);
        doc.add(new Chunk(linea));
        doc.add(Chunk.NEWLINE);
    }

    private void AgregarEncabezadosTabla(PdfPTable tabla, String[] columnas) {
        Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10,
                Font.BOLD, BaseColor.WHITE);
        BaseColor colorHeader = new BaseColor(88, 86, 214);

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

    private void AgregarPiePDF(Document doc) throws Exception {
        Font fuentePie = new Font(Font.FontFamily.HELVETICA, 9,
                Font.ITALIC, BaseColor.GRAY);
        Paragraph pie = new Paragraph(
                "Sistema de Gestión de Usuarios  —  Reporte generado automáticamente",
                fuentePie);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(14f);
        doc.add(pie);
    }

    private void EstilizarCelda(PdfPCell celda, BaseColor colorFondo) {
        celda.setBackgroundColor(colorFondo);
        celda.setPadding(7f);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorderColor(new BaseColor(220, 218, 245));
        celda.setBorderWidth(0.5f);
    }

    private Font ObtenerFuenteTipoEvento(String tipoEvento) {
        switch (tipoEvento) {
            case "CREAR_USUARIO":
                return new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                        new BaseColor(22, 101, 52));
            case "ACTUALIZAR_USUARIO":
                return new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                        new BaseColor(88, 86, 214));
            case "DESACTIVAR_USUARIO":
                return new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                        new BaseColor(153, 27, 27));
            default:
                return new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL,
                        new BaseColor(50, 50, 50));
        }
    }
}
