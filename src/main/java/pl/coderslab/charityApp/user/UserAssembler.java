package pl.coderslab.charityApp.user;

import org.springframework.stereotype.Component;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;
import pl.coderslab.charityApp.user.resources.UserResource;

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

    public ToUpdateUserResource toUpdatedResource(User user) {
        return ToUpdateUserResource.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.getEnabled())
                .build();
    }

    public ToChangePasswordUserResource toChangePasswordUserResource(User user) {
        return ToChangePasswordUserResource.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .password(user.getPassword())
                .password2(user.getPassword())
                .build();
    }
}

