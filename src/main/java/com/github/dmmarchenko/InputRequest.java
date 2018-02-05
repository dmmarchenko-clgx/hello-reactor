package com.github.dmmarchenko;

import lombok.Data;

import java.util.List;

@Data
public class InputRequest {
    private List<String> months;
    private List<String> weekDays;
}
