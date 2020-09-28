package cn.mikyan.paas.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerRequestException;
import com.spotify.docker.client.exceptions.DockerTimeoutException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ContainerState;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.TopResults;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.mikyan.paas.constant.enums.ContainerOpEnum;
import cn.mikyan.paas.constant.enums.ContainerStatusEnum;
import cn.mikyan.paas.constant.enums.ResultEnum;
import cn.mikyan.paas.convert.UserContainerDTOConvert;
import cn.mikyan.paas.domain.dto.UserContainerDTO;
import cn.mikyan.paas.domain.entity.SysImageEntity;
import cn.mikyan.paas.domain.entity.UserContainerEntity;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.exception.CustomException;
import cn.mikyan.paas.mapper.UserContainerMapper;
import cn.mikyan.paas.service.PortService;
import cn.mikyan.paas.service.SysImageService;
import cn.mikyan.paas.service.UserContainerService;
import cn.mikyan.paas.utils.CollectionUtils;
import cn.mikyan.paas.utils.JsonUtils;
import cn.mikyan.paas.utils.ResultVOUtils;
import cn.mikyan.paas.utils.StringUtils;
import cn.mikyan.paas.utils.jedis.JedisClient;

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

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private PortService portService;

    @Autowired
    private SysImageService sysImageService;

    @Autowired
    private UserContainerMapper userContainerMapper;

    @Autowired
    private UserContainerDTOConvert dtoConvert;

    @Value("${redis.container-name.key}")
    private String key;

    /**
     * 启动状态允许的操作
     */
    private List<ContainerOpEnum> allowOpByRunning = Arrays.asList(ContainerOpEnum.PAUSE, ContainerOpEnum.STOP,
                                                                ContainerOpEnum.KILL, ContainerOpEnum.RESTART);
    /**
     * 暂停状态允许的操作
     */
    private List<ContainerOpEnum> allowOpByPause = Arrays.asList(ContainerOpEnum.CONTINUE, ContainerOpEnum.STOP,
            ContainerOpEnum.KILL, ContainerOpEnum.RESTART);
    /**
     * 停止状态允许的操作
     */
    private List<ContainerOpEnum> allowOpByStop = Arrays.asList(ContainerOpEnum.START, ContainerOpEnum.RESTART,
            ContainerOpEnum.DELETE);

    @Override
    public UserContainerDTO getById(String id) {
        return dtoConvert.convert(userContainerMapper.selectById(id));
    }

    @Override
    public String getName(String id) {
        try {
            String name = jedisClient.hget(key, id);
            if(StringUtils.isNotBlank(name)) {
                return name;
            }
        } catch (Exception e) {
            log.error("缓存读取异常，异常位置：UserContainerServiceImpl.getName()", e);
        }

        String name = getById(id).getName();
        if(StringUtils.isBlank(name)) {
            return name;
        }

        try {
            jedisClient.hset(key,id,name);
        } catch (Exception e) {
            log.error("缓存存储异常，异常位置：UserContainerServiceImpl.getName()", e);
        }

        return name;
    }

    private boolean checkPorts(List<String> exportPorts, Map<String,String> map) {
        // 校验NULL
        if(CollectionUtils.isListEmpty(exportPorts) && map == null) {
            return true;
        }
        if(CollectionUtils.isListNotEmpty(exportPorts) && map == null) {
            return false;
        }
        /*
         * 如果暴露接口非空
         * （1）判断暴露接口是否都设置
         * （2）判断接口是否合法
         * 如果暴露接口空
         * （1）判断接口是否合法
         */
        if(CollectionUtils.isListNotEmpty(exportPorts)) {
            for(String port : exportPorts) {
                //System.out.println(map.get(port));
                if(map.get(port) == null) {
                    return false;
                }
            }
        }
        return hasPortIllegal(map);
    }

    private boolean hasPortIllegal(Map<String,String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Integer value = Integer.parseInt(entry.getValue());

            // 判断数字
            try {
                Integer.parseInt(entry.getKey());
            } catch (Exception e) {
                return false;
            }

            // value允许端口范围：[10000 ~ 65535)
            if(value < 10000 || value > 65535) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ResultVO hasAllowOp(String userId, String containerId, ContainerOpEnum containerOpEnum) {

        // 2、判断状态
        ContainerStatusEnum statusEnum = getStatus(containerId);
        if (statusEnum == ContainerStatusEnum.RUNNING) {
            // 运行：暂停、停止、强制停止、重启
            return allowOpByRunning.contains(containerOpEnum) ? ResultVOUtils.success() : ResultVOUtils.error(ResultEnum.CONTAINER_ALREADY_START);
        } else if (statusEnum == ContainerStatusEnum.PAUSE) {
            // 暂停：恢复、停止、强制停止、重启
            return allowOpByPause.contains(containerOpEnum) ? ResultVOUtils.success() : ResultVOUtils.error(ResultEnum.CONTAINER_ALREADY_PAUSE);
        } else if (statusEnum == ContainerStatusEnum.STOP) {
            // 停止：启动、重启、删除
            return allowOpByStop.contains(containerOpEnum) ? ResultVOUtils.success() : ResultVOUtils.error(ResultEnum.CONTAINER_ALREADY_STOP);
        } else {
            return ResultVOUtils.error(ResultEnum.CONTAINER_STATUS_ERROR);
        }
    }

    @Override
    public ResultVO createContainerCheck(String userId, String imageId, Map<String, String> portMap) {
        // 2、校验Image
        SysImageEntity image = sysImageService.getById(imageId);
        if(image == null) {
            return ResultVOUtils.error(ResultEnum.IMAGE_EXCEPTION);
        }

        // 获取暴露接口
        ResultVO resultVO = sysImageService.listExportPorts(imageId);
        //System.out.println(resultVO);
        if(ResultEnum.OK.getCode() != resultVO.getCode()) {
            return resultVO;
        }
        List<String> exportPorts = (List<String>) resultVO.getData();
        // 3、校验输入的端口
        if(!checkPorts(exportPorts, portMap)) {
            return ResultVOUtils.error(ResultEnum.INPUT_PORT_ERROR);
        }

        return ResultVOUtils.success();
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void createContainerTask(String userId, String imageId, String[] cmd, Map<String, String> portMap,
                                    String containerName, String[] env, String[] destination,
                                    HttpServletRequest request) {                                
        SysImageEntity image = sysImageService.getById(imageId);
        UserContainerEntity uc = new UserContainerEntity();
        HostConfig hostConfig;
        ContainerConfig.Builder builder = ContainerConfig.builder();

        // 1、设置暴露端口
        if(portMap != null) {
            // 宿主机端口与暴露端口绑定
            Set<String> realExportPorts = new HashSet<>();
            Map<String, List<PortBinding>> portBindings = new HashMap<>(16);

            for(Map.Entry<String, String> entry : portMap.entrySet()) {
                String k = entry.getKey();
                int v = Integer.parseInt(entry.getValue());
                realExportPorts.add(k);
                // 捆绑端口
                List<PortBinding> hostPorts = new ArrayList<>();
                // 分配主机端口，如果用户输入端口被占用，随机分配
                Integer hostPort = portService.hasUse(v) ? portService.randomPort() : v;
                hostPorts.add(PortBinding.of("0.0.0.0", hostPort));
                portBindings.put(k, hostPorts);
            }

            uc.setPort(JsonUtils.objectToJson(portBindings));

            builder.exposedPorts(realExportPorts);

            hostConfig = HostConfig.builder()
                    .portBindings(portBindings)
                    .build();
        } else {
            hostConfig = HostConfig.builder().build();
        }

        // 2、构建ContainerConfig
        builder.hostConfig(hostConfig);
        builder.image(image.getFullName());
        builder.tty(true);
        if(CollectionUtils.isNotArrayEmpty(cmd)) {
            builder.cmd(cmd);
            uc.setCommand(Arrays.toString(cmd));
        }
        if(CollectionUtils.isNotArrayEmpty(destination)) {
            builder.volumes(destination);
        }
        if(CollectionUtils.isNotArrayEmpty(env)) {
            builder.env(env);
            uc.setEnv(Arrays.toString(env));
        }
        ContainerConfig containerConfig = builder.build();

        try {
            ContainerCreation creation = dockerClient.createContainer(containerConfig);

            uc.setId(creation.id());
            // 仅存在于数据库，不代表实际容器名
            uc.setName(containerName);
            uc.setUserId(userId);
            uc.setImage(image.getFullName());

            // 3、设置状态
            ContainerStatusEnum status = getStatus(creation.id());
            if(status == null) {
                throw new CustomException(ResultEnum.DOCKER_EXCEPTION.getCode(), "读取容器状态异常");
            }
            uc.setStatus(status.getCode());
            Date date = new Date();
            uc.setCreateDate(date);
            uc.setUpdateDate(date);
            userContainerMapper.insert(uc);

        } catch (DockerRequestException requestException){
            log.error("创建容器出现异常，错误位置：UserContainerServiceImpl.createContainerTask()", requestException);
        } catch (Exception e) {
            log.error("创建容器出现异常，错误位置：UserContainerServiceImpl.createContainerTask()", e);
        }
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void startContainerTask(String userId, String containerId) {
        try {
            dockerClient.startContainer(containerId);
            changeStatus(containerId);
        } catch (Exception e) {
            log.error("开启容器出现异常，异常位置：UserContainerServiceImpl.startContainerTask()",e);
        }
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void stopContainerTask(String userId, String containerId) {
        try {
            dockerClient.stopContainer(containerId, 5);
            changeStatus(containerId);

        } catch (Exception e) {
            log.error("停止容器出现异常，异常位置：UserContainerServiceImpl.stopContainerTask()，错误栈：{}",e);
        }
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void killContainerTask(String userId, String containerId) {
        try {
            dockerClient.killContainer(containerId);
            changeStatus(containerId);
            
        } catch (Exception e) {
            log.error("强制停止容器出现异常，异常位置：UserContainerServiceImpl.killContainerTask()，错误栈：{}",e);
        }
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void removeContainerTask(String userId, String containerId, HttpServletRequest request) {
        try {
            dockerClient.removeContainer(containerId);
            // 删除数据
            userContainerMapper.deleteById(containerId);
            // 清理缓存
            cleanCache(containerId);
        } catch (Exception e) {
            log.error("删除容器出现异常，异常位置：UserContainerServiceImpl.removeContainerTask()", e);
        }
    }

    

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void restartContainerTask(String userId, String containerId) {
        try {
            dockerClient.restartContainer(containerId);
            changeStatus(containerId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("重启容器出现异常，异常位置：UserContainerServiceImpl.restartContainerTask()，错误栈：{}",e);
        }
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void pauseContainerTask(String userId, String containerId) {
        try {
            dockerClient.pauseContainer(containerId);
            changeStatus(containerId);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("暂停容器出现异常，异常位置：UserContainerServiceImpl.pauseContainerTask()，错误栈：{}",e);
        }
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = CustomException.class)
    @Override
    public void continueContainerTask(String userId, String containerId) {
        try {
            dockerClient.unpauseContainer(containerId);
            changeStatus(containerId);
        } catch (Exception e) {
            log.error("继续容器出现异常，异常位置：UserContainerServiceImpl.continueContainerTask()，错误栈：{}",e);
        }
    }

    @Override
    public ResultVO topContainer(String userId, String containerId) {

        // 只有启动状态才能查看进程
        ContainerStatusEnum status = getStatus(containerId);
        if(status != ContainerStatusEnum.RUNNING) {
            return ResultVOUtils.error(ResultEnum.CONTAINER_NOT_RUNNING);
        }

        try {
            TopResults results = dockerClient.topContainer(containerId);
            return ResultVOUtils.success(results);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查看容器进程出现异常，异常位置：UserContainerServiceImpl.continueContainer()",e);
            return ResultVOUtils.error(ResultEnum.DOCKER_EXCEPTION);
        }
    }

    @Override
    public Page<UserContainerDTO> listContainerByUserId(String userId, String name, Integer status, Page<UserContainerEntity> page) {
        List<UserContainerEntity> containers = userContainerMapper.listContainerByUserIdAndNameAndStatus(page, userId, name, status);

        Page<UserContainerDTO> page1 = new Page<>();
        BeanUtils.copyProperties(page, page1);
        return page1.setRecords(dtoConvert.convert(containers));
    }

    @Override
    public ContainerStatusEnum getStatus(String containerId) {
        try {
            ContainerInfo info = dockerClient.inspectContainer(containerId);
            ContainerState state = info.state();

            ContainerStatusEnum statusEnum;
            if(state.running()) {
                if(state.paused()) {
                    statusEnum = ContainerStatusEnum.PAUSE;
                } else {
                    statusEnum = ContainerStatusEnum.RUNNING;
                }
            } else {
                statusEnum =  ContainerStatusEnum.STOP;
            }

            // 如果查询的状态和数据库不一致，更新数据库状态
            UserContainerEntity container = userContainerMapper.selectById(containerId);
            if(container != null) {
                if(statusEnum.getCode() != container.getStatus()) {
                    container.setStatus(statusEnum.getCode());
                    userContainerMapper.updateById(container);
                }
            }

            return statusEnum;
        } catch (ContainerNotFoundException e) {
            return ContainerStatusEnum.REMOVE;
        } catch (DockerTimeoutException te) {
            log.error("获取容器状态超时，异常位置：UserContainerServiceImpl.getStatus()",te);
        } catch (Exception e) {
            log.error("获取容器状态出现异常，异常位置：UserContainerServiceImpl.getStatus()",e);
        }
        return null;
    }

    @Override
    public ResultVO changeStatus(String containerId) {
        ContainerStatusEnum statusEnum = getStatus(containerId);
        if(statusEnum == null) {
            return ResultVOUtils.error(ResultEnum.DOCKER_EXCEPTION);
        }
        if(statusEnum == ContainerStatusEnum.REMOVE) {
            userContainerMapper.deleteById(containerId);
            return ResultVOUtils.success();
        }

        UserContainerEntity container = getById(containerId);
        if(container == null) {
            return ResultVOUtils.error(ResultEnum.PARAM_ERROR);
        }

        if(container.getStatus() != statusEnum.getCode()) {
            container.setStatus(statusEnum.getCode());
            userContainerMapper.updateById(container);
        }

        return ResultVOUtils.success();
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public Map<String, Integer> syncStatus(String userId) {
        // 读取数据库容器列表
        List<UserContainerEntity> containers;

        //为空时同步所有
        if(StringUtils.isBlank(userId)) {
            containers = userContainerMapper.selectList(new QueryWrapper<>());
        } else{
            containers = userContainerMapper.selectList(new QueryWrapper<UserContainerEntity>().eq("user_id",userId));
        }

        int successCount = 0, errorCount = 0;
        for(UserContainerEntity container : containers) {
            ResultVO resultVO = changeStatus(container.getId());

            if(ResultEnum.OK.getCode() == resultVO.getCode()) {
                successCount++;
            } else {
                errorCount++;
            }
        }

        Map<String, Integer> map =new HashMap<>(16);
        map.put("success", successCount);
        map.put("error", errorCount);
        return map;
    }

    private void cleanCache(String id) {
        try {
            jedisClient.hdel(key, id);
        } catch (Exception e) {
            log.error("删除缓存出现异常，异常位置：UserContainerServiceImpl.cleanCache", e);
        }
    }

}
