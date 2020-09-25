package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.entity.UserContainerEntity;
import cn.mikyan.paas.mapper.UserContainerMapper;
import cn.mikyan.paas.service.UserContainerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户容器表 服务实现类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Service
public class UserContainerServiceImpl extends ServiceImpl<UserContainerMapper, UserContainerEntity> implements UserContainerService {

}
