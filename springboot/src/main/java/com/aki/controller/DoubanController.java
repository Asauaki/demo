package com.aki.controller;

import com.aki.common.Result;
import com.aki.service.DoubanCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/douban")
public class DoubanController {
    
    @Autowired
    private DoubanCrawlerService doubanCrawlerService;
    
    @PostMapping("/import")
    public Result importDoubanMovies() {
        try {
            doubanCrawlerService.importMovies();
            return Result.success("电影导入成功");
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }
} 