package cn.mikyan.paas.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息发送详情表
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_notice_desc")
public class UserNoticeDescEntity extends Model<UserNoticeDescEntity> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 通知ID
     */
    private String noticeId;

    /**
     * 接收者
     */
    private String receive;

    /**
     * 是否已读
     */
    private Integer hasRead;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
