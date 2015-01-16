/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author pzielins
 */
@ManagedBean(name = "loginBackup")
@RequestScoped
public class LoginBackup implements Serializable {

    /**
     * Creates a new instance of LoginBackup
     */
    public LoginBackup() {
    }

    public void logout() {
        //Zakoncz sesje
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) ec.getRequest();
        request.getSession(false).invalidate();

        //Przenies do strony wylogowania
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = servletContext.getContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/");
        } catch (IOException ex) {
            Logger.getLogger(LoginBackup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
