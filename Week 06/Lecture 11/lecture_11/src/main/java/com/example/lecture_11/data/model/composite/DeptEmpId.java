package com.example.lecture_11.data.model.composite;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeptEmpId implements Serializable {
    private Integer empNo;
    private String deptNo;
}