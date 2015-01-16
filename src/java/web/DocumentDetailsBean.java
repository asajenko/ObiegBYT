/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Document;
import entities.Invoice;
import entities.TransactionStates;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import jpa.DocumentJpaController;
import jpa.InvoiceJpaController;

/**
 *
 * @author My
 */
@ManagedBean(name = "documentDetailsBean")
@ViewScoped
public class DocumentDetailsBean implements Serializable {

    @ManagedProperty(value = "#{" + ApplicationBackup.BEAN_NAME + "}")
    private ApplicationBackup appBean;
    @ManagedProperty(value = "#{" + SessionBean.BEAN_NAME + "}")
    private SessionBean sessionBean;
    private String filtr;
    private Document selectedDocument;
    private DocumentJpaController djc = new DocumentJpaController();
    private String newContent = "";

    public DocumentDetailsBean() {
        selectedDocument = new Document();
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

    public String getFiltr() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        filtr = request.getParameter("id");
        if (filtr != null) {
            selectedDocument = djc.findDocument(Integer.parseInt(filtr));
        }
        return filtr;
    }

    public void setFiltr(String filtr) {
        this.filtr = filtr;
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument() {
        this.selectedDocument = selectedDocument;
    }

    public String getStateName(int id) {
        for (TransactionStates state : sessionBean.getTransactionStates()) {
            if (state.getId() == id) {
                return state.getState();
            }
        }
        return null;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }
    
       public void updateDocument() {
        try {
            djc.edit(selectedDocument);
            appBean.addFacesMessage("Udało się zapisać nowe dane dokumentu", 0);
        } catch (Exception ex) {
            Logger.getLogger(ListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}
