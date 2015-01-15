/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import entities.Client;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import web.InvoiceListBean;

/**
 *
 * @author admin
 */
@ManagedBean(name = "clientConverter")
@ViewScoped
public class ClientConverter implements Converter {

    @ManagedProperty(value = "#{" + InvoiceListBean.BEAN_NAME + "}")
    private InvoiceListBean invoiceListBean;

    public InvoiceListBean getInvoiceListBean() {
        return invoiceListBean;
    }

    public void setInvoiceListBean(InvoiceListBean invoiceListBean) {
        this.invoiceListBean = invoiceListBean;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        List<Client> clients = invoiceListBean.getClients();
        Iterator<Client> it = clients.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
            Client tmp = it.next();
            if (tmp.getDostawca().equals(value)) {
                found = true;
                return tmp;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Client) {
            return ((Client) value).getDostawca();
        }
        return "";
    }
}
