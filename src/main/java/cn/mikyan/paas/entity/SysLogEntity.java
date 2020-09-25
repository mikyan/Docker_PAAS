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
@TableName("sys_log")
public class SysLogEntity extends Model<SysLogEntity> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 请求Url
     */
    private String action;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 访问IP
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 请求参数
     */
    private String param;

    /**
     * 异常
     */
    private String exception;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
