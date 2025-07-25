package com.example.Login_System2.api.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;   

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileRequest {
    private String adres;
    private String telefon;
    private LocalDate birthDate;
}
