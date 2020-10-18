package pl.coderslab.charityApp.user.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.charityApp.user.validation.constraint.SamePasswords;
import pl.coderslab.charityApp.user.validation.constraint.UniqueEmailForUpdate;
import pl.coderslab.charityApp.user.validation.group.ChangePassword;
import pl.coderslab.charityApp.user.validation.group.PreCheckedUpdating;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor

@SamePasswords(groups = ChangePassword.class)
@UniqueEmailForUpdate(groups = PreCheckedUpdating.class)
public class ToChangePasswordUserResource implements UserResource {
    private Long id;
    private String email;
    @NotNull
    private String uuid;
    @Size(max = 255)
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    private String password;
    @Size(max = 255)
    private String password2;
    private String firstName;
    private String lastName;
    private Boolean enabled;

}
