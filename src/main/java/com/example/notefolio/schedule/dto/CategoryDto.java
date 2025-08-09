package com.example.notefolio.schedule.dto;

import com.example.notefolio.schedule.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryDto {
    private Long id; //카테고리 ID

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name; //카테고리 이름

    @NotNull
    private String colorCode; //카테고리 컬러

    //category를 CategoryDto 객체로 바꿔주는 메서드
    public static CategoryDto from(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getColorCode());
    }
}
