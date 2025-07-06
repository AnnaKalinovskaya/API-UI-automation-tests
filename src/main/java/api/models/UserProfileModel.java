package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileModel that = (UserProfileModel) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, role, accounts);
    }
}
