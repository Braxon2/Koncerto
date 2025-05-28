package com.dusan.koncerto.controllers;

import com.dusan.koncerto.service.TicketService;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @GetMapping("/{ticketId}/pdf")
    public ResponseEntity<byte[]> generateTicketPdf(@PathVariable Long ticketId) {
        try {
            byte[] pdfBytes = ticketService.generateTicketPdf(ticketId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "ticket_" + ticketId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(("Error: " + e.getMessage()).getBytes());
        } catch (IOException | WriterException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to generate PDF: " + e.getMessage()).getBytes());
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }


}
