/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author asajenko
 */
@Entity
@Table(name = "config")
@NamedQueries({
    @NamedQuery(name = "Config.findAll", query = "SELECT c FROM Config c"),
    @NamedQuery(name = "Config.findByZmienna", query = "SELECT c FROM Config c WHERE c.zmienna = :zmienna")})
public class Config implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "zmienna")
    private String zmienna;
    @Lob
    @Column(name = "wartosc")
    private String wartosc;

    public Config() {
    }

    public Config(String zmienna) {
        this.zmienna = zmienna;
    }

    public String getZmienna() {
        return zmienna;
    }

    public void setZmienna(String zmienna) {
        this.zmienna = zmienna;
    }

    public String getWartosc() {
        return wartosc;
    }

    public void setWartosc(String wartosc) {
        this.wartosc = wartosc;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (zmienna != null ? zmienna.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Config)) {
            return false;
        }
        Config other = (Config) object;
        if ((this.zmienna == null && other.zmienna != null) || (this.zmienna != null && !this.zmienna.equals(other.zmienna))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Config[zmienna=" + zmienna + "]";
    }

}
