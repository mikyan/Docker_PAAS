package cn.mikyan.paas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.mikyan.paas.constant.enums.ResultEnum;
import cn.mikyan.paas.constant.enums.RoleEnum;
import cn.mikyan.paas.domain.dto.UserProjectDTO;
import cn.mikyan.paas.domain.entity.UserContainerEntity;
import cn.mikyan.paas.domain.entity.UserProjectEntity;
import cn.mikyan.paas.domain.entity.UserServiceEntity;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.exception.CustomException;
import cn.mikyan.paas.mapper.ProjectLogMapper;
import cn.mikyan.paas.mapper.UserContainerMapper;
import cn.mikyan.paas.mapper.UserProjectMapper;
import cn.mikyan.paas.service.ProjectLogService;
import cn.mikyan.paas.service.SysLogService;
import cn.mikyan.paas.service.SysLoginService;
import cn.mikyan.paas.service.UserContainerService;
import cn.mikyan.paas.service.UserProjectService;
import cn.mikyan.paas.service.UserServiceService;
import cn.mikyan.paas.utils.JsonUtils;
import cn.mikyan.paas.utils.ResultVOUtils;
import cn.mikyan.paas.utils.jedis.JedisClient;
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
