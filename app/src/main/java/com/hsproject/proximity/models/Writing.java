package com.hsproject.proximity.models;

import java.io.Serializable;

public class Writing implements Serializable {
    String title;
    String intro;
    String categories;

    public Writing(String title, String categories, String preferred) {
        this.title = title;
        this.intro = categories;
        this.categories = preferred;
    }

    public String getCategories() {
        return categories;
    }

    public String getIntro() {
        return intro;
    }

    public String getTitle() {
        return title;
    }
    public void sys_out_test() {
        System.out.println("정상출력 확인 : " + this.title + " / "+ this.intro + " / " + this.categories);
    }

}
