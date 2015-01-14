/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import helper.Constants;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;


/**
 *
 * @author asajenko
 */
@ManagedBean(name = ApplicationBackup.BEAN_NAME)
@ApplicationScoped
public class ApplicationBackup implements Serializable {

    public static final String BEAN_NAME = "applicationBackup";
    private String version = Constants.VERSION;
    private String appCtx;
    private String projectStage;

    public String getVersion() {
            return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Creates a new instance of ApplicationBackup
     */
    public ApplicationBackup() {
    }

    public String getAppCtx() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        appCtx = servletContext.getContextPath();
        return appCtx;
    }

    public void setAppCtx(String appCtx) {
        this.appCtx = appCtx;
    }

    private String generatePassword() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String pass = "";
        for (int i = 0; i < 6; i++) {
            //Generate letter or number
            Random generator = new Random();
            int ln = generator.nextInt();
            if (ln % 2 == 0) {
                //It's letter
                int randomIndex = generator.nextInt(26);
                String letter = String.valueOf(alphabet.charAt(randomIndex));
                int bigSmall = generator.nextInt();
                if (bigSmall % 2 == 0) {
                    pass += letter.toUpperCase();
                } else {
                    pass += letter;
                }
            } else {
                //It's number
                pass += String.valueOf(generator.nextInt(10));
            }
        }
        return pass;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes());

            byte[] mdbytes = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            System.out.println("Digest(in hex format):: " + sb.toString());
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                String hex = Integer.toHexString(0xff & mdbytes[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            System.out.println("Digest(in hex format):: " + hexString.toString());
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ApplicationBackup.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public void addFacesMessage(String msg, int level) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (level == 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sukces!", msg));
        } else if (level == 1) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Uwaga!", msg));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd!", msg));
        }
    }

    public String getProjectStage() {
        if (this.projectStage == null) {
            ProjectStage projectStage = FacesContext.getCurrentInstance().getApplication().getProjectStage();
            if (projectStage != null) {
                this.projectStage = projectStage.toString();
            } else {
                this.projectStage = "Unknown";
            }
        }
        return this.projectStage;
    }
}
