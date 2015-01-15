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
import entities.Invoice;
import entities.InvoiceFile;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author admin
 */
public class InvoiceFileJpaController implements Serializable {

    public InvoiceFileJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(InvoiceFile invoiceFile) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice invoiceId = invoiceFile.getInvoiceId();
            if (invoiceId != null) {
                invoiceId = em.getReference(invoiceId.getClass(), invoiceId.getId());
                invoiceFile.setInvoiceId(invoiceId);
            }
            em.persist(invoiceFile);
            if (invoiceId != null) {
                invoiceId.getInvoiceFileList().add(invoiceFile);
                invoiceId = em.merge(invoiceId);
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

    public void edit(InvoiceFile invoiceFile) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            InvoiceFile persistentInvoiceFile = em.find(InvoiceFile.class, invoiceFile.getId());
            Invoice invoiceIdOld = persistentInvoiceFile.getInvoiceId();
            Invoice invoiceIdNew = invoiceFile.getInvoiceId();
            if (invoiceIdNew != null) {
                invoiceIdNew = em.getReference(invoiceIdNew.getClass(), invoiceIdNew.getId());
                invoiceFile.setInvoiceId(invoiceIdNew);
            }
            invoiceFile = em.merge(invoiceFile);
            if (invoiceIdOld != null && !invoiceIdOld.equals(invoiceIdNew)) {
                invoiceIdOld.getInvoiceFileList().remove(invoiceFile);
                invoiceIdOld = em.merge(invoiceIdOld);
            }
            if (invoiceIdNew != null && !invoiceIdNew.equals(invoiceIdOld)) {
                invoiceIdNew.getInvoiceFileList().add(invoiceFile);
                invoiceIdNew = em.merge(invoiceIdNew);
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
                Integer id = invoiceFile.getId();
                if (findInvoiceFile(id) == null) {
                    throw new NonexistentEntityException("The invoiceFile with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            InvoiceFile invoiceFile;
            try {
                invoiceFile = em.getReference(InvoiceFile.class, id);
                invoiceFile.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoiceFile with id " + id + " no longer exists.", enfe);
            }
            Invoice invoiceId = invoiceFile.getInvoiceId();
            if (invoiceId != null) {
                invoiceId.getInvoiceFileList().remove(invoiceFile);
                invoiceId = em.merge(invoiceId);
            }
            em.remove(invoiceFile);
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

    public List<InvoiceFile> findInvoiceFileEntities() {
        return findInvoiceFileEntities(true, -1, -1);
    }

    public List<InvoiceFile> findInvoiceFileEntities(int maxResults, int firstResult) {
        return findInvoiceFileEntities(false, maxResults, firstResult);
    }

    private List<InvoiceFile> findInvoiceFileEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(InvoiceFile.class));
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

    public InvoiceFile findInvoiceFile(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(InvoiceFile.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvoiceFileCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<InvoiceFile> rt = cq.from(InvoiceFile.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
