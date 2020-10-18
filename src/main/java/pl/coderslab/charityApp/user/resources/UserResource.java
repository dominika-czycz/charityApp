package pl.coderslab.charityApp.user.resources;

public interface UserResource {
    Long getId();

    String getPassword();

    String getPassword2();

    String getEmail();

    String getFirstName();

    Boolean getEnabled();

    String getLastName();
}
