package com.example.aggregator.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveDto {
  private String id;
  private String documentId;
  private String archivedAt;
}
