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
 * 
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("persistent_logins")
public class PersistentLoginsEntity extends Model<PersistentLoginsEntity> {

    private static final long serialVersionUID=1L;

    private String username;

    @TableId(value = "series", type = IdType.ASSIGN_ID)
    private String series;

    private String token;

    private LocalDateTime lastUsed;


    @Override
    protected Serializable pkVal() {
        return this.series;
    }

}
