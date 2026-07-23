package com.utn.golmundial.publicfrontend.bean;

import com.utn.golmundial.publicfrontend.model.User;
import com.utn.golmundial.publicfrontend.services.AuthService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;
    private User authenticatedUser;
    private boolean guest = false;

    @Inject
    private AuthService authService;

    public String login() {
        User user = authService.authenticate(username, password);

        if (user == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Credenciales no válidas", "El nombre de usuario o la contraseña son incorrectos"));
            return null;
        }

        this.authenticatedUser = user;
        this.guest = false;
        return "/home.xhtml?faces-redirect=true";
    }

    // RF26: guest access, browsing only, no predictions.
    public String enterAsGuest() {
        this.authenticatedUser = null;
        this.guest = true;
        return "/home.xhtml?faces-redirect=true";
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User getAuthenticatedUser() { return authenticatedUser; }
    public boolean isGuest() { return guest; }
    public boolean isAuthenticated() { return authenticatedUser != null; }
}