package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.domain.entity.UserNoticeEntity;
import cn.mikyan.paas.mapper.UserNoticeMapper;
import cn.mikyan.paas.service.UserNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户通知表 服务实现类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNoticeEntity> implements UserNoticeService {

}
