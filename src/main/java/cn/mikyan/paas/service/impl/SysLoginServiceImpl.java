package cn.mikyan.paas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import cn.mikyan.paas.utils.*;
import cn.mikyan.paas.domain.entity.SysLoginEntity;
import cn.mikyan.paas.constant.enums.ResultEnum;
import cn.mikyan.paas.constant.enums.RoleEnum;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.exception.CustomException;
import cn.mikyan.paas.mapper.SysLoginMapper;
import cn.mikyan.paas.utils.StringUtils;
import cn.mikyan.paas.service.SysLoginService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.operator.KeyUnwrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 登陆表 服务实现类
 * </p>
 *
 * @author jitwxs
 * @since 2018-06-27
 */
@Service
@Slf4j
public class SysLoginServiceImpl extends ServiceImpl<SysLoginMapper, SysLoginEntity> implements SysLoginService {
    
    @Autowired
    private SysLoginMapper loginMapper;


    // @Autowired
    // private TemplateEngine templateEngine;

    @Autowired
    private HttpServletRequest request;


    private final String ID_PREFIX = "ID:";
    private final String USERNAME_PREFIX = "NAME:";
    private final String EMAIL_PREFIX = "EMAIL:";


    @Value("${server.addr}")
    private String serverIp;

    // @Override
    // public SysLoginEntity getById(String id) {
    //     String field = ID_PREFIX + id;
    //     try {
    //         String res = jedisClient.hget(key, field);
    //         if (StringUtils.isNotBlank(res)) {
    //             return JsonUtils.jsonToObject(res, SysLoginEntity.class);
    //         }
    //     } catch (Exception e) {
    //         log.error("缓存读取异常，错误位置：SysLoginServiceImpl.getById()");
    //     }

    //     SysLoginEntity login = loginMapper.selectById(id);
    //     // 如果用户不存在，跳过缓存
    //     if (login == null) {
    //         return null;
    //     }

    //     try {
    //         jedisClient.hset(key, field, JsonUtils.objectToJson(login));
    //     } catch (Exception e) {
    //         log.error("缓存存储异常，错误位置：SysLoginServiceImpl.getById()");
    //     }

    //     return login;
    // }

    
    @Override
    public SysLoginEntity getByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        //System.out.println("3333333333333333333333333333333333333333");
        List<SysLoginEntity> list = loginMapper.selectList(new QueryWrapper<SysLoginEntity>().eq("username", username));
        SysLoginEntity first = CollectionUtils.getListFirst(list);

        // 如果用户不存在，跳过缓存
        if (first == null) {
            return null;
        }

        //System.out.println("3333333333333333333333333333333333333333");

