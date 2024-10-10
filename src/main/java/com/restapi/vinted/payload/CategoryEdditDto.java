package com.restapi.vinted.payload;

import com.restapi.vinted.utils.Constant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEdditDto {

    private long id;

    @NotNull
    @Length(min = 3, max=50, message = Constant.NAME_VALIDATION_FAILED)
    private String name;

    @NotNull
    @Length(min = 10, max=100, message = Constant.DESCRIPTION_VALIDATION_FAILED)
    private String description;
}
