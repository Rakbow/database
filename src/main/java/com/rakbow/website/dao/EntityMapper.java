package com.rakbow.website.dao;

import com.rakbow.website.entity.EntityInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-10-07 8:13
 * @Description:
 */
@Mapper
public interface EntityMapper {

    void updateItemStatus(String entityName, int entityId, int status);

}
