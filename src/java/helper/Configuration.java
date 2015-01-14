/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import entities.Config;
import java.util.List;
import javax.inject.Named;
import jpa.ConfigJpaController;

/**
 *
 * @author pzielins
 */
@Named(value = "configurationApp")
public class Configuration {

    public static String EMAIL_SENT = "Przesłano dokument";

    public static String getConfigParameter(String paramName) {
        ConfigJpaController cjc = new ConfigJpaController();
        List<Config> confList = cjc.findConfigEntities();
        if (confList != null && !confList.isEmpty()) {
            for (Config c : confList) {
                if (c.getZmienna().equalsIgnoreCase(paramName)) {
                    return c.getWartosc();
                }
            }
        }
        return "";
    }
}
