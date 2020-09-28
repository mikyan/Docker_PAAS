package cn.mikyan.paas.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.mikyan.paas.constant.enums.ContainerOpEnum;
import cn.mikyan.paas.constant.enums.ContainerStatusEnum;
import cn.mikyan.paas.constant.enums.ResultEnum;
import cn.mikyan.paas.domain.dto.UserContainerDTO;
import cn.mikyan.paas.domain.entity.UserContainerEntity;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.service.UserContainerService;
import cn.mikyan.paas.utils.CollectionUtils;
import cn.mikyan.paas.utils.JsonUtils;
import cn.mikyan.paas.utils.ResultVOUtils;
import cn.mikyan.paas.utils.StringUtils;

/**
 * <p>
 * 用户容器表 前端控制器
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@RestController
@RequestMapping("/userContainer")
public class UserContainerController {

    @Autowired
    private UserContainerService containerService;

    @Value("${docker.server.address}")
    private String dockerAddress;
    @Value("${docker.server.port}")
    private String dockerPort;
    @Value("${server.ip}")
    private String serverIp;
    @Value("${server.port}")
    private String serverPort;

    /**
     * 获取容器
     */

    @GetMapping("/{id}")
    public ResultVO getById(String uid, @PathVariable String id) {
        UserContainerDTO containerDTO = containerService.getById(id);

        return ResultVOUtils.success(containerDTO);
    }

    /**
     * 获取容器状态（包含状态同步）
     */
    @GetMapping("/status/{id}")
    public ResultVO getStatus(@PathVariable String id) {
        ContainerStatusEnum status = containerService.getStatus(id);

        return ResultVOUtils.success(status.getCode());
    }

    /**
     * 获取容器列表
     * 普通用户获取本人容器，系统管理员获取所有容器
     *
     * @param name 容器名
     */
    @GetMapping("/list")
    public ResultVO listContainer(String uid, String name, Integer status,  @RequestParam(defaultValue = "1") Integer current,
                                  @RequestParam(defaultValue = "10") Integer size) {

        Page<UserContainerEntity> page = new Page<UserContainerEntity>(current, size).addOrder(OrderItem.desc("update_date"));
        Page<UserContainerDTO> selectPage = null;

        selectPage = containerService.listContainerByUserId(uid, name, status, page);

        return ResultVOUtils.success(selectPage);
    }

    /**
     * 创建容器【WebSocket】
     *
     * @param imageId        镜像ID 必填
     * @param containerName  容器名 必填
     * @param portMapStr     端口映射，Map<String,String> JSON字符串
     * @param cmdStr         执行命令，如若为空，使用默认的命令，多个分号连接
     * @param envStr         环境变量，多个分号连接
     * @param destinationStr 容器内部目录，多个分号连接
     */
    @PostMapping("/create")
    public ResultVO createContainer(String imageId, String containerName, 
                                    String portMapStr, String cmdStr, String envStr, String destinationStr,
                                    String uid, HttpServletRequest request) {
        // 输入验证
        if (StringUtils.isBlank(imageId, containerName)) {
            return ResultVOUtils.error(ResultEnum.PARAM_ERROR);
        }
        
        System.out.println(portMapStr);
        // 前端传递map字符串
        Map<String, String> portMap;
        try {
            portMap = CollectionUtils.mapJson2map(portMapStr);
        } catch (Exception e) {
            return ResultVOUtils.error(ResultEnum.JSON_ERROR);
        }

        // 前台字符串转换
        String[] cmd = CollectionUtils.str2Array(cmdStr, ";"),
                env = CollectionUtils.str2Array(envStr, ";"),
                destination = CollectionUtils.str2Array(destinationStr, ";");

        // 创建校验
        ResultVO resultVO = containerService.createContainerCheck(uid, imageId, portMap);
        if (ResultEnum.OK.getCode() != resultVO.getCode()) {
            return resultVO;
        } else {
            containerService.createContainerTask(uid, imageId, cmd, portMap, containerName, env, destination, request);
            return ResultVOUtils.success("开始创建容器");
        }
    }
    

    /**
     * 开启容器【WebSocket】
     */
    @GetMapping("/start/{containerId}")
    public ResultVO startContainer(String uid, @PathVariable String containerId) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.START);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.startContainerTask(uid, containerId);
            return ResultVOUtils.success("开始启动容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 暂停容器【WebSocket】
     */
    @GetMapping("/pause/{containerId}")
    public ResultVO pauseContainer(String uid, @PathVariable String containerId) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.PAUSE);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.pauseContainerTask(uid, containerId);
            return ResultVOUtils.success("开始暂停容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 把容器从暂停状态恢复【WebSocket】
     */
    @GetMapping("/continue/{containerId}")
    public ResultVO continueContainer(String uid, @PathVariable String containerId) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.CONTINUE);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.continueContainerTask(uid, containerId);
            return ResultVOUtils.success("开始恢复容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 停止容器【WebSocket】
     */
    @GetMapping("/stop/{containerId}")
    public ResultVO stopContainer(String uid, @PathVariable String containerId) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.STOP);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.stopContainerTask(uid, containerId);
            return ResultVOUtils.success("开始停止容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 强制停止容器【WebSocket】
     */
    @GetMapping("/kill/{containerId}")
    public ResultVO killContainer(String uid, @PathVariable String containerId) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.KILL);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.killContainerTask(uid, containerId);
            return ResultVOUtils.success("开始强制停止容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 重启容器【WebSocket】
     */
    @GetMapping("/restart/{containerId}")
    public ResultVO restartContainer(String uid, @PathVariable String containerId) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.RESTART);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.restartContainerTask(uid, containerId);
            return ResultVOUtils.success("开始重启容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 获取运行容器的内部状态
     */
    @GetMapping("/top/{containerId}")
    public ResultVO topContainer(String uid, @PathVariable String containerId) {
        return containerService.topContainer(uid, containerId);
    }

    /**
     * 删除容器【WebSocket】
     */
    @DeleteMapping("/delete/{containerId}")
    public ResultVO removeContainer(@PathVariable String containerId, String uid, HttpServletRequest request) {
        ResultVO resultVO = containerService.hasAllowOp(uid, containerId, ContainerOpEnum.DELETE);

        if (ResultEnum.OK.getCode() == resultVO.getCode()) {
            containerService.removeContainerTask(uid, containerId, request);
            return ResultVOUtils.success("开始删除容器");
        } else {
            return resultVO;
        }
    }

    /**
     * 调用终端
     *
     * @param containerId 容器ID
     */
    @PostMapping("/terminal")
    public ResultVO showTerminal(String uid, String containerId,
                                 @RequestParam(defaultValue = "false") Boolean cursorBlink,
                                 @RequestParam(defaultValue = "100") Integer cols,
                                 @RequestParam(defaultValue = "50") Integer rows,
                                 @RequestParam(defaultValue = "100") Integer width,
                                 @RequestParam(defaultValue = "50") Integer height) {
        UserContainerEntity container = containerService.getById(containerId);
        if (container == null) {
            return ResultVOUtils.error(ResultEnum.CONTAINER_NOT_FOUND);
        }

        // 只有启动状态容器才能调用Terminal
        ContainerStatusEnum status = containerService.getStatus(containerId);
        if (status != ContainerStatusEnum.RUNNING) {
            return ResultVOUtils.error(ResultEnum.CONTAINER_NOT_RUNNING);
        }

        String url = "ws://" + serverIp + ":" + serverPort + "/ws/container/exec?width=" + width + "&height=" + height +
                "&ip=" + dockerAddress + "&port=" + dockerPort + "&containerId=" + containerId;

        Map<String, Object> map = new HashMap<>(16);
        map.put("cursorBlink", cursorBlink);
        map.put("cols", cols);
        map.put("rows", rows);
        map.put("url", url);
        return ResultVOUtils.success(map);
    }

}

