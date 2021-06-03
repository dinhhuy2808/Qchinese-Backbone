package com.nhs3108.utils;

import java.util.ArrayList;
import java.util.List;

import com.elearning.models.News;

/**
 * Created by nhs3108 on 4/2/17.
 */
public class FakeUtils {
    public static List<News> getAllNews() {
        List<News> newsList = new ArrayList<News>();
        newsList.add(new News("Title 1", "Framgia - From asian to the world", "Hong Son"));
        newsList.add(new News("Title 2", "Framgia - We make it awesome", "Son Nguyen"));
        newsList.add(new News("Title 3", "Framgia - 2012 to 2017", "Mr Son"));
        return newsList;
    }
}
