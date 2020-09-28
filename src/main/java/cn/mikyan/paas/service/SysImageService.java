package cn.mikyan.paas.service;

import cn.mikyan.paas.domain.dto.SysImageDTO;
import cn.mikyan.paas.domain.entity.SysImageEntity;
import cn.mikyan.paas.domain.vo.ResultVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
public interface SysImageService extends IService<SysImageEntity> {

    boolean saveImage(String fullName);

    ResultVO cleanImage();

    ResultVO inspectImage(String id);

    ResultVO listExportPorts(String imageId);

    Page<SysImageDTO> listPublicImage(String name, Page<SysImageDTO> page);

    SysImageEntity getById(String id);

    SysImageEntity getByFullName(String id); 

    ResultVO sync();

    void cleanCache(String id, String fullName);
}
