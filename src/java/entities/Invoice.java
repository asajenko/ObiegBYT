/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "invoice")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i"),
    @NamedQuery(name = "Invoice.findById", query = "SELECT i FROM Invoice i WHERE i.id = :id"),
    @NamedQuery(name = "Invoice.findByInserted", query = "SELECT i FROM Invoice i WHERE i.inserted = :inserted"),
    @NamedQuery(name = "Invoice.findByModifiedDate", query = "SELECT i FROM Invoice i WHERE i.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "Invoice.findByTerminWystawienia", query = "SELECT i FROM Invoice i WHERE i.terminWystawienia = :terminWystawienia"),
    @NamedQuery(name = "Invoice.findByTerminPlatnosci", query = "SELECT i FROM Invoice i WHERE i.terminPlatnosci = :terminPlatnosci")})
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "inserted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inserted;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "number")
    private String number;
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Lob
    @Size(max = 65535)
    @Column(name = "merytoryczny")
    private String merytoryczny;
    @Lob
    @Size(max = 65535)
    @Column(name = "rachunkowy")
    private String rachunkowy;
    @Lob
    @Size(max = 16777215)
    @Column(name = "brutto")
    private String brutto;
    @Lob
    @Size(max = 16777215)
    @Column(name = "netto")
    private String netto;
    @Column(name = "termin_wystawienia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date terminWystawienia;
    @Column(name = "termin_platnosci")
    @Temporal(TemporalType.TIMESTAMP)
    private Date terminPlatnosci;
    @JoinColumn(name = "inserted_by", referencedColumnName = "user_id")
    @ManyToOne
    private Users insertedBy;
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @ManyToOne
    private Client clientId;

    public Invoice() {
    }

    public Invoice(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getInserted() {
        return inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getMerytoryczny() {
        return merytoryczny;
    }

    public void setMerytoryczny(String merytoryczny) {
        this.merytoryczny = merytoryczny;
    }

    public String getRachunkowy() {
        return rachunkowy;
    }

    public void setRachunkowy(String rachunkowy) {
        this.rachunkowy = rachunkowy;
    }

    public String getBrutto() {
        return brutto;
    }

    public void setBrutto(String brutto) {
        this.brutto = brutto;
    }

    public String getNetto() {
        return netto;
    }

    public void setNetto(String netto) {
        this.netto = netto;
    }

    public Date getTerminWystawienia() {
        return terminWystawienia;
    }

    public void setTerminWystawienia(Date terminWystawienia) {
        this.terminWystawienia = terminWystawienia;
    }

    public Date getTerminPlatnosci() {
        return terminPlatnosci;
    }

    public void setTerminPlatnosci(Date terminPlatnosci) {
        this.terminPlatnosci = terminPlatnosci;
    }

    public Users getInsertedBy() {
        return insertedBy;
    }

    public void setInsertedBy(Users insertedBy) {
        this.insertedBy = insertedBy;
    }

    public Client getClientId() {
        return clientId;
    }

    public void setClientId(Client clientId) {
        this.clientId = clientId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Invoice[ id=" + id + " ]";
    }
    
}
