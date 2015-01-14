/*
 * To change this template, choose Tools | Templates
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author asajenko
 */
@Entity
@Table(name = "transaction_hist")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionHist.findAll", query = "SELECT t FROM TransactionHist t"),
    @NamedQuery(name = "TransactionHist.findById", query = "SELECT t FROM TransactionHist t WHERE t.id = :id"),
    @NamedQuery(name = "TransactionHist.findByStatus", query = "SELECT t FROM TransactionHist t WHERE t.status = :status"),
    @NamedQuery(name = "TransactionHist.findByInserted", query = "SELECT t FROM TransactionHist t WHERE t.inserted = :inserted")})
public class TransactionHist implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Lob
    @Size(max = 65535)
    @Column(name = "kierownik")
    private String kierownik;
    @Column(name = "status")
    private Integer status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "inserted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inserted;
    @JoinColumn(name = "trans_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Transaction transId;

    public TransactionHist() {
    }

    public TransactionHist(Integer id) {
        this.id = id;
    }

    public TransactionHist(Integer id, Date inserted) {
        this.id = id;
        this.inserted = inserted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKierownik() {
        return kierownik;
    }

    public void setKierownik(String kierownik) {
        this.kierownik = kierownik;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getInserted() {
        return inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    public Transaction getTransId() {
        return transId;
    }

    public void setTransId(Transaction transId) {
        this.transId = transId;
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
        if (!(object instanceof TransactionHist)) {
            return false;
        }
        TransactionHist other = (TransactionHist) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.TransactionHist[ id=" + id + " ]";
    }
    
}
