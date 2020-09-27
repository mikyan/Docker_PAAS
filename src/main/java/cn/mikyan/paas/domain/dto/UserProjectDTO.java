package cn.mikyan.paas.domain.dto;

import cn.mikyan.paas.domain.entity.UserProjectEntity;
import lombok.Data;

/**
 * 用户项目DTO
 * @author jitwxs
 * @since 2018/7/11 18:19
 */
@Data
public class UserProjectDTO extends UserProjectEntity {
    /**
     * 用户名
     */
    private String username;
}
