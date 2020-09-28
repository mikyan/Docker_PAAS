package cn.mikyan.paas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.mikyan.paas.domain.entity.SysLoginEntity;
import cn.mikyan.paas.domain.vo.ResultVO;

/**
 * <p>
 * 登陆表 服务类
 * </p>
 *
 * @author mikyan
 * @since 2018-06-27
 */
public interface SysLoginService extends IService<SysLoginEntity> {
    /**
     * 根据ID获取用户
     *
     * @author jitwxs
     * @since 2018/6/29 16:59
     */
    SysLoginEntity getById(String id);

    /**
     * 根据用户名获取用户
     *
     * @author jitwxs
     * @since 2018/6/27 14:33
     */
    SysLoginEntity getByUsername(String username);

    SysLoginEntity getByUsername_old(String username);

    /**
     * 根据邮件获取用户
     *
     * @author hf
     * @since 2018/6/27 14:33
     */
    SysLoginEntity getByEmail(String email);

    boolean checkPassword(String username, String password);

    /**
     * 保存用户信息至数据库
     *
     * @author hf
     * @since 2018/6/27 14:33
     */
    boolean save(SysLoginEntity sysLogin);

    /**
     * 更新数据库用户信息
     *
     * @author hf
     * @since 2018/6/27 14:33
     */
    int update(SysLoginEntity sysLogin);

    /**
     * 根据用户名删除
     *
     * @since 2018/6/27 14:33
     */
    void deleteByUsername(String username);

    void deleteById(SysLoginEntity login);

    /**
     * 校验注册
     * @author jitwxs
     * @since 2018/7/1 8:43
     */
    ResultVO registerCheck(String username);

    String getRoleName(String userId);
}