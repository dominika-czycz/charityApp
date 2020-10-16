package pl.coderslab.charityApp.user;

import org.springframework.stereotype.Component;

@Component
public class UserAssembler {
    public OrdinaryUserResource toResource(User user) {
        return OrdinaryUserResource.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.getEnabled())
                .password(user.getPassword())
                .password2(user.getPassword())
                .build();
    }

    public User fromResource(UserResource resource) {
        return User.builder()
                .id(resource.getId())
                .email(resource.getEmail())
                .firstName(resource.getFirstName())
                .enabled(resource.getEnabled())
                .lastName(resource.getLastName())
                .password(resource.getPassword()).build();
    }

    public ToUpdateUserResource toUpdatedResource(OrdinaryUserResource ordinary) {
        return ToUpdateUserResource.builder()
                .id(ordinary.getId())
                .email(ordinary.getEmail())
                .firstName(ordinary.getFirstName())
                .lastName(ordinary.getLastName())
                .enabled(ordinary.getEnabled())
                .build();
    }
}
