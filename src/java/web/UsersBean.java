/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entities.Groups;
import entities.Users;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jpa.GroupsJpaController;
import jpa.UsersJpaController;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author asajenko
 */
@ManagedBean(name = UsersBean.BEAN_NAME)
@RequestScoped
public class UsersBean {

    public static final String BEAN_NAME = "usersBean";
    private List<Users> users = new ArrayList<Users>();
    private UsersJpaController ujc = new UsersJpaController();
    private Users newUser = new Users();
    private boolean admin;

    public List<Users> getUsers() {
        users = ujc.findUsersEntities();
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    public Users getNewUser() {
        return newUser;
    }

    public void setNewUser(Users newUser) {
        this.newUser = newUser;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void createUser() {
        try {
            GroupsJpaController gjc = new GroupsJpaController();
            if (admin) {
                Collection<Groups> groups = new ArrayList<Groups>();
                groups.addAll(gjc.findGroupsEntities());
                newUser.setGroupsCollection(groups);
            } else {
                Collection<Groups> groups = new ArrayList<Groups>();
                groups.add(gjc.findGroups(1));
                newUser.setGroupsCollection(groups);
            }
            newUser.setPassword(hashPassword("password"));
            ujc.create(newUser);
        } catch (Exception ex) {
            Logger.getLogger(UsersBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteUser(Users user) {
        try {
            ujc.destroy(user.getUserId());
        } catch (RollbackFailureException ex) {
            Logger.getLogger(UsersBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(UsersBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void goToDetails(int id) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = servletContext.getContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/system/details.xhtml?id=" + id);
        } catch (IOException ex) {
            Logger.getLogger(UsersBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
