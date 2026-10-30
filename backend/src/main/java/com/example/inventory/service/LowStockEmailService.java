package com.example.inventory.service;

import com.example.inventory.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.notifications.enabled", havingValue = "true")
public class LowStockEmailService {
    private static final Logger log = LoggerFactory.getLogger(LowStockEmailService.class);
    private final JavaMailSender mailSender;
    private final String recipient;
    private final String from;

    public LowStockEmailService(JavaMailSender mailSender,
            @Value("${app.notifications.recipient:}") String recipient,
            @Value("${app.notifications.from:}") String from) {
        this.mailSender = mailSender;
        this.recipient = recipient == null ? "" : recipient.trim();
        this.from = from == null ? "" : from.trim();
    }

    public void sendLowStockAlert(Product product, int currentQuantity) {
        if (recipient.isBlank()) {
            log.warn("Low-stock email was not sent because INVENTORY_ALERT_EMAIL_TO is not configured.");
            return;
        }
        if (from.isBlank()) {
            log.warn("Low-stock email was not sent because INVENTORY_ALERT_EMAIL_FROM is not configured.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject((currentQuantity <= 0 ? "StockPilot out-of-stock alert: " : "StockPilot low-stock alert: ") + product.getName());
        message.setText((currentQuantity <= 0
                ? "A product is now out of stock.\n\n"
                : "A product has fallen below its configured minimum stock level.\n\n")
                + "Product: " + product.getName() + "\n"
                + "SKU: " + (product.getSku() == null ? "Not set" : product.getSku()) + "\n"
                + "Current total stock: " + currentQuantity + "\n"
                + "Minimum stock: " + product.getMinimumStock() + "\n\n"
                + "Please review the inventory and arrange replenishment if required.\n\n"
                + "StockPilot Inventory Management System");
        try {
            mailSender.send(message);
            log.info("Low-stock email sent for product id {}.", product.getId());
        } catch (MailException ex) {
            // Notification failure must not hide a successful inventory operation.
            log.error("Unable to send low-stock email for product id {}: {}", product.getId(), ex.getMessage());
        }
    }


    public void sendProductCreated(Product product) {
        sendProductChangeNotification("Product created", product,
                "A new product was added to StockPilot inventory.");
    }

    public void sendProductUpdated(Product product) {
        sendProductChangeNotification("Product updated", product,
                "An existing product was updated in StockPilot inventory.");
    }

    private void sendProductChangeNotification(String event, Product product, String description) {
        if (recipient.isBlank() || from.isBlank()) {
            log.warn("{} notification was not sent because recipient/from email is not configured.", event);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("StockPilot: " + event + " — " + product.getName());
        message.setText(description + "\n\n"
                + "Product: " + product.getName() + "\n"
                + "SKU: " + (product.getSku() == null || product.getSku().isBlank() ? "Not set" : product.getSku()) + "\n"
                + "Category: " + (product.getCategory() == null || product.getCategory().isBlank() ? "Not set" : product.getCategory()) + "\n"
                + "Price: " + (product.getPrice() == null ? "Not set" : product.getPrice()) + "\n"
                + "Minimum stock: " + product.getMinimumStock() + "\n\n"
                + "StockPilot Inventory Management System");
        try {
            mailSender.send(message);
            log.info("{} notification sent for product id {}.", event, product.getId());
        } catch (MailException ex) {
            // Email failures must not undo a successful product operation.
            log.error("Unable to send {} notification for product id {}: {}", event, product.getId(), ex.getMessage());
        }
    }
}
