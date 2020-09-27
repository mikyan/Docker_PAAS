package cn.mikyan.paas.domain.entity;

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
 * 用户服务表
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_service")
public class UserServiceEntity extends Model<UserServiceEntity> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 服务名
     */
    private String name;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 横向扩展数量
     */
    private Integer replicas;

    /**
     * 执行命令
     */
    private String command;

    /**
     * 端口
     */
    private String port;

    /**
     * 使用镜像
     */
    private String image;

    /**
     * 环境变量
     */
    private String env;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
