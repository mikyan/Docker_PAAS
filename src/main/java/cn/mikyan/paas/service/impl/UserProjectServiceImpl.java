package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.entity.UserProjectEntity;
import cn.mikyan.paas.mapper.UserProjectMapper;
import cn.mikyan.paas.service.UserProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Service
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProjectEntity> implements UserProjectService {

}
