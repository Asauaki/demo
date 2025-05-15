package com.aki.controller;

import com.aki.common.Result;
import com.aki.entity.Category;
import com.aki.entity.Film;
import com.aki.service.CategoryService;
import com.aki.service.FilmService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/film")
public class FilmController {

    @Resource
    private FilmService filmService;

    @Resource
    private CategoryService categoryService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Film film) {
        // 根据分类名称查找分类ID
        Category category = categoryService.findByName(film.getCategoryName());
        if (category != null) {
            film.setCategoryId(category.getId());
        } else {
            return Result.error("分类不存在");
        }
        filmService.add(film);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Film film) {
        // 根据分类名称查找分类ID
        Category category = categoryService.findByName(film.getCategoryName());
        if (category != null) {
            film.setCategoryId(category.getId());
        } else {
            return Result.error("分类不存在");
        }
        filmService.update(film);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        filmService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Film film) {
        List<Film> list = filmService.selectAll(film);
        return Result.success(list);
    }

    /**
     * 查询电影评分榜
     */
    @GetMapping("/selectRanking")
    public Result selectRanking() {
        List<Film> list = filmService.selectRanking();
        return Result.success(list);
    }

    @GetMapping("/selectRecommend/{id}")
    public Result selectRecommend(@PathVariable Integer id) {
        List<Film> list = filmService.selectRecommend(id);
        return Result.success(list);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Film film = filmService.selectById(id);
        return Result.success(film);
    }

    /**
     * 分页模糊查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Film film,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Film> pageInfo = filmService.selectPage(film, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
