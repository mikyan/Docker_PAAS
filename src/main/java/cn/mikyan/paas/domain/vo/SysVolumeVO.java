package cn.mikyan.paas.domain.vo;

import com.spotify.docker.client.messages.Volume;
import cn.mikyan.paas.domain.entity.SysVolumeEntity;
import lombok.Data;

/**
 *
 * @author jitwxs
 * @since 2018/7/21 22:19
 */
@Data
public class SysVolumeVO extends SysVolumeEntity {
    private String typeName;

    private String objName;
    
    private Volume volume;
    
}
