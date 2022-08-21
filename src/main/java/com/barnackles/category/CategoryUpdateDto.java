package com.barnackles.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoryUpdateDto {

    @Length(max = 100)
    private String name;

}
