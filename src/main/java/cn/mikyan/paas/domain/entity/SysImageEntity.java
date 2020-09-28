package cn.mikyan.paas.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

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

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 镜像ID
     */
    private String imageId;

    /**
     * 镜像完整名
     */
    private String fullName;

    /**
     * 镜像名
     */
    private String name;

    /**
     * TAG
     */
    private String tag;

    /**
     * 所占大小
     */
    private long size;

    /**
     * 镜像类型【1：公共镜像；2：用户镜像】
     */
    private int type;

    /**
     * 如果是用户镜像，指明用户ID
     */
    private String userId;

    /**
     * 如果是用户镜像，是否公开
     */
    private Boolean hasOpen;

    private long virtualSize;

    private String labels;

    private String cmd;

    /**
     * 仓库地址【官方：library】
     */
    private String repo;

    private String parentId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
