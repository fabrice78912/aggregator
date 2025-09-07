package com.example.aggregator.model;

import com.example.aggregator.model.dtos.ArchiveDto;
import com.example.aggregator.model.dtos.DocumentDto;
import com.example.aggregator.model.dtos.NotificationDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentHistory {
  private String clientId;
  private List<DocumentDto> documents;
  private List<ArchiveDto> archives;
  private List<NotificationDto> notifications;
}
