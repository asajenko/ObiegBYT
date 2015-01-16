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
    private boolean isKsiegowosc;
    private boolean isZarzad;
    private boolean isSekretariat;
    private boolean isUzytkownik;

    public boolean isIsKsiegowosc() {
        if (getZalogowany() != null && getZalogowany().getRola() != null && getZalogowany().getRola().equalsIgnoreCase("Księgowość")) {
            isKsiegowosc = true;
            return true;
        } else {
            isKsiegowosc = false;
            return false;
        }
    }

    public void setIsKsiegowosc(boolean isKsiegowosc) {
        this.isKsiegowosc = isKsiegowosc;
    }

    public boolean isIsZarzad() {
        if (getZalogowany() != null && getZalogowany().getRola() != null && getZalogowany().getRola().equalsIgnoreCase("Zarząd")) {
            isZarzad = true;
            return true;
        } else {
            isZarzad = false;
            return false;
        }
    }

    public void setIsZarzad(boolean isZarzad) {
        this.isZarzad = isZarzad;
    }

    public boolean isIsSekretariat() {
        if (getZalogowany() != null && getZalogowany().getRola() != null && getZalogowany().getRola().equalsIgnoreCase("Sekretariat")) {
            isSekretariat = true;
            return true;
        } else {
            isSekretariat = false;
            return false;
        }
    }

    public void setIsSekretariat(boolean isSekretariat) {
        this.isSekretariat = isSekretariat;
    }

    public boolean isIsUzytkownik() {
        if (getZalogowany() != null && getZalogowany().getRola() != null && getZalogowany().getRola().equalsIgnoreCase("Użytkownik")) {
            isUzytkownik = true;
            return true;
        } else {
            isUzytkownik = false;
            return false;
        }
    }

    public void setIsUzytkownik(boolean isUzytkownik) {
        this.isUzytkownik = isUzytkownik;
    }

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
