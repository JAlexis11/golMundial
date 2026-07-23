package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.User;
import com.utn.golmundial.publicfrontend.services.AuthService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;

@Named
@ViewScoped
public class RegisterBean implements Serializable {

    private String username;
    private String email;
    private String fullName;
    private String password;
    private String confirmPassword;

    @Inject
    private AuthService authService;

    public String register() {
        if (!password.equals(confirmPassword)) {
            errorMessage("Passwords do not match");
            return null;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPassword(password);

        boolean success = authService.register(newUser);

        if (!success) {
            errorMessage("That username is already taken");
            return null;
        }

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Registration successful!", "You can now log in"));

        return "/login.xhtml?faces-redirect=true";
    }

    private void errorMessage(String text) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, text, null));
    }

    public String getUsername() { 
        return username; 
    }
    public void setUsername(String username) { 
        this.username = username; 
    }
    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) {
        this.email = email; 
    }
    public String getFullName() { 
        return fullName;
    }
    public void setFullName(String fullName) { 
        this.fullName = fullName;
    }
    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
    public String getConfirmPassword() { 
        return confirmPassword; 
    }
    public void setConfirmPassword(String confirmPassword) { 
        this.confirmPassword = confirmPassword;
    }
}