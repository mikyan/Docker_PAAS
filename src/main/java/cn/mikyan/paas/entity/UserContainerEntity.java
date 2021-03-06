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
 * 用户容器表
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_container")
public class UserContainerEntity extends Model<UserContainerEntity> {

    private static final long serialVersionUID=1L;

    /**
     * 容器ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 容器名
     */
    private String name;

    /**
     * 执行命令
     */
    private String command;

    /**
     * 端口
     */
    private String port;

    /**
     * 镜像名
     */
    private String image;

    /**
     * 容器状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
