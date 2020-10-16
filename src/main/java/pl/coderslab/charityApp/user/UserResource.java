package pl.coderslab.charityApp.user;

public interface UserResource {
    Long getId();

    String getPassword();

    String getPassword2();

    String getFirstName();

    String getLastName();

    String getEmail();

    Boolean getEnabled();
}
