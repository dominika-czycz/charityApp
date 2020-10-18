package pl.coderslab.charityApp.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.charityApp.user.validation.constraint.SamePasswords;
import pl.coderslab.charityApp.user.validation.constraint.UniqueEmailForUpdate;
import pl.coderslab.charityApp.user.validation.group.ChangePassword;
import pl.coderslab.charityApp.user.validation.group.PreCheckedUpdating;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor

@SamePasswords(groups = ChangePassword.class)
@UniqueEmailForUpdate(groups = PreCheckedUpdating.class)
public class ToUpdateUserResource implements UserResource{
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    private String password;
    @Size(max = 255)
    private String password2;

    @NotBlank
    @Email
    private String email;
    private Boolean enabled;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
