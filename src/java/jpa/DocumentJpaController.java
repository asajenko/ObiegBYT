/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import entities.Document;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Users;
import entities.DocumentFile;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.exceptions.IllegalOrphanException;
import jpa.exceptions.NonexistentEntityException;
import jpa.exceptions.RollbackFailureException;

/**
 *
 * @author admin
 */
public class DocumentJpaController implements Serializable {

    public DocumentJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Document document) throws RollbackFailureException, Exception {
        if (document.getDocumentFileList() == null) {
            document.setDocumentFileList(new ArrayList<DocumentFile>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users insertedBy = document.getInsertedBy();
            if (insertedBy != null) {
                insertedBy = em.getReference(insertedBy.getClass(), insertedBy.getUserId());
                document.setInsertedBy(insertedBy);
            }
            List<DocumentFile> attachedDocumentFileList = new ArrayList<DocumentFile>();
            for (DocumentFile documentFileListDocumentFileToAttach : document.getDocumentFileList()) {
                documentFileListDocumentFileToAttach = em.getReference(documentFileListDocumentFileToAttach.getClass(), documentFileListDocumentFileToAttach.getId());
                attachedDocumentFileList.add(documentFileListDocumentFileToAttach);
            }
            document.setDocumentFileList(attachedDocumentFileList);
            em.persist(document);
            if (insertedBy != null) {
                insertedBy.getDocumentList().add(document);
                insertedBy = em.merge(insertedBy);
            }
            for (DocumentFile documentFileListDocumentFile : document.getDocumentFileList()) {
                Document oldDocumentIdOfDocumentFileListDocumentFile = documentFileListDocumentFile.getDocumentId();
                documentFileListDocumentFile.setDocumentId(document);
                documentFileListDocumentFile = em.merge(documentFileListDocumentFile);
                if (oldDocumentIdOfDocumentFileListDocumentFile != null) {
                    oldDocumentIdOfDocumentFileListDocumentFile.getDocumentFileList().remove(documentFileListDocumentFile);
                    oldDocumentIdOfDocumentFileListDocumentFile = em.merge(oldDocumentIdOfDocumentFileListDocumentFile);
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

    public void edit(Document document) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document persistentDocument = em.find(Document.class, document.getId());
            Users insertedByOld = persistentDocument.getInsertedBy();
            Users insertedByNew = document.getInsertedBy();
            List<DocumentFile> documentFileListOld = persistentDocument.getDocumentFileList();
            List<DocumentFile> documentFileListNew = document.getDocumentFileList();
            List<String> illegalOrphanMessages = null;
            for (DocumentFile documentFileListOldDocumentFile : documentFileListOld) {
                if (!documentFileListNew.contains(documentFileListOldDocumentFile)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentFile " + documentFileListOldDocumentFile + " since its documentId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (insertedByNew != null) {
                insertedByNew = em.getReference(insertedByNew.getClass(), insertedByNew.getUserId());
                document.setInsertedBy(insertedByNew);
            }
            List<DocumentFile> attachedDocumentFileListNew = new ArrayList<DocumentFile>();
            for (DocumentFile documentFileListNewDocumentFileToAttach : documentFileListNew) {
                documentFileListNewDocumentFileToAttach = em.getReference(documentFileListNewDocumentFileToAttach.getClass(), documentFileListNewDocumentFileToAttach.getId());
                attachedDocumentFileListNew.add(documentFileListNewDocumentFileToAttach);
            }
            documentFileListNew = attachedDocumentFileListNew;
            document.setDocumentFileList(documentFileListNew);
            document = em.merge(document);
            if (insertedByOld != null && !insertedByOld.equals(insertedByNew)) {
                insertedByOld.getDocumentList().remove(document);
                insertedByOld = em.merge(insertedByOld);
            }
            if (insertedByNew != null && !insertedByNew.equals(insertedByOld)) {
                insertedByNew.getDocumentList().add(document);
                insertedByNew = em.merge(insertedByNew);
            }
            for (DocumentFile documentFileListNewDocumentFile : documentFileListNew) {
                if (!documentFileListOld.contains(documentFileListNewDocumentFile)) {
                    Document oldDocumentIdOfDocumentFileListNewDocumentFile = documentFileListNewDocumentFile.getDocumentId();
                    documentFileListNewDocumentFile.setDocumentId(document);
                    documentFileListNewDocumentFile = em.merge(documentFileListNewDocumentFile);
                    if (oldDocumentIdOfDocumentFileListNewDocumentFile != null && !oldDocumentIdOfDocumentFileListNewDocumentFile.equals(document)) {
                        oldDocumentIdOfDocumentFileListNewDocumentFile.getDocumentFileList().remove(documentFileListNewDocumentFile);
                        oldDocumentIdOfDocumentFileListNewDocumentFile = em.merge(oldDocumentIdOfDocumentFileListNewDocumentFile);
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
                Integer id = document.getId();
                if (findDocument(id) == null) {
                    throw new NonexistentEntityException("The document with id " + id + " no longer exists.");
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
            Document document;
            try {
                document = em.getReference(Document.class, id);
                document.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The document with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DocumentFile> documentFileListOrphanCheck = document.getDocumentFileList();
            for (DocumentFile documentFileListOrphanCheckDocumentFile : documentFileListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Document (" + document + ") cannot be destroyed since the DocumentFile " + documentFileListOrphanCheckDocumentFile + " in its documentFileList field has a non-nullable documentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Users insertedBy = document.getInsertedBy();
            if (insertedBy != null) {
                insertedBy.getDocumentList().remove(document);
                insertedBy = em.merge(insertedBy);
            }
            em.remove(document);
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

    public List<Document> findDocumentEntities() {
        return findDocumentEntities(true, -1, -1);
    }

    public List<Document> findDocumentEntities(int maxResults, int firstResult) {
        return findDocumentEntities(false, maxResults, firstResult);
    }

    private List<Document> findDocumentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Document.class));
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

    public Document findDocument(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Document.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Document> rt = cq.from(Document.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
