/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Client;
import entities.Groups;
import entities.Users;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jpa.ClientJpaController;
import jpa.GroupsJpaController;
import jpa.exceptions.RollbackFailureException;
import javax.faces.application.FacesMessage;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = "listBean")
@RequestScoped
public class ListBean {

    @ManagedProperty(value = "#{" + ApplicationBackup.BEAN_NAME + "}")
    private ApplicationBackup appBean;
    private List<Client> clients = new ArrayList<Client>();
    private ClientJpaController cjc = new ClientJpaController();
    private Client newClient = new Client();

    public ApplicationBackup getAppBean() {
        return appBean;
    }

    public void setAppBean(ApplicationBackup appBean) {
        this.appBean = appBean;
    }

    public List<Client> getClients() {
        clients = cjc.findClientEntities();
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public Client getNewClient() {
        return newClient;
    }

    public void setNewClient(Client newClient) {
        this.newClient = newClient;
    }

    public void createClient() {
        try {
            cjc.create(newClient);
            appBean.addFacesMessage("Udało się stworzyć klienta", 0);
        } catch (Exception ex) {
            Logger.getLogger(ListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void goToDetails(int id) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = servletContext.getContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/system/details.xhtml?id=" + id);
        } catch (IOException ex) {
            Logger.getLogger(ListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
