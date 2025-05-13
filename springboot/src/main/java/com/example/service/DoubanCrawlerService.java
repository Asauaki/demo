package com.example.service;

import com.example.entity.Film;
import com.example.mapper.FilmMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

@Service
public class DoubanCrawlerService {

    @Autowired
    private FilmMapper filmMapper;

    private static final Logger logger = LoggerFactory.getLogger(DoubanCrawlerService.class);
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 5000; // 5秒

    private Document getDocumentWithRetry(String url) throws IOException {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                        .header("Cache-Control", "max-age=0")
                        .header("Connection", "keep-alive")
                        .header("Host", "movie.douban.com")
                        .header("Referer", "https://movie.douban.com/")
                        .header("Sec-Fetch-Dest", "document")
                        .header("Sec-Fetch-Mode", "navigate")
                        .header("Sec-Fetch-Site", "same-origin")
                        .header("Sec-Fetch-User", "?1")
                        .header("Upgrade-Insecure-Requests", "1")
                        .timeout(30000)
                        .get();
            } catch (IOException e) {
                retries++;
                if (retries == MAX_RETRIES) {
                    logger.error("获取页面失败，已重试{}次: {}", MAX_RETRIES, url);
                    throw e;
                }
                logger.warn("获取页面失败，第{}次重试: {}", retries, url);
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("重试被中断", ie);
                }
            }
        }
        throw new IOException("无法获取页面");
    }

    public List<Film> crawlDoubanTop250() {
        List<Film> films = new ArrayList<>();
        try {
            // 遍历豆瓣Top250的10个页面
            for (int i = 0; i < 10; i++) {
                logger.info("开始爬取第{}页", i + 1);
                String url = "https://movie.douban.com/top250?start=" + (i * 25);
                try {
                    Document doc = getDocumentWithRetry(url);

                    Elements items = doc.select(".item");
                    for (Element item : items) {
                        Film film = new Film();
                        
                        // 获取电影详情页URL
                        String detailUrl = item.select(".hd a").attr("href");
                        // 从URL中提取豆瓣ID
                        String doubanId = extractDoubanId(detailUrl);
                        
                        // 获取详细信息
                        Document detailDoc = getDocumentWithRetry(detailUrl);

                        // 设置基本信息
                        film.setName(detailDoc.select("h1 span[property='v:itemreviewed']").text());
                        film.setCover(item.select("img").attr("src"));
                        film.setDescr(detailDoc.select("span[property='v:summary']").text().trim());
                        
                        // 提取年份
                        String year = detailDoc.select("h1 .year").text().replaceAll("[()]", "");
                        film.setYear(year);
                        
                        // 提取导演
                        String director = detailDoc.select("a[rel='v:directedBy']").first().text();
                        film.setDirector(director);
                        
                        // 提取演员（前5个）
                        List<String> actors = new ArrayList<>();
                        detailDoc.select("a[rel='v:starring']").stream()
                                .limit(5)
                                .forEach(actor -> actors.add(actor.text()));
                        film.setActors(String.join(", ", actors));
                        
                        // 提取分类
                        List<String> genres = new ArrayList<>();
                        detailDoc.select("span[property='v:genre']")
                                .forEach(genre -> genres.add(genre.text()));
                        // 这里需要根据你的分类表来设置categoryId
                        // film.setCategoryId(mapGenreToCategory(genres));
                        
                        // 提取国家和语言
                        Element info = detailDoc.select("#info").first();
                        String infoText = info.text();
                        film.setCountry(extractInfo(infoText, "制片国家/地区:", "语言:"));
                        film.setLanguage(extractInfo(infoText, "语言:", "上映日期:"));
                        
                        // 上映日期
                        String releaseDate = detailDoc.select("span[property='v:initialReleaseDate']").text();
                        film.setDate(releaseDate.split("\\(")[0]);
                        
                        // 片长
                        String duration = detailDoc.select("span[property='v:runtime']").text();
                        film.setDuration(duration);
                        
                        // IMDb链接
                        String imdb = extractInfo(infoText, "IMDb:", null);
                        film.setImdb(imdb);

                        films.add(film);
                        
                        // 休眠1秒，避免请求过快
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    logger.error("爬取第{}页时发生错误: {}", i + 1, e.getMessage(), e);
                    continue;
                }
                // 随机等待2-5秒，避免被反爬
                Thread.sleep(2000 + new Random().nextInt(3000));
            }
        } catch (Exception e) {
            logger.error("爬取豆瓣Top250时发生错误: {}", e.getMessage(), e);
        }
        logger.info("爬取完成，共获取{}部电影", films.size());
        return films;
    }

    // 从URL中提取豆瓣ID
    private String extractDoubanId(String url) {
        Pattern pattern = Pattern.compile("/subject/(\\d+)/");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // 从信息文本中提取特定字段
    private String extractInfo(String text, String startKey, String endKey) {
        int start = text.indexOf(startKey);
        if (start == -1) return "";
        start += startKey.length();
        
        int end;
        if (endKey != null) {
            end = text.indexOf(endKey, start);
            if (end == -1) end = text.length();
        } else {
            end = text.length();
        }
        
        return text.substring(start, end).trim();
    }

    // 导入电影到数据库
    public void importMovies() {
        List<Film> films = crawlDoubanTop250();
        for (Film film : films) {
            try {
                // 检查电影是否已存在
                Film existingFilm = filmMapper.selectByName(film.getName());
                if (existingFilm == null) {
                    filmMapper.insert(film);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
} 