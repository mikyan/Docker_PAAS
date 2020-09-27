package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.domain.entity.UserServiceEntity;
import cn.mikyan.paas.mapper.UserServiceMapper;
import cn.mikyan.paas.service.UserServiceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户服务表 服务实现类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Service
public class UserServiceServiceImpl extends ServiceImpl<UserServiceMapper, UserServiceEntity> implements UserServiceService {

}
