package cn.mikyan.paas.service;

import cn.mikyan.paas.constant.enums.ContainerStatusEnum;
import cn.mikyan.paas.domain.dto.UserContainerDTO;
import cn.mikyan.paas.domain.entity.UserContainerEntity;
import cn.mikyan.paas.domain.vo.ResultVO;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户容器表 服务类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
public interface UserContainerService extends IService<UserContainerEntity> {
    
    UserContainerDTO getById(String id);

    String getName(String id);

    void createContainerTask(String userId, String imageId, String[] cmd, Map<String, String> portMap,
                             String containerName, String projectId, String[] env, String[]destination,
                             HttpServletRequest request);

    void startContainerTask(String userId, String containerId);
    
    ContainerStatusEnum getStatus(String containerId);

    ResultVO changeStatus(String containerId);

    void stopContainerTask(String userId, String containerId);

    void killContainerTask(String userId, String containerId);

    void removeContainerTask(String userId, String containerId, HttpServletRequest request);

    void restartContainerTask(String userId, String containerId);

    void pauseContainerTask(String userId, String containerId);

    void continueContainerTask(String userId, String containerId);
    
}
