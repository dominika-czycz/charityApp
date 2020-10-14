package pl.coderslab.charityApp.user;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.List;

public interface UserService {

    boolean save(UserResource userResource);

    @Transactional
    boolean saveAdmin(UserResource userResource);

    String getPrincipalEmail();

    User getPrincipal() throws NotExistingRecordException;

    boolean isValid(UserResource userResource, BindingResult result);

    boolean arePasswordsTheSame(UserResource userResource, BindingResult result);

    UserResource getPrincipalResource() throws NotExistingRecordException;


    List<UserResource> findAll();

    UserResource getAdminResourceById(Long id) throws NotExistingRecordException;

    void deleteAdmin(Long id) throws NotExistingRecordException;

    void editAdmin(UserResource userResource) throws NotExistingRecordException;

    List<UserResource> findAllAdmins();

    List<UserResource> findAllUsers();
}
