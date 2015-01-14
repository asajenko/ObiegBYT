/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Client;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jpa.ClientJpaController;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = "listBean")
@RequestScoped
public class ListBean {

    private List<Client> clients = new ArrayList<Client>();
    private ClientJpaController cjc = new ClientJpaController();

    public List<Client> getClients() {
        clients = cjc.findClientEntities();
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
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
