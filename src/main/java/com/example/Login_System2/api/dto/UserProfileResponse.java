package com.example.Login_System2.api.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private int id;
    private String adres;
    private String telefon;
    private LocalDate birthDate;
    private int userId;
    
}
