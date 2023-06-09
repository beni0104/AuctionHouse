package com.example.application.views.sendemail;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.util.Collections;
import com.example.application.data.services.SpringEmailService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import javax.mail.MessagingException;




//This is only for testing the mail sender -------------------------------------------



@PageTitle("Send mail")
@Route(value = "mail", layout = MainLayout.class)
@AnonymousAllowed

public class SendEmailView extends VerticalLayout {
    public SendEmailView() {

        addClassName("mail-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // the text field where users will specify their email address
        TextField textField = new TextField("Your email:");
        TextField textField2 = new TextField("Your content:");

        // a button with a click listener that sends the email
        Button button = new Button("Send me the PDF", e -> {
            try {
                SpringEmailService.send("robertvuia83@gmail.com", Collections.singleton("robert.vuia03@e-uvt.ro"), "test", "mesaj de test");
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        // a layout containing the previous components
        VerticalLayout layout = new VerticalLayout(textField, textField2, button);
        layout.setMargin(true);
        layout.setSpacing(true);

        add(layout); // sets the content for this UI
    }
}
