/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Client;
import entities.Document;
import entities.DocumentFile;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jpa.ClientJpaController;
import jpa.DocumentFileJpaController;
import jpa.DocumentJpaController;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = DocumentListBean.BEAN_NAME)
@ViewScoped
public class DocumentListBean {

    public static final String BEAN_NAME = "documentListBean";
    private List<Document> documents = new ArrayList<Document>();
    private DocumentJpaController djc = new DocumentJpaController();
    private List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
    private Document newDocument = new Document();
    private DocumentFileJpaController dfjc = new DocumentFileJpaController();
    private UploadedFile selectedFile;
    private List<Client> clients = new ArrayList<Client>();
    private ClientJpaController cjc = new ClientJpaController();

    public DocumentListBean() {
        documents = djc.findDocumentEntities();
        clients = cjc.findClientEntities();
    }

    public UploadedFile getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(UploadedFile selectedFile) {
        this.selectedFile = selectedFile;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public Document getNewDocument() {
        return newDocument;
    }

    public void setNewDocument(Document newDocument) {
        this.newDocument = newDocument;
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

    public void createDocument() {
        try {
            djc.create(newDocument);
            for (UploadedFile f : uploadedFiles) {
                String filePath = saveFile(f, "C:\\obiegfiles");
                if (filePath != null) {
                    DocumentFile p = new DocumentFile();
                    p.setDocumentId(newDocument);
                    p.setName(f.getFileName());
                    p.setPath(filePath);
                    dfjc.create(p);
                }
            }
            documents = djc.findDocumentEntities();
        } catch (Exception ex) {
            Logger.getLogger(DocumentListBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DocumentListBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
