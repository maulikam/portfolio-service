package com.codingreflex.renilalgo.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class InstrumentProcessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long instrumentToken;
    @Column(name = "is_successfully_processed")
    private Boolean isSucessfullyProcessed;
    private Integer processedCount;
    private String errorMessage;
}
