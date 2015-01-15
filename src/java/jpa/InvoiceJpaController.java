/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Users;
import entities.Client;
import entities.Invoice;
import entities.InvoiceFile;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import jpa.exceptions.IllegalOrphanException;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author admin
 */
public class InvoiceJpaController implements Serializable {

    public InvoiceJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Invoice invoice) throws RollbackFailureException, Exception {
        if (invoice.getInvoiceFileList() == null) {
            invoice.setInvoiceFileList(new ArrayList<InvoiceFile>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users insertedBy = invoice.getInsertedBy();
            if (insertedBy != null) {
                insertedBy = em.getReference(insertedBy.getClass(), insertedBy.getUserId());
                invoice.setInsertedBy(insertedBy);
            }
            Client clientId = invoice.getClientId();
            if (clientId != null) {
                clientId = em.getReference(clientId.getClass(), clientId.getId());
                invoice.setClientId(clientId);
            }
            List<InvoiceFile> attachedInvoiceFileList = new ArrayList<InvoiceFile>();
            for (InvoiceFile invoiceFileListInvoiceFileToAttach : invoice.getInvoiceFileList()) {
                invoiceFileListInvoiceFileToAttach = em.getReference(invoiceFileListInvoiceFileToAttach.getClass(), invoiceFileListInvoiceFileToAttach.getId());
                attachedInvoiceFileList.add(invoiceFileListInvoiceFileToAttach);
            }
            invoice.setInvoiceFileList(attachedInvoiceFileList);
            em.persist(invoice);
            if (insertedBy != null) {
                insertedBy.getInvoiceList().add(invoice);
                insertedBy = em.merge(insertedBy);
            }
            if (clientId != null) {
                clientId.getInvoiceList().add(invoice);
                clientId = em.merge(clientId);
            }
            for (InvoiceFile invoiceFileListInvoiceFile : invoice.getInvoiceFileList()) {
                Invoice oldInvoiceIdOfInvoiceFileListInvoiceFile = invoiceFileListInvoiceFile.getInvoiceId();
                invoiceFileListInvoiceFile.setInvoiceId(invoice);
                invoiceFileListInvoiceFile = em.merge(invoiceFileListInvoiceFile);
                if (oldInvoiceIdOfInvoiceFileListInvoiceFile != null) {
                    oldInvoiceIdOfInvoiceFileListInvoiceFile.getInvoiceFileList().remove(invoiceFileListInvoiceFile);
                    oldInvoiceIdOfInvoiceFileListInvoiceFile = em.merge(oldInvoiceIdOfInvoiceFileListInvoiceFile);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Invoice invoice) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice persistentInvoice = em.find(Invoice.class, invoice.getId());
            Users insertedByOld = persistentInvoice.getInsertedBy();
            Users insertedByNew = invoice.getInsertedBy();
            Client clientIdOld = persistentInvoice.getClientId();
            Client clientIdNew = invoice.getClientId();
            List<InvoiceFile> invoiceFileListOld = persistentInvoice.getInvoiceFileList();
            List<InvoiceFile> invoiceFileListNew = invoice.getInvoiceFileList();
            List<String> illegalOrphanMessages = null;
            for (InvoiceFile invoiceFileListOldInvoiceFile : invoiceFileListOld) {
                if (!invoiceFileListNew.contains(invoiceFileListOldInvoiceFile)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain InvoiceFile " + invoiceFileListOldInvoiceFile + " since its invoiceId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (insertedByNew != null) {
                insertedByNew = em.getReference(insertedByNew.getClass(), insertedByNew.getUserId());
                invoice.setInsertedBy(insertedByNew);
            }
            if (clientIdNew != null) {
                clientIdNew = em.getReference(clientIdNew.getClass(), clientIdNew.getId());
                invoice.setClientId(clientIdNew);
            }
            List<InvoiceFile> attachedInvoiceFileListNew = new ArrayList<InvoiceFile>();
            for (InvoiceFile invoiceFileListNewInvoiceFileToAttach : invoiceFileListNew) {
                invoiceFileListNewInvoiceFileToAttach = em.getReference(invoiceFileListNewInvoiceFileToAttach.getClass(), invoiceFileListNewInvoiceFileToAttach.getId());
                attachedInvoiceFileListNew.add(invoiceFileListNewInvoiceFileToAttach);
            }
            invoiceFileListNew = attachedInvoiceFileListNew;
            invoice.setInvoiceFileList(invoiceFileListNew);
            invoice = em.merge(invoice);
            if (insertedByOld != null && !insertedByOld.equals(insertedByNew)) {
                insertedByOld.getInvoiceList().remove(invoice);
                insertedByOld = em.merge(insertedByOld);
            }
            if (insertedByNew != null && !insertedByNew.equals(insertedByOld)) {
                insertedByNew.getInvoiceList().add(invoice);
                insertedByNew = em.merge(insertedByNew);
            }
            if (clientIdOld != null && !clientIdOld.equals(clientIdNew)) {
                clientIdOld.getInvoiceList().remove(invoice);
                clientIdOld = em.merge(clientIdOld);
            }
            if (clientIdNew != null && !clientIdNew.equals(clientIdOld)) {
                clientIdNew.getInvoiceList().add(invoice);
                clientIdNew = em.merge(clientIdNew);
            }
            for (InvoiceFile invoiceFileListNewInvoiceFile : invoiceFileListNew) {
                if (!invoiceFileListOld.contains(invoiceFileListNewInvoiceFile)) {
                    Invoice oldInvoiceIdOfInvoiceFileListNewInvoiceFile = invoiceFileListNewInvoiceFile.getInvoiceId();
                    invoiceFileListNewInvoiceFile.setInvoiceId(invoice);
                    invoiceFileListNewInvoiceFile = em.merge(invoiceFileListNewInvoiceFile);
                    if (oldInvoiceIdOfInvoiceFileListNewInvoiceFile != null && !oldInvoiceIdOfInvoiceFileListNewInvoiceFile.equals(invoice)) {
                        oldInvoiceIdOfInvoiceFileListNewInvoiceFile.getInvoiceFileList().remove(invoiceFileListNewInvoiceFile);
                        oldInvoiceIdOfInvoiceFileListNewInvoiceFile = em.merge(oldInvoiceIdOfInvoiceFileListNewInvoiceFile);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invoice.getId();
                if (findInvoice(id) == null) {
                    throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice invoice;
            try {
                invoice = em.getReference(Invoice.class, id);
                invoice.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<InvoiceFile> invoiceFileListOrphanCheck = invoice.getInvoiceFileList();
            for (InvoiceFile invoiceFileListOrphanCheckInvoiceFile : invoiceFileListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Invoice (" + invoice + ") cannot be destroyed since the InvoiceFile " + invoiceFileListOrphanCheckInvoiceFile + " in its invoiceFileList field has a non-nullable invoiceId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Users insertedBy = invoice.getInsertedBy();
            if (insertedBy != null) {
                insertedBy.getInvoiceList().remove(invoice);
                insertedBy = em.merge(insertedBy);
            }
            Client clientId = invoice.getClientId();
            if (clientId != null) {
                clientId.getInvoiceList().remove(invoice);
                clientId = em.merge(clientId);
            }
            em.remove(invoice);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Invoice> findInvoiceEntities() {
        return findInvoiceEntities(true, -1, -1);
    }

    public List<Invoice> findInvoiceEntities(int maxResults, int firstResult) {
        return findInvoiceEntities(false, maxResults, firstResult);
    }

    private List<Invoice> findInvoiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Invoice.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Invoice findInvoice(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Invoice.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvoiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Invoice> rt = cq.from(Invoice.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
