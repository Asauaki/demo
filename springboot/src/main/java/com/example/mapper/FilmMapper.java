package com.example.mapper;

import com.example.entity.Film;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FilmMapper {

    void insert(Film film);

    void updateById(Film film);

    void deleteById(Integer id);

    List<Film> selectAll(Film film);

    Film selectById(Integer id);

    @Select("select * from film where name = #{name}")
    Film selectByName(String name);
}
