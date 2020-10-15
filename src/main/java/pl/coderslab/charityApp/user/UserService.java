package pl.coderslab.charityApp.user;

import org.springframework.validation.annotation.Validated;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.validation.group.PreChecked;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    void saveUser(@Valid UserResource userResource);

    void saveAdmin(@Valid UserResource userResource);

    String getPrincipalEmail();

    User getPrincipal() throws NotExistingRecordException;

    UserResource getPrincipalResource() throws NotExistingRecordException;


    List<UserResource> findAll();

    UserResource getAdminResourceById(Long id) throws NotExistingRecordException;

    void deleteAdmin(Long id) throws NotExistingRecordException;

    void editAdmin(@Valid UserResource userResource) throws NotExistingRecordException;

    List<UserResource> findAllAdmins();

    List<UserResource> findAllUsers();

    UserResource getUserResourceById(Long id) throws NotExistingRecordException;

    void editUser(@Valid UserResource user) throws NotExistingRecordException;

    void deleteUser(Long id) throws NotExistingRecordException;

    void blockUser(Long id) throws NotExistingRecordException;
}
