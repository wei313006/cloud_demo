package common.security.entity;

import lombok.Data;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/16 14:20
 */

@Data
public class AuthUserDTO {
    private String id;
    private String username;
    private String password;
    private String refreshToken;
    private String accessToken;
    private List<String> roles;
    private List<String> permissions;
    private String checkCodeId;
    private String checkCode;
    private String ip;
}
