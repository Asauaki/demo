package com.aki.mapper;

import com.aki.entity.Category;

import java.util.List;

public interface CategoryMapper {

    void insert(Category category);

    void updateById(Category category);

    void deleteById(Integer id);

    List<Category> selectAll(Category category);

}
