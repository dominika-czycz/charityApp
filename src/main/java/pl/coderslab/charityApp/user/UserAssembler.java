package pl.coderslab.charityApp.user;

import org.springframework.stereotype.Component;

@Component
public class UserAssembler {
    public UserResource toResource(User user) {
        return UserResource.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.getEnabled())
                .password(user.getPassword()).build();
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


}
