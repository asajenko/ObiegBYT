/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "document")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Document.findAll", query = "SELECT d FROM Document d"),
    @NamedQuery(name = "Document.findById", query = "SELECT d FROM Document d WHERE d.id = :id"),
    @NamedQuery(name = "Document.findByInserted", query = "SELECT d FROM Document d WHERE d.inserted = :inserted"),
    @NamedQuery(name = "Document.findByModifiedDate", query = "SELECT d FROM Document d WHERE d.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "Document.findByDataDostarczenia", query = "SELECT d FROM Document d WHERE d.dataDostarczenia = :dataDostarczenia")})
public class Document implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentId")
    private List<DocumentFile> documentFileList;
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
    @Column(name = "nadawca")
    private String nadawca;
    @Lob
    @Size(max = 65535)
    @Column(name = "notatka")
    private String notatka;
    @Column(name = "data_dostarczenia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDostarczenia;
    @JoinColumn(name = "inserted_by", referencedColumnName = "user_id")
    @ManyToOne
    private Users insertedBy;

    public Document() {
    }

    public Document(Integer id) {
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

    public String getNadawca() {
        return nadawca;
    }

    public void setNadawca(String nadawca) {
        this.nadawca = nadawca;
    }

    public String getNotatka() {
        return notatka;
    }

    public void setNotatka(String notatka) {
        this.notatka = notatka;
    }

    public Date getDataDostarczenia() {
        return dataDostarczenia;
    }

    public void setDataDostarczenia(Date dataDostarczenia) {
        this.dataDostarczenia = dataDostarczenia;
    }

    public Users getInsertedBy() {
        return insertedBy;
    }

    public void setInsertedBy(Users insertedBy) {
        this.insertedBy = insertedBy;
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
        if (!(object instanceof Document)) {
            return false;
        }
        Document other = (Document) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Document[ id=" + id + " ]";
    }

    @XmlTransient
    public List<DocumentFile> getDocumentFileList() {
        return documentFileList;
    }

    public void setDocumentFileList(List<DocumentFile> documentFileList) {
        this.documentFileList = documentFileList;
    }
    
}
