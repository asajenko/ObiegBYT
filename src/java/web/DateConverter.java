/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author pzielins
 */
@FacesConverter("web.DateConverter")
public class DateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || (value != null && value.isEmpty())) {
            return null;
        }

        try {
            Date date = new Date(value);
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            return date;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            String val = df.format((Date) value);
            return val;
        } catch (Exception ex) {
            return "";
        }
    }
    
    public String getAsStringNotHours(FacesContext context, UIComponent component, Object value) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            String val = df.format((Date) value);
            return val;
        } catch (Exception ex) {
            return "";
        }
    }
}
