package common.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author abing
 * @created 2025/5/20 13:50
 * nacos动态配置
 */

@Data
@Component
@RefreshScope
public class CommonConfig {

    @Value("${aes-key}")
    private String aesKey;

    @Value("${iv-key}")
    private String ivKey;

    @Value("${pattern}")
    private String pattern;

    @Value("${hmac-key}")
    private String hmacKey;
}