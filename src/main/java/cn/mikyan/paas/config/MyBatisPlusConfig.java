package cn.mikyan.paas.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBaits Plus 配置类
 * @author jitwxs
 * @since 2018/6/27 13:55
 */
@EnableTransactionManagement
@Configuration
@MapperScan("cn.mikyan.paas.mapper")
public class MyBatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
