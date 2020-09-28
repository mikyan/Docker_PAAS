package cn.mikyan.paas.mapper;

import cn.mikyan.paas.domain.entity.SysVolumeEntity;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Mapper
public interface SysVolumeMapper extends BaseMapper<SysVolumeEntity> {
    
    List<SysVolumeEntity> selectByObjId(@Param("objId") String objId, Page<SysVolumeEntity> page);

    boolean hasUser(String id);

    void deleteByObjId(@Param("objId") String objId);
}
