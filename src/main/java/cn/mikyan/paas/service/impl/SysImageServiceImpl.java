package cn.mikyan.paas.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableSet;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerRequestException;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.ImageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.mikyan.paas.constant.enums.ImageTypeEnum;
import cn.mikyan.paas.constant.enums.ResultEnum;
import cn.mikyan.paas.domain.dto.SysImageDTO;
import cn.mikyan.paas.domain.entity.SysImageEntity;
import cn.mikyan.paas.domain.vo.ResultVO;
import cn.mikyan.paas.mapper.SysImageMapper;
import cn.mikyan.paas.service.SysImageService;
import cn.mikyan.paas.utils.CollectionUtils;
import cn.mikyan.paas.utils.HttpClientUtils;
import cn.mikyan.paas.utils.JsonUtils;
import cn.mikyan.paas.utils.ResultVOUtils;
import cn.mikyan.paas.utils.StringUtils;
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
public class SysImageServiceImpl extends ServiceImpl<SysImageMapper, SysImageEntity> implements SysImageService {

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private SysImageMapper sysImageMapper;

    @Value("${docker.server.url}")
    private String serverUrl;

    @Value("${redis.local-image.key}")
    private String key;
    private final String ID_PREFIX = "ID:";
    private final String FULL_NAME_PREFIX = "FULL_NAME:";

    @Override
    public boolean saveImage(String fullName) {
        // 如果数据已存在，不再保存
        if(getByFullName(fullName) != null) {
            return true;
        }

        try {
            List<Image> list = dockerClient.listImages(DockerClient.ListImagesParam.byName(fullName));
            Image image = CollectionUtils.getListFirst(list);

            SysImageEntity sysImage = imageToSysImage(image, image.repoTags().get(0));
            // 插入数据
            sysImageMapper.insert(sysImage);
            return true;
        } catch (Exception e) {
            log.error("保存镜像数据错误，错误位置：SysImageServiceImpl.saveImage()", e);
            return false;
        }
    }

    /**
     * 拆分ImageId，去掉头部，如：
     * sha256:e38bc07ac18ee64e6d59cf2eafcdddf9cec2364dfe129fe0af75f1b0194e0c96
     * -> e38bc07ac18ee64e6d59cf2eafcdddf9cec2364dfe129fe0af75f1b0194e0c96
     * @author jitwxs
     * @since 2018/7/4 9:44
     */
    private String splitImageId(String imageId) {
        String[] splits = imageId.split(":");
        if(splits.length == 1) {
            return imageId;
        }

        return splits[1];
    }

    /**
     * 拆分repoTage
     * 包含：fullName、tag、repo、type、name
     *      当type = LOCAL_USER_IMAGE时，包含userId
     * @author jitwxs
     * @since 2018/7/4 8:33
     */
    private Map<String, Object> splitRepoTag(String repoTag) {
        Map<String, Object> map = new HashMap<>(16);
        boolean flag = true;
        //设置tag
        int tagIndex = repoTag.lastIndexOf(":");
        String tag = repoTag.substring(tagIndex+1);

        map.put("fullName", repoTag);
        map.put("tag", tag);

        String tagHead = repoTag.substring(0, tagIndex);
        String[] names = tagHead.split("/");

        if(names.length == 1) {
            // 如果包含1个部分，代表来自官方的Image，例如nginx
            map.put("repo", "library");
            map.put("name", names[0]);
            map.put("type", ImageTypeEnum.LOCAL_PUBLIC_IMAGE.getCode());
        } else if(names.length == 2) {
            // 如果包含2个部分，代表来自指定的Image，例如：portainer/portainer
            map.put("repo", names[0]);
            map.put("name", names[1]);
            map.put("type", ImageTypeEnum.LOCAL_PUBLIC_IMAGE.getCode());
        } else if(names.length == 3) {
            // 如果包含3个部分，代表来自用户上传的Image，例如：local/jitwxs/hello-world
            map.put("repo", names[0]);
            map.put("type", ImageTypeEnum.LOCAL_USER_IMAGE.getCode());
            map.put("userId", names[1]);
            map.put("name", names[2]);
        } else {
            // 其他情况异常，形如：local/jitwxs/portainer/portainer:latest
            flag = false;
        }

        // 状态
        map.put("status", flag);

        return map;
    }

    @Override
    public ResultVO listExportPorts(String imageId, String userId) {
        SysImageEntity sysImage = getById(imageId);
        if(sysImage == null) {
            return ResultVOUtils.error(ResultEnum.IMAGE_NOT_EXIST);
        }

        // 获取端口号
        try {
            ImageInfo info = dockerClient.inspectImage(sysImage.getFullName());
            System.out.println(info);
            // 形如：["80/tcp"]
            ImmutableSet<String> exposedPorts = info.containerConfig().exposedPorts();
            System.out.println(exposedPorts);
            Set<String> res = new HashSet<>();

            // 取出端口号信息
            if(exposedPorts != null && exposedPorts.size() > 0) {
                exposedPorts.forEach(s -> {
                    res.add(s.split("/")[0]);
                });
            }

            return ResultVOUtils.success(new ArrayList<>(res));
        } catch (DockerRequestException requestException){
            return ResultVOUtils.error(ResultEnum.DOCKER_EXCEPTION.getCode(), HttpClientUtils.getErrorMessage(requestException.getMessage()));
        }catch (Exception e) {
            log.error("获取镜像暴露端口错误，出错位置：SysImageServiceImpl.listExportPorts()", e);
            return null;
        }
    }

