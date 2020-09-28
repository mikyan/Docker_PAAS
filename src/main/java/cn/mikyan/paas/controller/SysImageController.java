package cn.mikyan.paas.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.mikyan.paas.domain.dto.SysImageDTO;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.service.SysImageService;
import cn.mikyan.paas.utils.ResultVOUtils;

/**
 * 镜像Controller
 */
@RestController
@RequestMapping("/image")
public class SysImageController {

    @Autowired
    private SysImageService imageService;

    /**
     * 查找镜像
     */
    @GetMapping("/list")
    public ResultVO searchLocalImage(String name, @RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size) {
                                         
        Page<SysImageDTO> page = new Page<SysImageDTO>(current, size).addOrder(OrderItem.desc("create_date"));
        return ResultVOUtils.success(imageService.listPublicImage(name, page));
    }

    /**
     * 查询镜像的详细信息
     * 注：只能查询本地镜像
     */
    @GetMapping("/inspect/{id}")
    public ResultVO imageInspect(@PathVariable String id) {
        return imageService.inspectImage(id);
    }

    /**
     * 本地镜像同步
     * 同步本地镜像和数据库信息
     */
    @GetMapping("/sync")
    public ResultVO syncLocalImage() {
        return imageService.sync();
    }

    /**
     * 获取镜像所有暴露接口
     */
    @GetMapping("/{id}/exportPort")
    public ResultVO listExportPort(String uid, @PathVariable String id) {
        return imageService.listExportPorts(id);
    }

    @GetMapping("/clean")
    public ResultVO cleanImage() {
        return imageService.cleanImage();
    }

}