package pl.coderslab.charityApp.user;

import org.springframework.stereotype.Component;

@Component
public class UserAssembler {
    public UserResource toResource(User user) {
        return UserResource.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword()).build();
    }

    public User fromResource(UserResource resource) {
        return User.builder()
                .email(resource.getEmail())
                .firstName(resource.getFirstName())
                .lastName(resource.getLastName())
                .password(resource.getPassword()).build();
    }


}
