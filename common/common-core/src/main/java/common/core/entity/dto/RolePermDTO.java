package common.core.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermDTO {
    private String id;
    private String username;
    private List<String> roles;
    private List<String> permissions;
}
