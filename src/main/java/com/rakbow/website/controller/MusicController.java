package com.rakbow.website.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.emun.common.DataActionType;
import com.rakbow.website.data.emun.common.EntityType;
import com.rakbow.website.entity.Music;
import com.rakbow.website.service.*;
import com.rakbow.website.util.common.RedisUtil;
import com.rakbow.website.util.entity.MusicUtil;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.ApiResult;
import com.rakbow.website.util.convertMapper.AlbumVOMapper;
import com.rakbow.website.util.convertMapper.MusicVOMapper;
import com.rakbow.website.util.file.CommonImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-11-06 19:50
 * @Description:
 */
@Controller
@RequestMapping("/db/music")
public class MusicController {

    //region ------引入实例------
    @Autowired
    private MusicService musicService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    private final MusicVOMapper musicVOMapper = MusicVOMapper.INSTANCES;
    //endregion

    //获取单个音频详细信息页面
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public String getMusicDetail(@PathVariable("id") int musicId, Model model) {
        if (musicService.getMusicById(musicId) == null) {
            model.addAttribute("errorMessage", String.format(ApiInfo.GET_DATA_FAILED_404, EntityType.MUSIC.getNameZh()));
            return "/error/404";
        }
        //访问数+1
        visitService.increaseVisit(EntityType.MUSIC.getId(), musicId);

        Music music = musicService.getMusicById(musicId);

        model.addAttribute("audioTypeSet", redisUtil.get("audioTypeSet"));
        model.addAttribute("music", musicVOMapper.music2VO(music));
        //获取页面信息
        model.addAttribute("pageInfo", visitService.getPageInfo(EntityType.MUSIC.getId(), musicId, music.getAddedTime(), music.getEditedTime()));
        //获取同属一张碟片的音频
        model.addAttribute("relatedMusics", musicService.getRelatedMusics(musicId));
        //获取所属专辑的信息
        model.addAttribute("relatedAlbum", AlbumVOMapper.INSTANCES.album2VOBeta(albumService.getAlbumById(music.getAlbumId())));

        return "/music/music-detail";

    }

    //更新Music
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateMusic(@RequestBody  String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONObject param = JSON.parseObject(json);
        try{
            if (userService.checkAuthority(request).state) {

                Music music = musicService.json2Music(param);

                //检测数据
                if(!StringUtils.isBlank(musicService.checkMusicJson(param))) {
                    res.setErrorMessage(musicService.checkMusicJson(param));
                    return JSON.toJSONString(res);
                }

                //修改编辑时间
                music.setEditedTime(new Timestamp(System.currentTimeMillis()));

                musicService.updateMusic(music.getId(), music);

                //将更新的专辑保存到Elasticsearch服务器索引中

                res.message = String.format(ApiInfo.UPDATE_DATA_SUCCESS, EntityType.MUSIC.getNameZh());

            }else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //更新music创作人员信息
    @RequestMapping(path = "/update-artists", method = RequestMethod.POST)
    @ResponseBody
    public String updateMusicArtists(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String artists = JSON.parseObject(json).get("artists").toString();
                musicService.updateMusicArtists(id, artists);
                res.message = ApiInfo.UPDATE_MUSIC_ARTISTS_SUCCESS;
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //更新歌词文本
    @RequestMapping(path = "/update-lyrics-text", method = RequestMethod.POST)
    @ResponseBody
    public String updateMusicLyricsText(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String lyricsText = JSON.parseObject(json).get("lyricsText").toString();
                musicService.updateMusicLyricsText(id, lyricsText);
                res.message = ApiInfo.UPDATE_MUSIC_LYRICS_SUCCESS;
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //更新描述信息
    @RequestMapping(path = "/update-description", method = RequestMethod.POST)
    @ResponseBody
    public String updateMusicDescription(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String description = JSON.parseObject(json).get("description").toString();
                musicService.updateMusicDescription(id, description);
                res.message = ApiInfo.UPDATE_MUSIC_DESCRIPTION_SUCCESS;
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //更新音频，新增或删除
    @RequestMapping(path = "/update-file", method = RequestMethod.POST)
    @ResponseBody
    public String updateMusicFile(int id, MultipartFile[] files, String fileInfos, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {

                if (files == null || files.length == 0) {
                    res.setErrorMessage(ApiInfo.INPUT_FILE_EMPTY);
                    return JSON.toJSONString(res);
                }

                JSONArray fileInfosJson = JSON.parseArray(fileInfos);

                musicService.updateMusicFile(id, files, fileInfosJson, userService.getUserByRequest(request));

            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

}
