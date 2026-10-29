package com.tradetrack.tradingjournal.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class NoteRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    private LocalDate relatedDate;
    private Long relatedTradeId;

    public NoteRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getRelatedDate() { return relatedDate; }
    public void setRelatedDate(LocalDate relatedDate) { this.relatedDate = relatedDate; }

    public Long getRelatedTradeId() { return relatedTradeId; }
    public void setRelatedTradeId(Long relatedTradeId) { this.relatedTradeId = relatedTradeId; }
}
