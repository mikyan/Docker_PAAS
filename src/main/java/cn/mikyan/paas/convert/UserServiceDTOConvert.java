package cn.mikyan.paas.convert;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.mikyan.paas.domain.dto.UserServiceDTO;
import cn.mikyan.paas.domain.entity.UserServiceEntity;

/**
 * UserService --> UserServiceDTO
 * 
 * @author hf
 * @since 2018/7/13 09:10
 */
@Component
public class UserServiceDTOConvert {

    @Value("${docker.swarm.manager.address}")
    private String serverIp;

    public UserServiceDTO convert(UserServiceEntity userService) {
        if (userService == null) {
            return null;
        }

        UserServiceDTO dto = new UserServiceDTO();
        BeanUtils.copyProperties(userService, dto);

        dto.setIp(serverIp);

        return dto;
    }

    public List<UserServiceDTO> convert(List<UserServiceEntity> userServices) {
        return userServices.stream().map(this::convert).collect(Collectors.toList());
    }

    public Page<UserServiceDTO> convert(Page<UserServiceEntity> page) {
        List<UserServiceEntity> userServices = page.getRecords();
        List<UserServiceDTO> userServiceDTOS = convert(userServices);

        Page<UserServiceDTO> page1 = new Page<>();
        BeanUtils.copyProperties(page, page1);
        page1.setRecords(userServiceDTOS);

        return page1;
    }
}