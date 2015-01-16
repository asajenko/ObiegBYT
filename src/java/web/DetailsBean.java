/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Client;
import entities.Note;
import entities.Transaction;
import entities.TransactionStates;
import entities.Users;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import jpa.ClientJpaController;
import jpa.NoteJpaController;
import jpa.TransactionJpaController;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = "detailsBean")
@ViewScoped
public class DetailsBean implements Serializable {

    @ManagedProperty(value = "#{" + ApplicationBackup.BEAN_NAME + "}")
    private ApplicationBackup appBean;
    @ManagedProperty(value = "#{" + SessionBean.BEAN_NAME + "}")
    private SessionBean sessionBean;
    private String filtr;
    private Client selectedClient;
    private ClientJpaController cjc = new ClientJpaController();
    private NoteJpaController njc = new NoteJpaController();
    private TransactionJpaController tjc = new TransactionJpaController();
    private String newContent = "";
    private String newTransactionContent = "";
    private String confirmMessage;

    public DetailsBean() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        filtr = request.getParameter("id");
        if (filtr != null) {
            selectedClient = cjc.findClientByNumber(filtr);
        }
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public ApplicationBackup getAppBean() {
        return appBean;
    }

    public void setAppBean(ApplicationBackup appBean) {
        this.appBean = appBean;
    }

    public String getNewTransactionContent() {
        return newTransactionContent;
    }

    public void setNewTransactionContent(String newTransactionContent) {
        this.newTransactionContent = newTransactionContent;
    }

    public Client getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
    }

    public String getFiltr() {
        return filtr;
    }

    public void setFiltr(String filtr) {
        this.filtr = filtr;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public String getConfirmMessage() {
        if (selectedClient != null) {
            if (selectedClient.getInvoiceList() != null && selectedClient.getInvoiceList().size() > 0) {
                return "Czy na pewno? Klient jest podłączony do faktury istniejącej w systemie.";
            } else if (selectedClient.getInvoiceList() == null || selectedClient.getInvoiceList().isEmpty()) {
                return "Czy na pewno?";
            }
        }
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    /*  public void addNewNote() {
     try {
     Note note = new Note();
     note.setClientId(selectedClient);
     Users user = sessionBean.getZalogowany();
     note.setKonsultant(user.getLastName() + " " + user.getFirstName());
     note.setTres(newContent);
     njc.create(note);
     selectedClient = cjc.findClient(selectedClient.getId());
     newContent = "";
     appBean.addFacesMessage("Zapisano notatke", 0);
     } catch (RollbackFailureException ex) {
     appBean.addFacesMessage("Błąd podczas zapisu notatki", 2);
     Logger.getLogger(DetailsBean.class.getName()).log(Level.SEVERE, null, ex);
     } catch (Exception ex) {
     appBean.addFacesMessage("Błąd podczas zapisu notatki", 2);
     Logger.getLogger(DetailsBean.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     public void addNewTrans() {
     try {
     Transaction transaction = new Transaction();
     transaction.setClientId(selectedClient);
     Users user = sessionBean.getZalogowany();
     transaction.setKonsultant(user.getLastName() + " " + user.getFirstName());
     transaction.setTresc(newTransactionContent);
     for (TransactionStates state : sessionBean.getTransactionStates()) {
     if (state.getState().equalsIgnoreCase("Wprowadzona")) {
     transaction.setStatus(state.getId());
     break;
     }
     }
     tjc.create(transaction);
     selectedClient = cjc.findClient(selectedClient.getId());
     newTransactionContent = "";
     appBean.addFacesMessage("Zapisano transakcje", 0);
     } catch (RollbackFailureException ex) {
     appBean.addFacesMessage("Błąd podczas zapisu transakcji", 2);
     Logger.getLogger(DetailsBean.class.getName()).log(Level.SEVERE, null, ex);
     } catch (Exception ex) {
     appBean.addFacesMessage("Błąd podczas zapisu transakcji", 2);
     Logger.getLogger(DetailsBean.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     */
    public String getStateName(int id) {
        for (TransactionStates state : sessionBean.getTransactionStates()) {
            if (state.getId() == id) {
                return state.getState();
            }
        }
        return null;
    }

    public String deleteClient() {
        try {
            cjc.destroy(selectedClient.getId());
            appBean.addFacesMessage("Udało się usunąć klienta", 0);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ListBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "index";
    }

    public String goToIndex() {
        return "index";
    }

    public void updateCient() {
        try {
            appBean.addFacesMessage("Udało się zapisać nowe dane klienta", 0);
            cjc.edit(selectedClient);
        } catch (Exception ex) {
            Logger.getLogger(ListBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
