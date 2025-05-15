package com.aki.service;

import com.aki.entity.Film;
import com.aki.entity.Category;
import com.aki.mapper.FilmMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DoubanCrawlerService {

    @Autowired
    private FilmMapper filmMapper;

    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(DoubanCrawlerService.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

    // 分类映射表
    private final Map<String, String> genreMapping = new HashMap<>();

    public DoubanCrawlerService() {
        // 初始化豆瓣电影类型到系统分类的映射
        genreMapping.put("剧情", "剧情");
        genreMapping.put("喜剧", "喜剧");
        genreMapping.put("动作", "动作");
        genreMapping.put("爱情", "爱情");
        genreMapping.put("科幻", "科幻");
        genreMapping.put("动画", "动画");
        genreMapping.put("悬疑", "悬疑");
        genreMapping.put("惊悚", "惊悚");
        genreMapping.put("恐怖", "恐怖");
        genreMapping.put("犯罪", "犯罪");
        genreMapping.put("同性", "同性");
        genreMapping.put("音乐", "音乐");
        genreMapping.put("歌舞", "歌舞");
        genreMapping.put("传记", "传记");
        genreMapping.put("历史", "历史");
        genreMapping.put("战争", "战争");
        genreMapping.put("西部", "西部");
        genreMapping.put("奇幻", "奇幻");
        genreMapping.put("冒险", "冒险");
        genreMapping.put("灾难", "灾难");
        genreMapping.put("武侠", "武侠");
        genreMapping.put("情色", "情色");
        genreMapping.put("纪录片", "纪录片");
        genreMapping.put("短片", "短片");
        genreMapping.put("运动", "运动");
        genreMapping.put("儿童", "儿童");
        genreMapping.put("黑色电影", "黑色电影");
    }

    // 获取或创建分类ID
    private Integer getOrCreateCategoryId(String genre) {
        if (genre == null || genre.trim().isEmpty()) return null;

        String mappedGenre = genreMapping.getOrDefault(genre.trim(), genre.trim());

        Category existing = categoryService.findByName(mappedGenre);
        if (existing != null) return existing.getId();

        Category newCategory = new Category();
        newCategory.setName(mappedGenre);
        categoryService.add(newCategory);
        logger.info("创建新分类: {}", mappedGenre);

        // 再查一遍以拿到 ID
        existing = categoryService.findByName(mappedGenre);
        return existing != null ? existing.getId() : null;
    }

    // 爬取豆瓣 Top250 的第一页
    public List<Film> crawlDoubanTop250() {
        List<Film> films = new ArrayList<>();
        String url = "https://movie.douban.com/top250?start=0";

        try {
            logger.info("开始爬取第一页: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(30000)
                    .get();

            Elements items = doc.select("#content .grid_view li");
            logger.info("找到 {} 个电影条目", items.size());

            for (Element item : items) {
                try {
                    Film film = new Film();

                    // 标题
                    Element titleElement = item.select(".title").first();
                    if (titleElement == null) continue;
                    film.setName(titleElement.text());

                    // 封面图
                    Element imgElement = item.selectFirst("img");
                    if (imgElement != null) film.setCover(imgElement.attr("src"));

                    // 评分
                    Element scoreElement = item.selectFirst(".rating_num");
                    if (scoreElement != null) {
                        String score = scoreElement.text();
                        if (!score.isEmpty()) film.setScore(Double.parseDouble(score));
                    }

                    // 评论数
                    Element commentElement = item.selectFirst(".star span:last-child");
                    if (commentElement != null) {
                        String commentNum = commentElement.text().replaceAll("[^0-9]", "");
                        if (!commentNum.isEmpty()) film.setCommentNum(Integer.parseInt(commentNum));
                    }

                    // 基本信息解析（年份/国家/类型）
                    Element infoElement = item.selectFirst(".bd p");
                    if (infoElement != null) {
                        String[] parts = infoElement.text().split("\\s+/\\s+");
                        if (parts.length >= 3) {
                            film.setYear(parts[0].replaceAll("[^0-9]", ""));
                            film.setCountry(parts[1].trim());

                            String[] genres = parts[2].trim().split("\\s+");
                            if (genres.length > 0) {
                                Integer categoryId = getOrCreateCategoryId(genres[0].trim());
                                film.setCategoryId(categoryId);
                            }
                        }
                    }

                    // 简介
                    Element quoteElement = item.selectFirst(".quote .inq");
                    if (quoteElement != null) film.setDescr(quoteElement.text());

                    // 详情页处理
                    Element linkElement = item.selectFirst(".hd a");
                    if (linkElement != null) {
                        String detailUrl = linkElement.attr("href");

                        try {
                            Document detailDoc = Jsoup.connect(detailUrl)
                                    .userAgent(USER_AGENT)
                                    .timeout(30000)
                                    .get();

                            Element movieInfo = detailDoc.selectFirst("#info");
                            if (movieInfo != null) {
                                String infoText = movieInfo.text();

                                // 正则提取字段
                                film.setDirector(getMatch(infoText, "导演:?\\s*([^\\s/]+)"));
                                film.setActors(getMatch(infoText, "主演:?\\s*([^\\s/]+)"));
                                film.setLanguage(getMatch(infoText, "语言:?\\s*([^\\s/]+)"));
                                film.setCountry(getMatch(infoText, "制片国家/地区:?\\s*([^\\s/]+)"));
                                film.setDuration(getMatch(infoText, "片长:?\\s*([^\\s/]+)"));
                                film.setImdb(getMatch(infoText, "IMDb链接:?\\s*(https?://[\\w./\\-]+)"));

                                // 上映日期（完整）
                                String dateStr = getMatch(infoText, "上映日期:?\\s*(\\d{4}-\\d{2}-\\d{2})");
                                if (dateStr != null) {
                                    film.setDate(dateStr);
                                    film.setYear(dateStr.substring(0, 4));
                                }

                                // 类型（详情页优先覆盖）
                                String genre = getMatch(infoText, "类型:?\\s*([^\\s/]+)");
                                if (genre != null) {
                                    Integer categoryId = getOrCreateCategoryId(genre);
                                    film.setCategoryId(categoryId);
                                }
                            }

                            // 描述（v:summary）
                            Element descrElement = detailDoc.selectFirst("span[property=v:summary]");
                            if (descrElement != null && !descrElement.text().isEmpty()) {
                                film.setDescr(descrElement.text().trim());
                            }

                            // 获取 IMDb ID
                            Element imdbElement = detailDoc.select("span.pl:contains(IMDb:)").first();
                            if (imdbElement != null) {
                                String imdbId = imdbElement.nextSibling().toString().trim();  // 提取IMDB ID部分
                                film.setImdb(imdbId);
                            }



                        } catch (Exception e) {
                            logger.warn("爬取详情页失败: {} 错误: {}", detailUrl, e.getMessage());
                        }
                    }

                    films.add(film);
                    logger.info("成功处理电影: {}", film.getName());

                    // 随机延时 2-4 秒
                    Thread.sleep(2000 + new Random().nextInt(2000));

                } catch (Exception e) {
                    logger.error("处理电影条目失败: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("爬取 Top250 页面失败: {}", e.getMessage());
        }

        logger.info("爬取完成，共获取 {} 部电影", films.size());
        return films;
    }

    // 导入电影到数据库
    public void importMovies() {
        List<Film> films = crawlDoubanTop250();
        int success = 0, skip = 0, error = 0;

        for (Film film : films) {
            try {
                Film existing = filmMapper.selectByName(film.getName());
                if (existing == null) {
                    filmMapper.insert(film);
                    logger.info("导入成功: {}", film.getName());
                    success++;
                } else {
                    logger.info("已存在，跳过: {}", film.getName());
                    skip++;
                }
            } catch (Exception e) {
                logger.error("导入失败: {}, 错误: {}", film.getName(), e.getMessage());
                error++;
            }
        }

        logger.info("导入完成：成功: {}, 跳过: {}, 失败: {}", success, skip, error);
    }

    // 提取正则匹配
    private String getMatch(String text, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}
