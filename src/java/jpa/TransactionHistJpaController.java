/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Transaction;
import entities.TransactionHist;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author asajenko
 */
public class TransactionHistJpaController implements Serializable {

    public TransactionHistJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    public void create(TransactionHist transactionHist) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaction transId = transactionHist.getTransId();
            if (transId != null) {
                transId = em.getReference(transId.getClass(), transId.getId());
                transactionHist.setTransId(transId);
            }
            em.persist(transactionHist);
            if (transId != null) {
                transId.getTransactionHistList().add(transactionHist);
                transId = em.merge(transId);
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

    public void edit(TransactionHist transactionHist) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransactionHist persistentTransactionHist = em.find(TransactionHist.class, transactionHist.getId());
            Transaction transIdOld = persistentTransactionHist.getTransId();
            Transaction transIdNew = transactionHist.getTransId();
            if (transIdNew != null) {
                transIdNew = em.getReference(transIdNew.getClass(), transIdNew.getId());
                transactionHist.setTransId(transIdNew);
            }
            transactionHist = em.merge(transactionHist);
            if (transIdOld != null && !transIdOld.equals(transIdNew)) {
                transIdOld.getTransactionHistList().remove(transactionHist);
                transIdOld = em.merge(transIdOld);
            }
            if (transIdNew != null && !transIdNew.equals(transIdOld)) {
                transIdNew.getTransactionHistList().add(transactionHist);
                transIdNew = em.merge(transIdNew);
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
                Integer id = transactionHist.getId();
                if (findTransactionHist(id) == null) {
                    throw new NonexistentEntityException("The transactionHist with id " + id + " no longer exists.");
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
            TransactionHist transactionHist;
            try {
                transactionHist = em.getReference(TransactionHist.class, id);
                transactionHist.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transactionHist with id " + id + " no longer exists.", enfe);
            }
            Transaction transId = transactionHist.getTransId();
            if (transId != null) {
                transId.getTransactionHistList().remove(transactionHist);
                transId = em.merge(transId);
            }
            em.remove(transactionHist);
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

    public List<TransactionHist> findTransactionHistEntities() {
        return findTransactionHistEntities(true, -1, -1);
    }

    public List<TransactionHist> findTransactionHistEntities(int maxResults, int firstResult) {
        return findTransactionHistEntities(false, maxResults, firstResult);
    }

    private List<TransactionHist> findTransactionHistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransactionHist.class));
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

    public TransactionHist findTransactionHist(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransactionHist.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransactionHistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransactionHist> rt = cq.from(TransactionHist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
