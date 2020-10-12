package pl.coderslab.charityApp.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResource {
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String password;
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String password2;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;
}
