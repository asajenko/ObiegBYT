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
import entities.Document;
import entities.DocumentFile;
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
public class DocumentFileJpaController implements Serializable {

    public DocumentFileJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocumentFile documentFile) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document documentId = documentFile.getDocumentId();
            if (documentId != null) {
                documentId = em.getReference(documentId.getClass(), documentId.getId());
                documentFile.setDocumentId(documentId);
            }
            em.persist(documentFile);
            if (documentId != null) {
                documentId.getDocumentFileList().add(documentFile);
                documentId = em.merge(documentId);
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

    public void edit(DocumentFile documentFile) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocumentFile persistentDocumentFile = em.find(DocumentFile.class, documentFile.getId());
            Document documentIdOld = persistentDocumentFile.getDocumentId();
            Document documentIdNew = documentFile.getDocumentId();
            if (documentIdNew != null) {
                documentIdNew = em.getReference(documentIdNew.getClass(), documentIdNew.getId());
                documentFile.setDocumentId(documentIdNew);
            }
            documentFile = em.merge(documentFile);
            if (documentIdOld != null && !documentIdOld.equals(documentIdNew)) {
                documentIdOld.getDocumentFileList().remove(documentFile);
                documentIdOld = em.merge(documentIdOld);
            }
            if (documentIdNew != null && !documentIdNew.equals(documentIdOld)) {
                documentIdNew.getDocumentFileList().add(documentFile);
                documentIdNew = em.merge(documentIdNew);
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
                Integer id = documentFile.getId();
                if (findDocumentFile(id) == null) {
                    throw new NonexistentEntityException("The documentFile with id " + id + " no longer exists.");
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
            DocumentFile documentFile;
            try {
                documentFile = em.getReference(DocumentFile.class, id);
                documentFile.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documentFile with id " + id + " no longer exists.", enfe);
            }
            Document documentId = documentFile.getDocumentId();
            if (documentId != null) {
                documentId.getDocumentFileList().remove(documentFile);
                documentId = em.merge(documentId);
            }
            em.remove(documentFile);
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

    public List<DocumentFile> findDocumentFileEntities() {
        return findDocumentFileEntities(true, -1, -1);
    }

    public List<DocumentFile> findDocumentFileEntities(int maxResults, int firstResult) {
        return findDocumentFileEntities(false, maxResults, firstResult);
    }

    private List<DocumentFile> findDocumentFileEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DocumentFile.class));
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

    public DocumentFile findDocumentFile(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DocumentFile.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentFileCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DocumentFile> rt = cq.from(DocumentFile.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
