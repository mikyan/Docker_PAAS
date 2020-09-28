package cn.mikyan.paas.mapper;

import cn.mikyan.paas.domain.entity.UserContainerEntity;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户容器表 Mapper 接口
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@Mapper
public interface UserContainerMapper extends BaseMapper<UserContainerEntity> {
    /**
     * 获取某一用户所有容器
     * @param name 容器名
     * @author jitwxs
     * @since 2018/7/9 11:24
     */
    List<UserContainerEntity> listContainerByUserIdAndNameAndStatus(Page<UserContainerEntity> page, @Param("userId") String userId, @Param("name") String name, @Param("status") Integer status);

    /**
     * 判断容器是否属于指定用户
     * @author jitwxs
     * @since 2018/7/5 11:44
     */
    Boolean hasBelongSb(@Param("containerId") String containerId, @Param("userId") String userId);

    /**
     * 统计用户容器数目
     * @param status 容器状态，可选
     * @author jitwxs
     * @since 2018/7/10 0:16
     */
    Integer countByUserId(@Param("userId") String userId, @Param("status") Integer status);

    /**
     * 设置容器所属项目为NULL
     * @author jitwxs
     * @since 2018/7/12 14:47
     */
    Integer cleanProjectId(String projectId);
}
