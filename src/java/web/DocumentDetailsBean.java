/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Document;
import entities.DocumentFile;
import entities.Invoice;
import entities.TransactionStates;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import jpa.DocumentFileJpaController;
import jpa.DocumentJpaController;
import jpa.InvoiceJpaController;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
    private List<DocumentFile> plikiFaktura;
    private String fileToDownload;
    private StreamedContent fileToDownload1;

    public String getFileToDownload() {
        return fileToDownload;
    }

    public void setFileToDownload(String fileToDownload) {
        this.fileToDownload = fileToDownload;
    }

    public List<DocumentFile> getPlikiFaktura() {
        if (selectedDocument != null) {
            plikiFaktura = selectedDocument.getDocumentFileList();
        } else {
            plikiFaktura = new ArrayList<DocumentFile>();
        }
        return plikiFaktura;
    }

    public void setPlikiFaktura(List<DocumentFile> plikiFaktura) {
        this.plikiFaktura = plikiFaktura;
    }

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

    public StreamedContent getFileToDownload1() {
        if (fileToDownload != null) {
            try {
                DocumentFileJpaController pjc = new DocumentFileJpaController();
                DocumentFile p = pjc.findDocumentFile(Integer.valueOf(fileToDownload));
                File f = new File(p.getPath());
                FileInputStream fstr = new FileInputStream(f);
                fileToDownload1 = new DefaultStreamedContent(fstr, "attachment", p.getName());
                if (p.getName().endsWith(".msg")) {
                    fileToDownload1 = new DefaultStreamedContent(fstr, "application/vnd.ms-outlook", p.getName());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return fileToDownload1;
    }

    public void setFileToDownload1(StreamedContent fileToDownload1) {
        this.fileToDownload1 = fileToDownload1;
    }
}
