package com.github.tianmu19.tphotoviewerlibrary;

/**
 * @author sunwei
 * 邮箱：tianmu19@gmail.com
 * 时间：2019/3/21 21:49
 * 包名：com.github.tianmu19.tphotoviewerlibrary
 * <p>description:   图片类         </p>
 */
public class TImageEntity {
    private String thumbUrl;
    private String originUrl;
    private String desc;
    private String date ;

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getThumbUrl() {
        return thumbUrl == null ? "" : thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getOriginUrl() {
        return originUrl == null ? "" : originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getDesc() {
        return desc == null ? "" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
