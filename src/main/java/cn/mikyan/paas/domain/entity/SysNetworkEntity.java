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
 * 系统网络表
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_network")
public class SysNetworkEntity extends Model<SysNetworkEntity> {

    private static final long serialVersionUID=1L;

    /**
     * 网络ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 网络名
     */
    private String name;

    /**
     * 网络范围
     */
    private String scope;

    /**
     * 网络驱动
     */
    private String driver;

    /**
     * 是否是内部网络
     */
    private Boolean hasInternal;

    /**
     * 是否开启ipv6
     */
    private Boolean hasIpv6;

    private String labels;

    /**
     * 是否是公开网络
     */
    private Boolean hasPublic;

    /**
     * 如果不是公开网络，所属用户
     */
    private String userId;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
