package pl.coderslab.charityApp.user;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;
import pl.coderslab.charityApp.user.validation.group.ChangePassword;
import pl.coderslab.charityApp.user.validation.group.PreCheckedUpdating;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    void saveUser(@Valid OrdinaryUserResource userResource);

    void saveAdmin(@Valid OrdinaryUserResource userResource);

    String getUuid(Long id) throws NotExistingRecordException;

    String getPrincipalEmail();

    User getPrincipal() throws NotExistingRecordException;

    OrdinaryUserResource getPrincipalResource() throws NotExistingRecordException;

    List<OrdinaryUserResource> findAll();

    OrdinaryUserResource getAdminResourceById(Long id) throws NotExistingRecordException;

    void deleteAdmin(Long id) throws NotExistingRecordException;

    void editAdmin(@Valid ToUpdateUserResource userResource) throws NotExistingRecordException;

    void changePassword(ToChangePasswordUserResource userResource) throws NotExistingRecordException;

    void editUser(@Valid ToUpdateUserResource userResource) throws NotExistingRecordException;

    List<OrdinaryUserResource> findAllAdmins();

    List<OrdinaryUserResource> findAllUsers();

    OrdinaryUserResource getUserResourceById(Long id) throws NotExistingRecordException;

    void deleteUser(Long id) throws NotExistingRecordException;

    void blockUser(Long id) throws NotExistingRecordException;

    ToUpdateUserResource getPrincipalToUpdateResource() throws NotExistingRecordException;

    ToUpdateUserResource getToUpdateUserResourceById(Long id) throws NotExistingRecordException;

    void changePassword(@Valid ToUpdateUserResource user) throws NotExistingRecordException;

    ToUpdateUserResource getToUpdateAdminResourceById(Long id) throws NotExistingRecordException;

    void activate(String uuid) throws NotExistingRecordException;

    ToChangePasswordUserResource getUserToChangePasswordByUuid(String uuid) throws NotExistingRecordException;

    ToChangePasswordUserResource findByEmail(String email) throws NotExistingRecordException;

    void setUuid(ToChangePasswordUserResource userResource) throws NotExistingRecordException;
}