    /**
     * dockerClient.Image --> entity.SysImage
     * 注：hasOpen、createDate、updateDate属性无法计算出，使用默认值
     * @author jitwxs
     * @since 2018/7/3 16:53
     */
    private SysImageEntity imageToSysImage(Image image, String repoTag) {
        SysImageEntity sysImage = new SysImageEntity();
        // 设置ImageId
        sysImage.setImageId(splitImageId(image.id()));

        // 获取repoTag
        Map<String, Object> map = splitRepoTag(repoTag);

        // 判断状态
        if(!(Boolean)map.get("status")) {
            return null;
        }

        // 设置完整名
        sysImage.setFullName((String)map.get("fullName"));
        // 设置Tag
        sysImage.setTag((String)map.get("tag"));
        // 设置Repo
        sysImage.setRepo((String)map.get("repo"));
        // 设置name
        sysImage.setName((String)map.get("name"));
        // 设置type
        Integer type = (Integer)map.get("type");
        sysImage.setType(type);
        // 设置CMD
        try {
            ImageInfo info = dockerClient.inspectImage(repoTag);
            sysImage.setCmd(JsonUtils.objectToJson(info.containerConfig().cmd()));
        } catch (Exception e) {
            log.error("获取镜像信息错误，错误位置：SysImageServiceImpl.imageToSysImage()", e);
        }

        // 设置大小
        sysImage.setSize(image.size());
        // 设置虚拟大小
        sysImage.setVirtualSize(image.virtualSize());
        // 设置Label
        sysImage.setLabels(JsonUtils.mapToJson(image.labels()));
        // 设置父节点
        sysImage.setParentId(image.parentId());
        sysImage.setCreateDate(new Date());

        return sysImage;
    }

    @Override
    public Page<SysImageDTO> listLocalPublicImage(String name, Page<SysImageDTO> page) {
        return page.setRecords(sysImageMapper.listPublicImage(page, name));
    }

    @Override
    public ResultVO cleanImage() {
        List<SysImageEntity> images =  sysImageMapper.selectList(new QueryWrapper<SysImageEntity>().eq("name", "<none>"));
        int success = 0, error = 0;
        try {
            for(SysImageEntity image : images) {
                dockerClient.removeImage(image.getImageId());
                success++;
            }
        } catch (Exception e) {
            log.error("清理镜像出现异常，异常位置：SysImageServiceImpl.cleanImage()", e);
            error++;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("success", success);
        map.put("error", error);
        return ResultVOUtils.success(map);
    }

    @Override
    public SysImageEntity getById(String id) {
        String field = ID_PREFIX + id;

        try {
            String json = jedisClient.hget(key, field);
            if(StringUtils.isNotBlank(json)) {
                return JsonUtils.jsonToObject(json, SysImageEntity.class);
            }
        } catch (Exception e) {
            log.error("缓存读取异常，异常位置：SysImageServiceImpl.getById()", e);
        }

        SysImageEntity image = sysImageMapper.selectById(id);
        if(image == null) {
            return null;
        }

        try {
            String json = JsonUtils.objectToJson(image);
            jedisClient.hset(key, field, json);
        } catch (Exception e) {
            log.error("缓存存储异常，异常位置：SysImageServiceImpl.getById()", e);
        }

        return image;
    }

    @Override
    public SysImageEntity getByFullName(String fullName) {
        String field = FULL_NAME_PREFIX + fullName;

        try {
            String json = jedisClient.hget(key, field);
            if(StringUtils.isNotBlank(json)) {
                return JsonUtils.jsonToObject(json, SysImageEntity.class);
            }
        } catch (Exception e) {
            log.error("缓存读取异常，异常位置：SysImageServiceImpl.getByFullName()", e);
        }

        List<SysImageEntity> images = sysImageMapper.selectList(new QueryWrapper<SysImageEntity>().eq("full_name", fullName));
        SysImageEntity image = CollectionUtils.getListFirst(images);
        if(image == null) {
            return null;
        }

        try {
            String json = JsonUtils.objectToJson(image);
            jedisClient.hset(key, field, json);
        } catch (Exception e) {
            log.error("缓存存储异常，异常位置：SysImageServiceImpl.getByFullName()", e);
        }

        return image;
    }

    /**
     * 查询镜像详细信息
     * @author hf
     * @since 2018/6/28 16:15
     */
    @Override
    public ResultVO inspectImage(String id, String userId) {
        // 1、校验参数
        if(StringUtils.isBlank(id)) {
            return ResultVOUtils.error(ResultEnum.PARAM_ERROR);
        }

        // 2、查询数据库
        SysImageEntity image = getById(id);
        if(image == null) {
            return ResultVOUtils.error(ResultEnum.IMAGE_EXCEPTION);
        }

        // 4、查询信息
        try {
            String fullName = image.getFullName();

            return ResultVOUtils.success(dockerClient.inspectImage(fullName));
        } catch (DockerRequestException requestException){
            return ResultVOUtils.error(ResultEnum.DOCKER_EXCEPTION.getCode(),
                    HttpClientUtils.getErrorMessage(requestException.getMessage()));
        }catch (Exception e) {
            //log.error("Docker查询详情异常，错误位置：{}，错误栈：{}",
            //        "SysImageServiceImpl.inspectImage", HttpClientUtils.getStackTraceAsString(e));
            return ResultVOUtils.error(ResultEnum.INSPECT_ERROR);
        }

    }
}
