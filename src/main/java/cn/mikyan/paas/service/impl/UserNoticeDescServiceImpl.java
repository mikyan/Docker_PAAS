package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.domain.entity.UserNoticeDescEntity;
import cn.mikyan.paas.mapper.UserNoticeDescMapper;
import cn.mikyan.paas.service.UserNoticeDescService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息发送详情表 服务实现类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Service
public class UserNoticeDescServiceImpl extends ServiceImpl<UserNoticeDescMapper, UserNoticeDescEntity> implements UserNoticeDescService {

}
