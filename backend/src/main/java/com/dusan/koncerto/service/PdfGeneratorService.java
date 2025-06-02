package com.dusan.koncerto.service;

import com.dusan.koncerto.model.Event;
import com.dusan.koncerto.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfGeneratorService {

    private static final int QR_CODE_SIZE = 250;

    public byte[] generateTicketPdf(Ticket ticket) throws IOException, WriterException, DocumentException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();


        Event event = ticket.getEventTicket().getEvent();
        String eventName = event.getArtist() + " - " + event.getDescription();
        String artistName = event.getArtist();
        String eventDate = event.getDateTime().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"));
        String ticketType = ticket.getEventTicket().getTicketType();
        String userName = ticket.getUser().getLastName();
        String ticketId = String.valueOf(ticket.getId());

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.NORMAL);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.NORMAL);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.NORMAL);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.NORMAL);

        Paragraph title = new Paragraph("--- Your Event Ticket ---", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(Chunk.NEWLINE)); // Add some space

        Paragraph pEvent = new Paragraph("Event: ", headerFont);
        pEvent.add(new Chunk(eventName, normalFont));
        document.add(pEvent);

        Paragraph pArtist = new Paragraph("Artist: ", normalFont);
        pArtist.add(new Chunk(artistName, normalFont));
        document.add(pArtist);

        Paragraph pDateTime = new Paragraph("Date & Time: ", normalFont);
        pDateTime.add(new Chunk(eventDate, normalFont));
        document.add(pDateTime);

        Paragraph pTicketType = new Paragraph("Ticket Type: ", normalFont);
        pTicketType.add(new Chunk(ticketType, normalFont));
        document.add(pTicketType);

        Paragraph pHolder = new Paragraph("Holder: ", normalFont);
        pHolder.add(new Chunk(userName, normalFont));
        document.add(pHolder);

        Paragraph pTicketId = new Paragraph("Ticket ID: ", smallFont);
        pTicketId.add(new Chunk(ticketId, smallFont));
        document.add(pTicketId);

        document.add(new Paragraph(Chunk.NEWLINE));
        document.add(new Paragraph(Chunk.NEWLINE));

        byte[] qrCodeImageBytes = generateQrCodeImage(ticket.getQrContent());
        if (qrCodeImageBytes != null) {
            Image qrCode = Image.getInstance(qrCodeImageBytes);
            qrCode.scaleAbsolute(QR_CODE_SIZE, QR_CODE_SIZE);
            qrCode.setAlignment(Element.ALIGN_CENTER);
            document.add(qrCode);
        }

        document.add(new Paragraph(Chunk.NEWLINE));
        Paragraph footer = new Paragraph("Enjoy the event!", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private byte[] generateQrCodeImage(String qrContent) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

}
