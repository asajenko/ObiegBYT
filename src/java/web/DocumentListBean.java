/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Document;
import entities.Invoice;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jpa.DocumentJpaController;
import jpa.InvoiceJpaController;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = DocumentListBean.BEAN_NAME)
@RequestScoped
public class DocumentListBean {

    public static final String BEAN_NAME = "documentListBean";
    private List<Document> documents = new ArrayList<Document>();
    private DocumentJpaController djc = new DocumentJpaController();

    public List<Document> getDocuments() {
        documents = djc.findDocumentEntities();
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void goToDetails(int id) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = servletContext.getContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/system/details.xhtml?id=" + id);
        } catch (IOException ex) {
            Logger.getLogger(DocumentListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
