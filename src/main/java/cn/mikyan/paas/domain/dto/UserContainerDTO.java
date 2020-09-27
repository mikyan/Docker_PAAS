package cn.mikyan.paas.domain.dto;

import cn.mikyan.paas.domain.entity.UserContainerEntity;
import lombok.Data;

/**
 * 容器DTO
 * @author jitwxs
 * @since 2018/7/9 22:29
 */
@Data
public class UserContainerDTO extends UserContainerEntity {
    /**
     * 所属项目名
     */
    private String projectName;

    /**
     * 状态名
     */
    private String statusName;

    /**
     * 所属用户名
     */
    private String username;
    /**
     * docker主机ip地址
     */
    private String ip;


}
