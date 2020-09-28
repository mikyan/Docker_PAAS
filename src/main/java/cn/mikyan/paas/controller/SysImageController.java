package cn.mikyan.paas.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.mikyan.paas.domain.dto.SysImageDTO;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.service.SysImageService;
import cn.mikyan.paas.utils.ResultVOUtils;

/**
 * 镜像Controller
 *
 * @author jitwxs
 * @since 2018/6/28 14:27
 */
@RestController
@RequestMapping("/image")
public class SysImageController {

    @Autowired
    private SysImageService imageService;

    /**
     * 查找镜像
     * @author jitwxs
     * @since 2018/7/3 15:46
     */
    @GetMapping("/list")
    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_SYSTEM')")
    public ResultVO searchLocalImage(String uid,  String name,
                                     @RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size) {
                                         
        Page<SysImageDTO> page = new Page<SysImageDTO>(current, size).addOrder(OrderItem.desc("create_date"));
        return ResultVOUtils.success(imageService.listLocalPublicImage(name, page));
    }

    /**
     * 获取镜像所有暴露接口
     * @author jitwxs
     * @since 2018/7/7 15:50
     */
    @GetMapping("/{id}/exportPort")
    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_SYSTEM')")
    public ResultVO listExportPort(String uid, @PathVariable String id) {
        return imageService.listExportPorts(id, uid);
    }

    @GetMapping("/clean")
    //@PreAuthorize("hasRole('ROLE_SYSTEM')")
    public ResultVO cleanImage() {
        return imageService.cleanImage();
    }

}