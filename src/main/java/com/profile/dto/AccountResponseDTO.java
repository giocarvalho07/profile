package com.profile.dto;

public class AccountResponseDTO {

    private Long idUser;
    private String name;
    private Integer age;
    private String email;

    public AccountResponseDTO(Long idUser, String name, Integer age, String email) {
        this.idUser = idUser;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // Getters
    public Long getIdUser() { return idUser; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getEmail() { return email; }
}