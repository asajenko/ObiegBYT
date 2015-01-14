/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

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
import jpa.InvoiceJpaController;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = InvoiceListBean.BEAN_NAME)
@RequestScoped
public class InvoiceListBean {

    public static final String BEAN_NAME = "invoiceListBean";
    private List<Invoice> invoices = new ArrayList<Invoice>();
    private InvoiceJpaController ijc = new InvoiceJpaController();

    public List<Invoice> getInvoices() {
        invoices = ijc.findInvoiceEntities();
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
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
