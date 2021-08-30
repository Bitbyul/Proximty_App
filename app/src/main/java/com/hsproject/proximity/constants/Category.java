package com.hsproject.proximity.constants;

public class Category {
    public final static String[] CATEGORIES = {
            "식사",
            "택시",
            "카풀",
            "놀이",
            "공부",
            "기타",
    };
    
    public final static String[] PREFERENCES = {
            "남자선호",
            "여자선호",
            "성별무관",
            "더치",
            "침묵!",
            "비흡연자",
            "또래",
            "급해요!",
            "한분만"
    };

    public final static String[] RANGES = {
            "5KM",
            "3KM",
            "1KM"
    };

    public static String categoriesNumToString(String categoriesNum) {
        String ret = "";
        String[] nums = categoriesNum.split(",");
        for(String numstr : nums) {
            ret += CATEGORIES[Integer.parseInt(numstr)] + "/";
        }
        ret = ret.replaceAll("/$", "");  //마지막 "/" 제거
        return ret;
    }
    public static String preferencesNumToString(String preferencesNum) {
        String ret = "";
        String[] nums = preferencesNum.split(",");
        for(String numstr : nums) {
            ret += PREFERENCES[Integer.parseInt(numstr)] + "/";
        }
        ret = ret.replaceAll("/$", "");  //마지막 "/" 제거
        return ret;
    }
}
