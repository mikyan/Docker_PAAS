package cn.mikyan.paas.domain.dto;

import cn.mikyan.paas.domain.entity.SysImageEntity;
import lombok.Data;

@Data
public class SysImageDTO extends SysImageEntity {
    /**
     * 所属用户名
     */
    private String username;

}
