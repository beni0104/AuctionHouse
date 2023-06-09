package com.example.application.views.registrationform;

import com.example.application.data.entity.Users;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

public class RegistrationFormBinder {
    private RegistrationForm registrationForm;


    private boolean enablePasswordValidation;


    public void addBindingAndValidation() {
        BeanValidationBinder<Users> binder = new BeanValidationBinder<>(Users.class);
        binder.bindInstanceFields(registrationForm);


        binder.forField(registrationForm.getPasswordField())
                .withValidator(this::passwordValidator).bind("password");


        registrationForm.getPasswordConfirmField().addValueChangeListener(e -> {

            enablePasswordValidation = true;

            binder.validate();
        });


        binder.setStatusLabel(registrationForm.getErrorMessageField());


        registrationForm.getSubmitButton().addClickListener(event -> {
            try {
                Users userBean = new Users();
                binder.writeBean(userBean);
                showSuccess(userBean);
            } catch (ValidationException exception) {

            }
        });
    }

    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {

        if (pass1 == null || pass1.length() < 4) {
            return ValidationResult.error("Password should be at least 4 characters long");
        }

        if (!enablePasswordValidation) {

            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = registrationForm.getPasswordConfirmField().getValue();

        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }


    private void showSuccess(Users userBean) {
        //registrationService.signup(userBean);
        Notification notification =
                Notification.show("Data saved, welcome " + userBean.getFirstName());
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);


    }

}