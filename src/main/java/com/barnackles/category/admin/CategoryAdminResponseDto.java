package com.barnackles.category.admin;

import lombok.Data;

import javax.persistence.Id;

@Data
public class CategoryAdminResponseDto {

    @Id
    private Long id;
    private String name;

}
