package com.rakbow.website.data.emun.entity.album;

import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.system.SystemLanguage;
import com.rakbow.website.util.common.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-19 22:57
 * @Description: 专辑分类
 */
@AllArgsConstructor
public enum AlbumFormat {
    UNCATEGORIZED(0,"未分类", "Uncategorized"),
    VOCAL(1, "歌曲","Vocal"),
    OPENING_THEME(2, "片头曲","Opening Theme"),
    ENDING_THEME(3, "片尾曲","Ending Theme"),
    INSERT_SONG(4, "插入曲","Insert Song"),
    SOUNDTRACK(5, "原声","Soundtrack"),
    CHARACTER_SONG(6, "角色曲","Character Song"),
    DRAMA(7, "广播剧","Drama"),
    TALK(8, "广播电台","Talk"),
    REMIX(9, "混音","Remix"),
    DOUJIN_REMIX(10, "同人混音","Doujin Remix"),
    DERIVATIVE(11, "衍生曲","Derivative"),
    ARRANGEMENT(12, "改编","Arrangement"),
    DOUJIN_ARRANGEMENT(13,"同人改编","Doujin Arrangement"),
    VIDEO(14,"影片","Video");

    @Getter
    private final int id;
    @Getter
    private final String nameZh;
    @Getter
    private final String nameEn;

    public static List<String> getNamesByIds(List<Integer> ids) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        return ids.stream().map(id -> getNameById(id, lang)).collect(Collectors.toList());
    }

    public static List<Integer> getIdsByNames(List<String> names) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        if (names.isEmpty()) return new ArrayList<>();
        return names.stream().map(name -> getIdByName(name, lang)).collect(Collectors.toList());
    }

    public static String getNameById(int id, String lang) {
        for (AlbumFormat format : AlbumFormat.values()) {
            if (format.getId() == id) {
                if(StringUtils.equals(lang, SystemLanguage.ENGLISH.getCode())) {
                    return format.getNameEn();
                }else {
                    return format.getNameZh();
                }
            }
        }
        return null;
    }

    public static int getIdByName(String name, String lang) {
        if(StringUtils.equals(lang, SystemLanguage.ENGLISH.getCode())) {
            for (AlbumFormat format : AlbumFormat.values()) {
                if(StringUtils.equals(name, format.nameEn)) {
                    return format.id;
                }
            }
        }else {
            for (AlbumFormat format : AlbumFormat.values()) {
                if(StringUtils.equals(name, format.nameZh)) {
                    return format.id;
                }
            }
        }
        return 0;
    }

    public static List<Attribute> getAttributes(String json) {

        String lang = LocaleContextHolder.getLocale().getLanguage();

        List<Attribute> res = new ArrayList<>();

        List<Integer> ids = CommonUtil.ids2List(json);

        ids.forEach(id -> {
            res.add(new Attribute(id, getNameById(id, lang)));
        });

        return res;
    }

    /**
     * 获取专辑分类数组
     *
     * @return list 专辑分类数组
     * @author rakbow
     */
    public static List<Attribute> getAttributeSet(String lang) {
        List<Attribute> set = new ArrayList<>();
        if(StringUtils.equals(lang, SystemLanguage.ENGLISH.getCode())) {
            for (AlbumFormat item : AlbumFormat.values()) {
                set.add(new Attribute(item.id, item.nameEn));
            }
        }else if(StringUtils.equals(lang, SystemLanguage.CHINESE.getCode())) {
            for (AlbumFormat item : AlbumFormat.values()) {
                set.add(new Attribute(item.id, item.nameZh));
            }
        }
        return set;
    }

}
