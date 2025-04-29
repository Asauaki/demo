package com.example.mapper;

import com.example.entity.Event;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EventMapper {

    void insert(Event event);

    void updateById(Event event);

    void deleteById(Integer id);

    List<Event> selectAll(Event event);

    @Select("select * from event where id = #{id}")
    Event selectById(Integer id);

    @Select("select * from event where id < #{id} order by id desc limit 1")
    Event selectPrevious(Integer id);

    @Select("select * from event where id > #{id} order by id asc limit 1")
    Event selectNext(Integer id);

}
