package com.example.aggregator.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private String id;
    private String documentId;
    private String channel;
    private String status;
}
