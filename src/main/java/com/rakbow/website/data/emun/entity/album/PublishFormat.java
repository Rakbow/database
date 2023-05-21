package com.rakbow.website.data.emun.entity.album;

import com.alibaba.fastjson2.JSON;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.MetaEmun;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-19 22:34
 * @Description: 出版形式
 */
@AllArgsConstructor
public enum PublishFormat implements MetaEmun {
    UNCATEGORIZED(0,"未分类", "Uncategorized"),
    COMMERCIAL(1, "商业发行", "Commercial"),
    INDIE_DOUJIN(2,"独立同人", "Doujin"),
    BONUS(3,"同捆特典", "Bonus"),
    EVENT_ONLY(4,"展会限定", "Event Only"),
    PREORDER(5,"预约特典", "Preorder");

    @Getter
    private final int id;
    @Getter
    private final String nameZh;
    @Getter
    private final String nameEn;

    public static String getNameById(int id, String lang) {
        for (PublishFormat item : PublishFormat.values()) {
            if (item.getId() == id) {
                if(StringUtils.equals(lang, Locale.ENGLISH.getLanguage())) {
                    return item.getNameEn();
                }else {
                    return item.getNameZh();
                }
            }
        }
        return null;
    }

    public static List<Attribute> getAttributes(String json) {

        String lang = LocaleContextHolder.getLocale().getLanguage();

        List<Attribute> res = new ArrayList<>();

        int[] ids = JSON.parseObject(json, int[].class);

        for(int id : ids) {
            res.add(new Attribute(id, getNameById(id, lang)));
        }

        return res;
    }
}
