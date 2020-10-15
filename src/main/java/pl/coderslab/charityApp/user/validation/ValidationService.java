package pl.coderslab.charityApp.user.validation;

public interface ValidationService {
    boolean isUniqueEmail(String email);

    boolean isUniqueEmail(String email, Long excludedId);
}