        return first;
    }

    // /**
    //  * 根据用户名查找
    //  *
    //  * @author jitwxs
    //  * @since 2018/6/29 15:35
    //  */
    // @Override
    // public SysLoginEntity getByUsername_old(String username) {
    //     if (StringUtils.isBlank(username)) {
    //         return null;
    //     }

    //     String field = USERNAME_PREFIX + username;
    //     try {
    //         String res = jedisClient.hget(key, field);
    //         if (StringUtils.isNotBlank(res)) {
    //             return JsonUtils.jsonToObject(res, SysLoginEntity.class);
    //         }
    //     } catch (Exception e) {
    //         log.error("缓存读取异常，错误位置：SysLoginServiceImpl.getByUsername()");
    //     }

    //     List<SysLoginEntity> list = loginMapper.selectList(new EntityWrapper<SysLoginEntity>().eq("username", username));
    //     SysLoginEntity first = CollectionUtils.getListFirst(list);

    //     // 如果用户不存在，跳过缓存
    //     if (first == null) {
    //         return null;
    //     }

    //     try {
    //         jedisClient.hset(key, field, JsonUtils.objectToJson(first));
    //     } catch (Exception e) {
    //         log.error("缓存存储异常，错误位置：SysLoginServiceImpl.getByUsername()");
    //     }

    //     return first;
    // }

    // @Override
    // public SysLoginEntity getByUsername(String username) {
    //     if (StringUtils.isBlank(username)) {
    //         return null;
    //     }

    //     List<SysLoginEntity> list = loginMapper.selectList(new EntityWrapper<SysLoginEntity>().eq("username", username));
    //     SysLoginEntity first = CollectionUtils.getListFirst(list);

    //     // 如果用户不存在，跳过缓存
    //     if (first == null) {
    //         return null;
    //     }

    //     return first;
    // }

    /**
     * 根据邮箱查找
     *
     * @author jitwxs
     * @since 2018/6/29 15:35
     */
    // @Override
    // public SysLoginEntity getByEmail(String email) {
    //     if (StringUtils.isBlank(email)) {
    //         return null;
    //     }

    //     String field = EMAIL_PREFIX + email;
    //     try {
    //         String res = jedisClient.hget(key, field);
    //         if (StringUtils.isNotBlank(res)) {
    //             return JsonUtils.jsonToObject(res, SysLoginEntity.class);
    //         }
    //     } catch (Exception e) {
    //         log.error("缓存读取异常，错误位置：SysLoginServiceImpl.getByEmail()");
    //     }

    //     List<SysLoginEntity> list = loginMapper.selectList(new EntityWrapper<SysLoginEntity>().eq("email", email));
    //     SysLoginEntity first = CollectionUtils.getListFirst(list);

    //     // 如果用户不存在，跳过缓存
    //     if (first == null) {
    //         return null;
    //     }

    //     try {
    //         jedisClient.hset(key, field, JsonUtils.objectToJson(first));
    //     } catch (Exception e) {
    //         log.error("缓存存储异常，错误位置：SysLoginServiceImpl.getByEmail()");
    //     }

    //     return first;
    // }

    /**
     * 验证密码
     *
     * @author jitwxs
     * @since 2018/6/29 15:38
     */
    @Override
    public boolean checkPassword(String username, String password) {
        SysLoginEntity login = getByUsername(username);
        if (login == null) {
            return false;
        }
        //ln(login.getPassword());
        //System.out.println(PasswordUtils.encryptPassword(password));

        return PasswordUtils.validatePassword(password, login.getPassword());
        //return new BCryptPasswordEncoder().matches(password, login.getPassword());
    }

    @Override
    public boolean save(SysLoginEntity sysLoginEntity) {
        // 加密密码
        if(StringUtils.isNotBlank(sysLoginEntity.getPassword())) {
            
            sysLoginEntity.setPassword(PasswordUtils.encryptPassword(sysLoginEntity.getPassword()));
        }
        // 用户角色默认为User
        sysLoginEntity.setRoleId(RoleEnum.ROLE_USER.getCode());
        Integer i = loginMapper.insert(sysLoginEntity);
        //System.out.println("========================");
        return i == 1;
    }

    // @Override
    // public int update(SysLoginEntity sysLoginEntity) {
    //     int i = loginMapper.updateById(sysLoginEntity);
    //     cleanLoginCache(sysLoginEntity);
    //     return i;
    // }

    // @Override
    // public void deleteByUsername(String username) {
    //     List<SysLoginEntity> list = loginMapper.selectList(new EntityWrapper<SysLoginEntity>().eq("username", username));
    //     SysLoginEntity first = CollectionUtils.getListFirst(list);
    //     if (first != null) {
    //         loginMapper.delete(new EntityWrapper<SysLoginEntity>().eq("username", username));
    //         // 清理缓存
    //         cleanLoginCache(first);
    //     }
    // }

    // @Override
    // public void deleteById(SysLoginEntity login) {
    //     loginMapper.deleteById(login);
    //     // 清理缓存
    //     cleanLoginCache(login);
    // }



    @Override
    public ResultVO registerCheck(String username) {
        if(StringUtils.isBlank(username)) {
            return ResultVOUtils.error(ResultEnum.PARAM_ERROR);
        }
        if(getByUsername(username) != null) {
            return ResultVOUtils.error(ResultEnum.REGISTER_USERNAME_ERROR);
        }

        return ResultVOUtils.success();
    }

    @Override
    public String getRoleName(String userId) {
        SysLoginEntity sysLoginEntity = getById(userId);
        return RoleEnum.getMessage(sysLoginEntity.getRoleId());
    }

    @Override
    public SysLoginEntity getById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SysLoginEntity getByUsername_old(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SysLoginEntity getByEmail(String email) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(SysLoginEntity sysLogin) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteByUsername(String username) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteById(SysLoginEntity login) {
        // TODO Auto-generated method stub

    }
}