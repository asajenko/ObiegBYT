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
import entities.Client;
import entities.Transaction;
import entities.TransactionHist;
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
 * @author asajenko
 */
public class TransactionJpaController implements Serializable {

    public TransactionJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Transaction transaction) throws RollbackFailureException, Exception {
        if (transaction.getTransactionHistList() == null) {
            transaction.setTransactionHistList(new ArrayList<TransactionHist>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Client clientId = transaction.getClientId();
            if (clientId != null) {
                clientId = em.getReference(clientId.getClass(), clientId.getId());
                transaction.setClientId(clientId);
            }
            List<TransactionHist> attachedTransactionHistList = new ArrayList<TransactionHist>();
            for (TransactionHist transactionHistListTransactionHistToAttach : transaction.getTransactionHistList()) {
                transactionHistListTransactionHistToAttach = em.getReference(transactionHistListTransactionHistToAttach.getClass(), transactionHistListTransactionHistToAttach.getId());
                attachedTransactionHistList.add(transactionHistListTransactionHistToAttach);
            }
            transaction.setTransactionHistList(attachedTransactionHistList);
            em.persist(transaction);
            if (clientId != null) {
                clientId.getTransactionList().add(transaction);
                clientId = em.merge(clientId);
            }
            for (TransactionHist transactionHistListTransactionHist : transaction.getTransactionHistList()) {
                Transaction oldTransIdOfTransactionHistListTransactionHist = transactionHistListTransactionHist.getTransId();
                transactionHistListTransactionHist.setTransId(transaction);
                transactionHistListTransactionHist = em.merge(transactionHistListTransactionHist);
                if (oldTransIdOfTransactionHistListTransactionHist != null) {
                    oldTransIdOfTransactionHistListTransactionHist.getTransactionHistList().remove(transactionHistListTransactionHist);
                    oldTransIdOfTransactionHistListTransactionHist = em.merge(oldTransIdOfTransactionHistListTransactionHist);
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

    public void edit(Transaction transaction) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaction persistentTransaction = em.find(Transaction.class, transaction.getId());
            Client clientIdOld = persistentTransaction.getClientId();
            Client clientIdNew = transaction.getClientId();
            List<TransactionHist> transactionHistListOld = persistentTransaction.getTransactionHistList();
            List<TransactionHist> transactionHistListNew = transaction.getTransactionHistList();
            List<String> illegalOrphanMessages = null;
            for (TransactionHist transactionHistListOldTransactionHist : transactionHistListOld) {
                if (!transactionHistListNew.contains(transactionHistListOldTransactionHist)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TransactionHist " + transactionHistListOldTransactionHist + " since its transId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clientIdNew != null) {
                clientIdNew = em.getReference(clientIdNew.getClass(), clientIdNew.getId());
                transaction.setClientId(clientIdNew);
            }
            List<TransactionHist> attachedTransactionHistListNew = new ArrayList<TransactionHist>();
            for (TransactionHist transactionHistListNewTransactionHistToAttach : transactionHistListNew) {
                transactionHistListNewTransactionHistToAttach = em.getReference(transactionHistListNewTransactionHistToAttach.getClass(), transactionHistListNewTransactionHistToAttach.getId());
                attachedTransactionHistListNew.add(transactionHistListNewTransactionHistToAttach);
            }
            transactionHistListNew = attachedTransactionHistListNew;
            transaction.setTransactionHistList(transactionHistListNew);
            transaction = em.merge(transaction);
            if (clientIdOld != null && !clientIdOld.equals(clientIdNew)) {
                clientIdOld.getTransactionList().remove(transaction);
                clientIdOld = em.merge(clientIdOld);
            }
            if (clientIdNew != null && !clientIdNew.equals(clientIdOld)) {
                clientIdNew.getTransactionList().add(transaction);
                clientIdNew = em.merge(clientIdNew);
            }
            for (TransactionHist transactionHistListNewTransactionHist : transactionHistListNew) {
                if (!transactionHistListOld.contains(transactionHistListNewTransactionHist)) {
                    Transaction oldTransIdOfTransactionHistListNewTransactionHist = transactionHistListNewTransactionHist.getTransId();
                    transactionHistListNewTransactionHist.setTransId(transaction);
                    transactionHistListNewTransactionHist = em.merge(transactionHistListNewTransactionHist);
                    if (oldTransIdOfTransactionHistListNewTransactionHist != null && !oldTransIdOfTransactionHistListNewTransactionHist.equals(transaction)) {
                        oldTransIdOfTransactionHistListNewTransactionHist.getTransactionHistList().remove(transactionHistListNewTransactionHist);
                        oldTransIdOfTransactionHistListNewTransactionHist = em.merge(oldTransIdOfTransactionHistListNewTransactionHist);
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
                Integer id = transaction.getId();
                if (findTransaction(id) == null) {
                    throw new NonexistentEntityException("The transaction with id " + id + " no longer exists.");
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
            Transaction transaction;
            try {
                transaction = em.getReference(Transaction.class, id);
                transaction.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaction with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TransactionHist> transactionHistListOrphanCheck = transaction.getTransactionHistList();
            for (TransactionHist transactionHistListOrphanCheckTransactionHist : transactionHistListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Transaction (" + transaction + ") cannot be destroyed since the TransactionHist " + transactionHistListOrphanCheckTransactionHist + " in its transactionHistList field has a non-nullable transId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Client clientId = transaction.getClientId();
            if (clientId != null) {
                clientId.getTransactionList().remove(transaction);
                clientId = em.merge(clientId);
            }
            em.remove(transaction);
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

    public List<Transaction> findTransactionEntities() {
        return findTransactionEntities(true, -1, -1);
    }

    public List<Transaction> findTransactionEntities(int maxResults, int firstResult) {
        return findTransactionEntities(false, maxResults, firstResult);
    }

    private List<Transaction> findTransactionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transaction.class));
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

    public Transaction findTransaction(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaction.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransactionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transaction> rt = cq.from(Transaction.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
