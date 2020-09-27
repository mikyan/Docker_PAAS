package cn.mikyan.paas.service.impl;

import cn.mikyan.paas.domain.entity.SysImageEntity;
import cn.mikyan.paas.mapper.SysImageMapper;
import cn.mikyan.paas.service.SysImageService;
import cn.mikyan.paas.utils.JsonUtils;
import cn.mikyan.paas.utils.StringUtils;
import cn.mikyan.paas.utils.jedis.JedisClient;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private SysImageMapper sysImageMapper;

    @Value("${docker.server.url}")
    private String serverUrl;

    @Value("${redis.local-image.key}")
    private String key;
    private final String ID_PREFIX = "ID:";
    private final String FULL_NAME_PREFIX = "FULL_NAME:";

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
}
