package com.barnackles.category.admin;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoryCreateDto {


    @Length(max = 100)
    private String name;
}
