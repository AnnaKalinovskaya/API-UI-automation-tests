package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileModel extends BaseModel{

    private Long id;
    private String username;
    private String password;
    private String name;
    private UserRole role;
    private List<BankAccountModel> accounts;
}
