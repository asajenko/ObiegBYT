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
import entities.Note;
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
public class NoteJpaController implements Serializable {

    public NoteJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Note note) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Client clientId = note.getClientId();
            if (clientId != null) {
                clientId = em.getReference(clientId.getClass(), clientId.getId());
                note.setClientId(clientId);
            }
            em.persist(note);
            if (clientId != null) {
                clientId.getNoteList().add(note);
                clientId = em.merge(clientId);
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

    public void edit(Note note) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Note persistentNote = em.find(Note.class, note.getId());
            Client clientIdOld = persistentNote.getClientId();
            Client clientIdNew = note.getClientId();
            if (clientIdNew != null) {
                clientIdNew = em.getReference(clientIdNew.getClass(), clientIdNew.getId());
                note.setClientId(clientIdNew);
            }
            note = em.merge(note);
            if (clientIdOld != null && !clientIdOld.equals(clientIdNew)) {
                clientIdOld.getNoteList().remove(note);
                clientIdOld = em.merge(clientIdOld);
            }
            if (clientIdNew != null && !clientIdNew.equals(clientIdOld)) {
                clientIdNew.getNoteList().add(note);
                clientIdNew = em.merge(clientIdNew);
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
                Integer id = note.getId();
                if (findNote(id) == null) {
                    throw new NonexistentEntityException("The note with id " + id + " no longer exists.");
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
            Note note;
            try {
                note = em.getReference(Note.class, id);
                note.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The note with id " + id + " no longer exists.", enfe);
            }
            Client clientId = note.getClientId();
            if (clientId != null) {
                clientId.getNoteList().remove(note);
                clientId = em.merge(clientId);
            }
            em.remove(note);
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

    public List<Note> findNoteEntities() {
        return findNoteEntities(true, -1, -1);
    }

    public List<Note> findNoteEntities(int maxResults, int firstResult) {
        return findNoteEntities(false, maxResults, firstResult);
    }

    private List<Note> findNoteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Note.class));
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

    public Note findNote(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Note.class, id);
        } finally {
            em.close();
        }
    }

    public int getNoteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Note> rt = cq.from(Note.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
