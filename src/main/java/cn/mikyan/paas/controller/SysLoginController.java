package cn.mikyan.paas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.mikyan.paas.constant.enums.ResultEnum;
import cn.mikyan.paas.domain.entity.SysLoginEntity;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.domain.vo.UserVO;
import cn.mikyan.paas.service.SysLoginService;
import cn.mikyan.paas.utils.ResultVOUtils;
import cn.mikyan.paas.utils.StringUtils;

/**
 * <p>
 * 登陆表 前端控制器
 * </p>
 *
 * @author MIKYAN
 * @since 2020-09-26
 */
@RestController
@RequestMapping("/sysLoginEntity")
public class SysLoginController {

    @Autowired
    private SysLoginService loginService;
    
    @PostMapping("/login")
    public ResultVO login(String username, String password) {
        if(StringUtils.isBlank(username, password)) {
            return ResultVOUtils.error(ResultEnum.PARAM_ERROR);
        }
        //System.out.println("1111111111111111111111111111111111");
        boolean b = loginService.checkPassword(username, password);
        //System.out.println("22222222222222222222222222222222222");
        if(b==false){
            return ResultVOUtils.error(ResultEnum.LOGIN_ERROR);
        }
        SysLoginEntity sysLogin=loginService.getByUsername(username);

        UserVO userVO = new UserVO();
        userVO.setUserId(sysLogin.getId());
        userVO.setRoleId(sysLogin.getRoleId());
        userVO.setUsername(sysLogin.getUsername());

        
        
		return ResultVOUtils.success(userVO);
    }


      /**
     * 用户注册
     * @author hf
     * @since 2018/6/28 9:17
     */
    @PostMapping("/register")
    public ResultVO register(String username, String password) {
        // 0. 判断用户名和密码不能为空
       // 

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ResultVOUtils.error(ResultEnum.NULL_NAME_OR_PASSWORD);
        }

        // 1. 判断用户名是否存在，如果存在则返回用户名已存在，如果不存在则注册
        //System.out.println("========================");

        ResultVO resultVO= loginService.registerCheck(username);
        if(resultVO.getCode() != ResultEnum.OK.getCode()) {
            return resultVO;
        }
       // System.out.println("========================");

        SysLoginEntity sysLoginEntity = new SysLoginEntity(username,password);
        loginService.save(sysLoginEntity);
        //id应该是会自动生成,待测试
        
		return ResultVOUtils.success();
    }

    @GetMapping("/logout")
    public ResultVO logout() {
        
        
		return ResultVOUtils.success();
    }

}

