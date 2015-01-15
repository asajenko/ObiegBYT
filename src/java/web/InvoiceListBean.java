/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Client;
import entities.Invoice;
import entities.InvoiceFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jpa.ClientJpaController;
import jpa.InvoiceFileJpaController;
import jpa.InvoiceJpaController;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = InvoiceListBean.BEAN_NAME)
@ViewScoped
public class InvoiceListBean {

    public static final String BEAN_NAME = "invoiceListBean";
    @ManagedProperty(value = "#{" + SessionBean.BEAN_NAME + "}")
    private SessionBean sessionBean;
    private List<Invoice> invoices = new ArrayList<Invoice>();
    private InvoiceJpaController ijc = new InvoiceJpaController();
    private Invoice newInvoice = new Invoice();
    private List<Client> clients = new ArrayList<Client>();
    private ClientJpaController cjc = new ClientJpaController();
    private InvoiceFileJpaController icjc = new InvoiceFileJpaController();
    private List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
    private UploadedFile selectedFile;

    public InvoiceListBean() {
        invoices = ijc.findInvoiceEntities();
        clients = cjc.findClientEntities();
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Invoice getNewInvoice() {
        return newInvoice;
    }

    public void setNewInvoice(Invoice newInvoice) {
        this.newInvoice = newInvoice;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public void deleteFile() {
        if (uploadedFiles != null && selectedFile != null) {
            uploadedFiles.remove(selectedFile);
            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String appCtx = servletContext.getContextPath();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/system/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(InvoiceListBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void uploadListener(org.primefaces.event.FileUploadEvent event) {
        if (event.getFile() != null) {
            if (uploadedFiles == null) {
                uploadedFiles = new ArrayList<UploadedFile>();
            }
            uploadedFiles.add(event.getFile());
        }
    }

    public void createInvoice() {
        try {
            ijc.create(newInvoice);
            for (UploadedFile f : uploadedFiles) {
                String filePath = saveFile(f, "C:\\obiegfiles");
                if (filePath != null) {
                    InvoiceFile p = new InvoiceFile();
                    p.setInvoiceId(newInvoice);
                    p.setName(f.getFileName());
                    p.setPath(filePath);
                    icjc.create(p);
                }
            }
            invoices = ijc.findInvoiceEntities();
        } catch (Exception ex) {
            Logger.getLogger(InvoiceListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String saveFile(UploadedFile f, String path) {
        Date date = new Date();
        path = path.replaceAll("/", "\\");
        if (!path.endsWith("\\")) {
            path += "\\";
        }
        try {
            File f1 = new File(path + date.getTime());
            OutputStream out = new FileOutputStream(f1);
            byte buf[] = new byte[1024];
            int len;
            InputStream is = f.getInputstream();
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            is.close();
            return path + date.getTime();
        } catch (IOException e) {
        }
        return null;
    }

    public void goToDetails(int id) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = servletContext.getContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/system/details.xhtml?id=" + id);
        } catch (IOException ex) {
            Logger.getLogger(InvoiceListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
