package cn.mikyan.paas.domain.dto;

import cn.mikyan.paas.domain.entity.UserServiceEntity;
import lombok.Data;

/**
 * 容器DTO
 * @author hf
 * @since 2018/7/13 09:10
 */
@Data
public class UserServiceDTO extends UserServiceEntity {
    /**
     * 所属项目名
     */
    private String projectName;

    /**
     * 状态名
     */
    private String statusName;

    /**
     * IP
     */
    private String ip;

}