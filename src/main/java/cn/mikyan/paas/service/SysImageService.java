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

    ResultVO listExportPorts(String imageId, String userId);

    boolean saveImage(String fullName);

    Page<SysImageDTO> listLocalPublicImage(String name, Page<SysImageDTO> page);

    ResultVO cleanImage();

    SysImageEntity getById(String id);

    SysImageEntity getByFullName(String id);

    ResultVO inspectImage(String id, String userId);
}
