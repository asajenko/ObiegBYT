/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import entities.Client;
import entities.Invoice;
import entities.Note;
import entities.Transaction;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;
import jpa.exceptions.IllegalOrphanException;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author admin
 */
public class ClientJpaController implements Serializable {

    public ClientJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Client client) throws RollbackFailureException, Exception {
        if (client.getTransactionList() == null) {
            client.setTransactionList(new ArrayList<Transaction>());
        }
        if (client.getNoteList() == null) {
            client.setNoteList(new ArrayList<Note>());
        }
        if (client.getInvoiceList() == null) {
            client.setInvoiceList(new ArrayList<Invoice>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Transaction> attachedTransactionList = new ArrayList<Transaction>();
            for (Transaction transactionListTransactionToAttach : client.getTransactionList()) {
                transactionListTransactionToAttach = em.getReference(transactionListTransactionToAttach.getClass(), transactionListTransactionToAttach.getId());
                attachedTransactionList.add(transactionListTransactionToAttach);
            }
            client.setTransactionList(attachedTransactionList);
            List<Note> attachedNoteList = new ArrayList<Note>();
            for (Note noteListNoteToAttach : client.getNoteList()) {
                noteListNoteToAttach = em.getReference(noteListNoteToAttach.getClass(), noteListNoteToAttach.getId());
                attachedNoteList.add(noteListNoteToAttach);
            }
            client.setNoteList(attachedNoteList);
            List<Invoice> attachedInvoiceList = new ArrayList<Invoice>();
            for (Invoice invoiceListInvoiceToAttach : client.getInvoiceList()) {
                invoiceListInvoiceToAttach = em.getReference(invoiceListInvoiceToAttach.getClass(), invoiceListInvoiceToAttach.getId());
                attachedInvoiceList.add(invoiceListInvoiceToAttach);
            }
            client.setInvoiceList(attachedInvoiceList);
            em.persist(client);
            for (Transaction transactionListTransaction : client.getTransactionList()) {
                Client oldClientIdOfTransactionListTransaction = transactionListTransaction.getClientId();
                transactionListTransaction.setClientId(client);
                transactionListTransaction = em.merge(transactionListTransaction);
                if (oldClientIdOfTransactionListTransaction != null) {
                    oldClientIdOfTransactionListTransaction.getTransactionList().remove(transactionListTransaction);
                    oldClientIdOfTransactionListTransaction = em.merge(oldClientIdOfTransactionListTransaction);
                }
            }
            for (Note noteListNote : client.getNoteList()) {
                Client oldClientIdOfNoteListNote = noteListNote.getClientId();
                noteListNote.setClientId(client);
                noteListNote = em.merge(noteListNote);
                if (oldClientIdOfNoteListNote != null) {
                    oldClientIdOfNoteListNote.getNoteList().remove(noteListNote);
                    oldClientIdOfNoteListNote = em.merge(oldClientIdOfNoteListNote);
                }
            }
            for (Invoice invoiceListInvoice : client.getInvoiceList()) {
                Client oldClientIdOfInvoiceListInvoice = invoiceListInvoice.getClientId();
                invoiceListInvoice.setClientId(client);
                invoiceListInvoice = em.merge(invoiceListInvoice);
                if (oldClientIdOfInvoiceListInvoice != null) {
                    oldClientIdOfInvoiceListInvoice.getInvoiceList().remove(invoiceListInvoice);
                    oldClientIdOfInvoiceListInvoice = em.merge(oldClientIdOfInvoiceListInvoice);
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

    public void edit(Client client) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Client persistentClient = em.find(Client.class, client.getId());
            List<Transaction> transactionListOld = persistentClient.getTransactionList();
            List<Transaction> transactionListNew = client.getTransactionList();
            List<Note> noteListOld = persistentClient.getNoteList();
            List<Note> noteListNew = client.getNoteList();
            List<Invoice> invoiceListOld = persistentClient.getInvoiceList();
            List<Invoice> invoiceListNew = client.getInvoiceList();
            List<String> illegalOrphanMessages = null;
            for (Transaction transactionListOldTransaction : transactionListOld) {
                if (!transactionListNew.contains(transactionListOldTransaction)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transaction " + transactionListOldTransaction + " since its clientId field is not nullable.");
                }
            }
            for (Note noteListOldNote : noteListOld) {
                if (!noteListNew.contains(noteListOldNote)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Note " + noteListOldNote + " since its clientId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Transaction> attachedTransactionListNew = new ArrayList<Transaction>();
            for (Transaction transactionListNewTransactionToAttach : transactionListNew) {
                transactionListNewTransactionToAttach = em.getReference(transactionListNewTransactionToAttach.getClass(), transactionListNewTransactionToAttach.getId());
                attachedTransactionListNew.add(transactionListNewTransactionToAttach);
            }
            transactionListNew = attachedTransactionListNew;
            client.setTransactionList(transactionListNew);
            List<Note> attachedNoteListNew = new ArrayList<Note>();
            for (Note noteListNewNoteToAttach : noteListNew) {
                noteListNewNoteToAttach = em.getReference(noteListNewNoteToAttach.getClass(), noteListNewNoteToAttach.getId());
                attachedNoteListNew.add(noteListNewNoteToAttach);
            }
            noteListNew = attachedNoteListNew;
            client.setNoteList(noteListNew);
            List<Invoice> attachedInvoiceListNew = new ArrayList<Invoice>();
            for (Invoice invoiceListNewInvoiceToAttach : invoiceListNew) {
                invoiceListNewInvoiceToAttach = em.getReference(invoiceListNewInvoiceToAttach.getClass(), invoiceListNewInvoiceToAttach.getId());
                attachedInvoiceListNew.add(invoiceListNewInvoiceToAttach);
            }
            invoiceListNew = attachedInvoiceListNew;
            client.setInvoiceList(invoiceListNew);
            client = em.merge(client);
            for (Transaction transactionListNewTransaction : transactionListNew) {
                if (!transactionListOld.contains(transactionListNewTransaction)) {
                    Client oldClientIdOfTransactionListNewTransaction = transactionListNewTransaction.getClientId();
                    transactionListNewTransaction.setClientId(client);
                    transactionListNewTransaction = em.merge(transactionListNewTransaction);
                    if (oldClientIdOfTransactionListNewTransaction != null && !oldClientIdOfTransactionListNewTransaction.equals(client)) {
                        oldClientIdOfTransactionListNewTransaction.getTransactionList().remove(transactionListNewTransaction);
                        oldClientIdOfTransactionListNewTransaction = em.merge(oldClientIdOfTransactionListNewTransaction);
                    }
                }
            }
            for (Note noteListNewNote : noteListNew) {
                if (!noteListOld.contains(noteListNewNote)) {
                    Client oldClientIdOfNoteListNewNote = noteListNewNote.getClientId();
                    noteListNewNote.setClientId(client);
                    noteListNewNote = em.merge(noteListNewNote);
                    if (oldClientIdOfNoteListNewNote != null && !oldClientIdOfNoteListNewNote.equals(client)) {
                        oldClientIdOfNoteListNewNote.getNoteList().remove(noteListNewNote);
                        oldClientIdOfNoteListNewNote = em.merge(oldClientIdOfNoteListNewNote);
                    }
                }
            }
            for (Invoice invoiceListOldInvoice : invoiceListOld) {
                if (!invoiceListNew.contains(invoiceListOldInvoice)) {
                    invoiceListOldInvoice.setClientId(null);
                    invoiceListOldInvoice = em.merge(invoiceListOldInvoice);
                }
            }
            for (Invoice invoiceListNewInvoice : invoiceListNew) {
                if (!invoiceListOld.contains(invoiceListNewInvoice)) {
                    Client oldClientIdOfInvoiceListNewInvoice = invoiceListNewInvoice.getClientId();
                    invoiceListNewInvoice.setClientId(client);
                    invoiceListNewInvoice = em.merge(invoiceListNewInvoice);
                    if (oldClientIdOfInvoiceListNewInvoice != null && !oldClientIdOfInvoiceListNewInvoice.equals(client)) {
                        oldClientIdOfInvoiceListNewInvoice.getInvoiceList().remove(invoiceListNewInvoice);
                        oldClientIdOfInvoiceListNewInvoice = em.merge(oldClientIdOfInvoiceListNewInvoice);
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
                Integer id = client.getId();
                if (findClient(id) == null) {
                    throw new NonexistentEntityException("The client with id " + id + " no longer exists.");
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
            Client client;
            try {
                client = em.getReference(Client.class, id);
                client.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The client with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Transaction> transactionListOrphanCheck = client.getTransactionList();
            for (Transaction transactionListOrphanCheckTransaction : transactionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Client (" + client + ") cannot be destroyed since the Transaction " + transactionListOrphanCheckTransaction + " in its transactionList field has a non-nullable clientId field.");
            }
            List<Note> noteListOrphanCheck = client.getNoteList();
            for (Note noteListOrphanCheckNote : noteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Client (" + client + ") cannot be destroyed since the Note " + noteListOrphanCheckNote + " in its noteList field has a non-nullable clientId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Invoice> invoiceList = client.getInvoiceList();
            for (Invoice invoiceListInvoice : invoiceList) {
                invoiceListInvoice.setClientId(null);
                invoiceListInvoice = em.merge(invoiceListInvoice);
            }
            em.remove(client);
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

    public List<Client> findClientEntities() {
        return findClientEntities(true, -1, -1);
    }

    public List<Client> findClientEntities(int maxResults, int firstResult) {
        return findClientEntities(false, maxResults, firstResult);
    }

    private List<Client> findClientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Client.class));
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

    public Client findClient(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Client.class, id);
        } finally {
            em.close();
        }
    }

    public int getClientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Client> rt = cq.from(Client.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Client findClientById(String id) {
        EntityManager em = getEntityManager();
        try {
            String query = "SELECT c FROM Client c WHERE c.id = " + id;
            Query q = em.createQuery(query);
            return (Client) q.getSingleResult();
        } catch (Exception ex) {
            goToIndex();
            return null;
        } finally {
            em.close();
        }
    }

    public void goToIndex() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = servletContext.getContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(appCtx + "/system/index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ClientJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
