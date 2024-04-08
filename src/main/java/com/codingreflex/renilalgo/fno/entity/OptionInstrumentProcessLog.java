package com.codingreflex.renilalgo.fno.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class OptionInstrumentProcessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String instrumentToken;
    @Column(name = "is_successfully_processed")
    private Boolean isSucessfullyProcessed;
    private Integer processedCount;
    private String errorMessage;
}
