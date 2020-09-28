package cn.mikyan.paas.mapper;

import cn.mikyan.paas.domain.dto.SysImageDTO;
import cn.mikyan.paas.domain.entity.SysImageEntity;

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
public interface SysImageMapper extends BaseMapper<SysImageEntity> {
    /**
     * 获取公共镜像
     * @author jitwxs
     * @since 2018/6/28 16:19
     */
    List<SysImageDTO> listPublicImage(Page<SysImageDTO> page, @Param("name") String name);
}
