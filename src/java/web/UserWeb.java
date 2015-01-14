/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pzielins
 */
public class UserWeb implements Serializable {

    public UserWeb() {
        name = "";
        email = "";
        login = "";
        distinguishedName = "";
        isGroup = false;
    }

    public static boolean notInList(List<UserWeb> users, UserWeb user) {
        boolean exists = false;
        if (users == null) {
            users = new ArrayList<UserWeb>();
        }
        for (UserWeb usr : users) {
            if (usr.getEmail().equals(user.getEmail()) && usr.getName().equals(user.getName())) {
                exists = true;
            }
        }
        return !exists;
    }
    private String name;
    private String email;
    private String login;
    private String distinguishedName;
    private boolean isGroup;

    public boolean isIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
