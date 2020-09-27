package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.domain.entity.ProjectLogEntity;
import cn.mikyan.paas.mapper.ProjectLogMapper;
import cn.mikyan.paas.service.ProjectLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 项目日志表 服务实现类
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Service
public class ProjectLogServiceImpl extends ServiceImpl<ProjectLogMapper, ProjectLogEntity> implements ProjectLogService {

}
