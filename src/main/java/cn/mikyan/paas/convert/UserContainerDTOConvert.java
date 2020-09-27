package cn.mikyan.paas.convert;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.mikyan.paas.constant.enums.ContainerStatusEnum;
import cn.mikyan.paas.domain.dto.UserContainerDTO;
import cn.mikyan.paas.service.SysLoginService;
import cn.mikyan.paas.domain.entity.UserContainerEntity;

/**
 * UserContainer --> UserContainerDTO
 * @author jitwxs
 * @since 2018/7/9 22:32
 */
@Component
public class UserContainerDTOConvert {
    @Autowired
    private SysLoginService sysLoginService;
    @Value("${docker.server.address}")
    private String serverIp;

    public UserContainerDTO convert(UserContainerEntity container) {
        if(container == null) {
            return null;
        }
        UserContainerDTO dto = new UserContainerDTO();
        BeanUtils.copyProperties(container, dto);

        Integer status = container.getStatus();
        if(status != null) {
            dto.setStatusName(ContainerStatusEnum.getMessage(status));
        }

        dto.setIp(serverIp);

        return dto;
    }

    public List<UserContainerDTO> convert(List<UserContainerEntity> containers) {
        return containers.stream().map(this::convert).collect(Collectors.toList());
    }

    public Page<UserContainerDTO> convert(Page<UserContainerEntity> page) {
        List<UserContainerEntity> containers = page.getRecords();
        List<UserContainerDTO> containerDTOS = convert(containers);

        Page<UserContainerDTO> page1 = new Page<>();
        BeanUtils.copyProperties(page, page1);
        page1.setRecords(containerDTOS);

        return page1;
    }
}