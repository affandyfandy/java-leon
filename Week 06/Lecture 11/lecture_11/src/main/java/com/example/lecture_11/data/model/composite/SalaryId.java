package com.example.lecture_11.data.model.composite;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalaryId implements Serializable {
    private Integer empNo;
    private LocalDate fromDate;
}
