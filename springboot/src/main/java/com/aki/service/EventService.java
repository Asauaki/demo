package com.aki.service;

import cn.hutool.core.date.DateUtil;
import com.aki.entity.Event;
import com.aki.mapper.EventMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Resource
    private EventMapper eventMapper;

    /**
     * 新增
     */
    public void add(Event event) {
        event.setTime(DateUtil.now());  // 获取当前的最新的时间的字符串  设置到event对象中去
        eventMapper.insert(event);
    }

    /**
     * 修改
     */
    public void update(Event event) {
        eventMapper.updateById(event);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        eventMapper.deleteById(id);
    }

    /**
     * 查询所有
     */
    public List<Event> selectAll(Event event) {
        return eventMapper.selectAll(event);
    }

    /**
     * 分页查询
     */
    public PageInfo<Event> selectPage(Event event, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Event> list = eventMapper.selectAll(event);
        return PageInfo.of(list);
    }

    public Event selectById(Integer id) {
        return eventMapper.selectById(id);
    }

    public Event selectPrevious(Integer id) {
        return eventMapper.selectPrevious(id);
    }

    public Event selectNext(Integer id) {
        return eventMapper.selectNext(id);
    }

}
