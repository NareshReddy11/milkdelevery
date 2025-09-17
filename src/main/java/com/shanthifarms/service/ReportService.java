package com.shanthifarms.service;

import com.shanthifarms.model.DeliveryRecord;
import com.opencsv.CSVWriter;
import com.shanthifarms.repository.DeliveryRecordRepository;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final DeliveryRecordRepository recordRepo;

    public ReportService(DeliveryRecordRepository recordRepo) {
        this.recordRepo = recordRepo;
    }

    /**
     * Export delivery records for a given date into CSV.
     */
    public void writeCsvForDate(LocalDate date, OutputStream out) throws Exception {
        List<DeliveryRecord> records = recordRepo.findByDeliveryDate(date);

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
            writer.writeNext(new String[]{"Customer", "Phone", "Liters", "PlanId", "Status", "DeliveredAt"});
            for (DeliveryRecord r : records) {
                String customer = r.getMilkPlan().getCustomer().getName() == null ? "" : r.getMilkPlan().getCustomer().getName();
                String phone = r.getMilkPlan().getCustomer().getPhone() == null ? "" : r.getMilkPlan().getCustomer().getPhone();
                String liters = String.valueOf(r.getMilkPlan().getLitersPerDay());
                String planId = String.valueOf(r.getMilkPlan().getId());
                String status = r.getStatus() == null ? "" : r.getStatus();
                String deliveredAt = r.getDeliveredAt() == null ? "" : r.getDeliveredAt().toString();

                writer.writeNext(new String[]{customer, phone, liters, planId, status, deliveredAt});
            }
        }
    }

    /**
     * Export delivery records for a given date into PDF.
     */
    public void writePdfForDate(LocalDate date, OutputStream out) throws Exception {
        List<DeliveryRecord> records = recordRepo.findByDeliveryDate(date);

        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument()) {
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            doc.addPage(page);

            try (org.apache.pdfbox.pdmodel.PDPageContentStream stream =
                         new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {

                stream.beginText();
                stream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 14);
                stream.newLineAtOffset(50, 750);
                stream.showText("Delivery Report for " + date);

                stream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);

                int y = 730;
                for (DeliveryRecord r : records) {
                    String line = String.format("%s | %s | %sL | %s | %s | %s",
                            r.getMilkPlan().getCustomer().getName(),
                            r.getMilkPlan().getCustomer().getPhone(),
                            r.getMilkPlan().getLitersPerDay(),
                            r.getMilkPlan().getId(),
                            r.getStatus(),
                            r.getDeliveredAt() == null ? "" : r.getDeliveredAt().toString());

                    stream.newLineAtOffset(0, -20);
                    stream.showText(line);
                    y -= 20;
                }

                stream.endText();
            }

            doc.save(out);
        }
    }
}
