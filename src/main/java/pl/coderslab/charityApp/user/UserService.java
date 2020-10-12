package pl.coderslab.charityApp.user;

import org.springframework.validation.BindingResult;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

public interface UserService {

    boolean save(UserResource userResource);

    String getPrincipalEmail();

    User getPrincipal() throws NotExistingRecordException;

    boolean isValid(UserResource userResource, BindingResult result);

    boolean arePasswordsTheSame(UserResource userResource, BindingResult result);
}
