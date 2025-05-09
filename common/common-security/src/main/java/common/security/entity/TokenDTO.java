package common.security.entity;

import lombok.Data;

import java.util.List;

/**
 * @author abing
 * @created 2025/5/8 17:10
 */

@Data
public class TokenDTO {
    private String id;
    private String refreshToken;
    private String accessToken;
    private String ip;
}
