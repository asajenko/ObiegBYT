/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Groups;
import entities.TransactionStates;
import entities.Users;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import jpa.TransactionStatesJpaController;
import jpa.UsersJpaController;

/**
 *
 * @author pzielins
 */
@ManagedBean(name = SessionBean.BEAN_NAME)
@SessionScoped
public class SessionBean implements Serializable {

    public static final String BEAN_NAME = "sessionBean";
    private Users zalogowany = null;
    private List<TransactionStates> transactionStates = null;
    private TransactionStatesJpaController tsjc = new TransactionStatesJpaController();
    private Boolean isAdmin = null;

    public Boolean getIsAdmin() {
        if (isAdmin == null) {
            isAdmin = false;
            for (Groups group : getZalogowany().getGroupsCollection()) {
                if (group.getGroupName().equalsIgnoreCase("ADMIN")) {
                    isAdmin = true;
                    return isAdmin;
                }
            }
        }
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<TransactionStates> getTransactionStates() {
        if (transactionStates == null) {
            transactionStates = new ArrayList<TransactionStates>();
            transactionStates = tsjc.findTransactionStatesEntities();
        }
        return transactionStates;
    }

    public void setTransactionStates(List<TransactionStates> transactionStates) {
        this.transactionStates = transactionStates;
    }

    public Users getZalogowany() {
        if (zalogowany == null) {
            zalogowany = new UsersJpaController().findUserByLogin(getLoggedIn());
        }
        return zalogowany;
    }

    public void setZalogowany(Users zalogowany) {
        this.zalogowany = zalogowany;
    }

    /**
     * Creates a new instance of IndexBean
     */
    public SessionBean() {
    }

    private String getLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }
}
