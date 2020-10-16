package pl.coderslab.charityApp.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.charityApp.user.validation.constraint.SamePasswords;
import pl.coderslab.charityApp.user.validation.constraint.UniqueEmail;
import pl.coderslab.charityApp.user.validation.group.PreChecked;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor

@SamePasswords
public class OrdinaryUserResource implements UserResource {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    private String lastName;

    @NotBlank
    @Size(max = 255)
    private String password;
    @NotBlank
    @Size(max = 255)
    private String password2;

    @NotBlank
    @Email
    @UniqueEmail(groups = PreChecked.class)
    private String email;
    private Boolean enabled;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
