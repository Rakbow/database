package com.rakbow.website.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.album.AlbumFormat;
import com.rakbow.website.data.album.MediaFormat;
import com.rakbow.website.data.album.PublishFormat;
import com.rakbow.website.entity.Album;
import com.rakbow.website.util.common.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-19 23:44
 * @Description:
 */
public class AlbumUtils {

    /**
     * 获取专辑分类数组
     * @author rakbow
     * @return list 专辑分类数组
     * */
    public static List<JSONObject> getAlbumFormatSet(){
        List<JSONObject> list = new ArrayList<>();
        for(AlbumFormat albumFormat : AlbumFormat.values()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label",albumFormat.getName());
            jsonObject.put("labelEn",albumFormat.getNameEn());
            jsonObject.put("value",albumFormat.getIndex());
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 获取媒体类型数组
     * @author rakbow
     * @return list 媒体类型数组
     * */
    public static List<JSONObject> getMediaFormatSet(){
        List<JSONObject> list = new ArrayList<>();
        for(MediaFormat mediaFormat : MediaFormat.values()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label",mediaFormat.getName());
            jsonObject.put("labelEn",mediaFormat.getNameEn());
            jsonObject.put("value",mediaFormat.getIndex());
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 获取出版形式数组
     * @author rakbow
     * @return list 出版形式数组
     * */
    public static List<JSONObject> getPublishFormatSet(){
        List<JSONObject> list = new ArrayList<>();
        for(PublishFormat publishFormat : PublishFormat.values()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label",publishFormat.getName());
            jsonObject.put("labelEn",publishFormat.getNameEn());
            jsonObject.put("value",publishFormat.getIndex());
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 获取专辑封面图片
     * @author rakbow
     * @param album 专辑
     * @return coverUrl 专辑封面图片url
     * */
    public static String getAlbumCoverUrl(Album album) {
        //先赋值为404图片
        String coverUrl = CommonConstant.EMPTY_IMAGE_URL;

        JSONArray images = JSON.parseArray(album.getImages());

        for (int i = 0; i < images.size(); i++) {
            JSONObject image = images.getJSONObject(i);
            if (StringUtils.equals(image.getString("type"), Integer.toString(CommonConstant.ALBUM_IMAGE_COVER))) {
                coverUrl = image.getString("url");
            }
        }
        return coverUrl;
    }

}
