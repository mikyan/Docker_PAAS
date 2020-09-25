package cn.mikyan.paas.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_image")
public class SysImageEntity extends Model<SysImageEntity> {

    private static final long serialVersionUID=1L;

    /**
     * 镜像ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 镜像名
     */
    private String name;

    /**
     * TAG
     */
    private String tag;

    /**
     * 来源仓库
     */
    private Boolean repository;

    /**
     * 所占大小
     */
    private String size;

    /**
     * 如果用户上传镜像，指明用户ID
     */
    private String userId;

    /**
     * 如果用户上传镜像，是否公开
     */
    private Boolean hasOpen;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
