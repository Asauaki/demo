package com.aki.controller;

import com.aki.common.Result;
import com.aki.entity.Event;
import com.aki.service.EventService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Resource
    private EventService eventService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Event event) {
        eventService.add(event);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Event event) {
        eventService.update(event);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        eventService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Event event) {
        List<Event> list = eventService.selectAll(event);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Event event,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Event> pageInfo = eventService.selectPage(event, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 根据ID查询活动详情
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Event event = eventService.selectById(id);
        return Result.success(event);
    }

    /**
     * 获取上一个活动
     */
    @GetMapping("/selectPrevious/{id}")
    public Result selectPrevious(@PathVariable Integer id) {
        Event event = eventService.selectPrevious(id);
        return Result.success(event);
    }

    /**
     * 获取下一个活动
     */
    @GetMapping("/selectNext/{id}")
    public Result selectNext(@PathVariable Integer id) {
        Event event = eventService.selectNext(id);
        return Result.success(event);
    }

}
