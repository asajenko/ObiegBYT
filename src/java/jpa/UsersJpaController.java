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
import entities.Groups;
import java.util.ArrayList;
import java.util.Collection;
import entities.Invoice;
import java.util.List;
import entities.Document;
import entities.Users;
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
public class UsersJpaController implements Serializable {

    public UsersJpaController() {
        emf = Persistence.createEntityManagerFactory("bungePU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        emf.getCache().evictAll();
        return emf.createEntityManager();
    }

    public void create(Users users) throws RollbackFailureException, Exception {
        if (users.getGroupsCollection() == null) {
            users.setGroupsCollection(new ArrayList<Groups>());
        }
        if (users.getInvoiceList() == null) {
            users.setInvoiceList(new ArrayList<Invoice>());
        }
        if (users.getDocumentList() == null) {
            users.setDocumentList(new ArrayList<Document>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Groups> attachedGroupsCollection = new ArrayList<Groups>();
            for (Groups groupsCollectionGroupsToAttach : users.getGroupsCollection()) {
                groupsCollectionGroupsToAttach = em.getReference(groupsCollectionGroupsToAttach.getClass(), groupsCollectionGroupsToAttach.getGroupId());
                attachedGroupsCollection.add(groupsCollectionGroupsToAttach);
            }
            users.setGroupsCollection(attachedGroupsCollection);
            List<Invoice> attachedInvoiceList = new ArrayList<Invoice>();
            for (Invoice invoiceListInvoiceToAttach : users.getInvoiceList()) {
                invoiceListInvoiceToAttach = em.getReference(invoiceListInvoiceToAttach.getClass(), invoiceListInvoiceToAttach.getId());
                attachedInvoiceList.add(invoiceListInvoiceToAttach);
            }
            users.setInvoiceList(attachedInvoiceList);
            List<Document> attachedDocumentList = new ArrayList<Document>();
            for (Document documentListDocumentToAttach : users.getDocumentList()) {
                documentListDocumentToAttach = em.getReference(documentListDocumentToAttach.getClass(), documentListDocumentToAttach.getId());
                attachedDocumentList.add(documentListDocumentToAttach);
            }
            users.setDocumentList(attachedDocumentList);
            em.persist(users);
            for (Groups groupsCollectionGroups : users.getGroupsCollection()) {
                groupsCollectionGroups.getUsersCollection().add(users);
                groupsCollectionGroups = em.merge(groupsCollectionGroups);
            }
            for (Invoice invoiceListInvoice : users.getInvoiceList()) {
                Users oldInsertedByOfInvoiceListInvoice = invoiceListInvoice.getInsertedBy();
                invoiceListInvoice.setInsertedBy(users);
                invoiceListInvoice = em.merge(invoiceListInvoice);
                if (oldInsertedByOfInvoiceListInvoice != null) {
                    oldInsertedByOfInvoiceListInvoice.getInvoiceList().remove(invoiceListInvoice);
                    oldInsertedByOfInvoiceListInvoice = em.merge(oldInsertedByOfInvoiceListInvoice);
                }
            }
            for (Document documentListDocument : users.getDocumentList()) {
                Users oldInsertedByOfDocumentListDocument = documentListDocument.getInsertedBy();
                documentListDocument.setInsertedBy(users);
                documentListDocument = em.merge(documentListDocument);
                if (oldInsertedByOfDocumentListDocument != null) {
                    oldInsertedByOfDocumentListDocument.getDocumentList().remove(documentListDocument);
                    oldInsertedByOfDocumentListDocument = em.merge(oldInsertedByOfDocumentListDocument);
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

    public void edit(Users users) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users persistentUsers = em.find(Users.class, users.getUserId());
            Collection<Groups> groupsCollectionOld = persistentUsers.getGroupsCollection();
            Collection<Groups> groupsCollectionNew = users.getGroupsCollection();
            List<Invoice> invoiceListOld = persistentUsers.getInvoiceList();
            List<Invoice> invoiceListNew = users.getInvoiceList();
            List<Document> documentListOld = persistentUsers.getDocumentList();
            List<Document> documentListNew = users.getDocumentList();
            Collection<Groups> attachedGroupsCollectionNew = new ArrayList<Groups>();
            for (Groups groupsCollectionNewGroupsToAttach : groupsCollectionNew) {
                groupsCollectionNewGroupsToAttach = em.getReference(groupsCollectionNewGroupsToAttach.getClass(), groupsCollectionNewGroupsToAttach.getGroupId());
                attachedGroupsCollectionNew.add(groupsCollectionNewGroupsToAttach);
            }
            groupsCollectionNew = attachedGroupsCollectionNew;
            users.setGroupsCollection(groupsCollectionNew);
            List<Invoice> attachedInvoiceListNew = new ArrayList<Invoice>();
            for (Invoice invoiceListNewInvoiceToAttach : invoiceListNew) {
                invoiceListNewInvoiceToAttach = em.getReference(invoiceListNewInvoiceToAttach.getClass(), invoiceListNewInvoiceToAttach.getId());
                attachedInvoiceListNew.add(invoiceListNewInvoiceToAttach);
            }
            invoiceListNew = attachedInvoiceListNew;
            users.setInvoiceList(invoiceListNew);
            List<Document> attachedDocumentListNew = new ArrayList<Document>();
            for (Document documentListNewDocumentToAttach : documentListNew) {
                documentListNewDocumentToAttach = em.getReference(documentListNewDocumentToAttach.getClass(), documentListNewDocumentToAttach.getId());
                attachedDocumentListNew.add(documentListNewDocumentToAttach);
            }
            documentListNew = attachedDocumentListNew;
            users.setDocumentList(documentListNew);
            users = em.merge(users);
            for (Groups groupsCollectionOldGroups : groupsCollectionOld) {
                if (!groupsCollectionNew.contains(groupsCollectionOldGroups)) {
                    groupsCollectionOldGroups.getUsersCollection().remove(users);
                    groupsCollectionOldGroups = em.merge(groupsCollectionOldGroups);
                }
            }
            for (Groups groupsCollectionNewGroups : groupsCollectionNew) {
                if (!groupsCollectionOld.contains(groupsCollectionNewGroups)) {
                    groupsCollectionNewGroups.getUsersCollection().add(users);
                    groupsCollectionNewGroups = em.merge(groupsCollectionNewGroups);
                }
            }
            for (Invoice invoiceListOldInvoice : invoiceListOld) {
                if (!invoiceListNew.contains(invoiceListOldInvoice)) {
                    invoiceListOldInvoice.setInsertedBy(null);
                    invoiceListOldInvoice = em.merge(invoiceListOldInvoice);
                }
            }
            for (Invoice invoiceListNewInvoice : invoiceListNew) {
                if (!invoiceListOld.contains(invoiceListNewInvoice)) {
                    Users oldInsertedByOfInvoiceListNewInvoice = invoiceListNewInvoice.getInsertedBy();
                    invoiceListNewInvoice.setInsertedBy(users);
                    invoiceListNewInvoice = em.merge(invoiceListNewInvoice);
                    if (oldInsertedByOfInvoiceListNewInvoice != null && !oldInsertedByOfInvoiceListNewInvoice.equals(users)) {
                        oldInsertedByOfInvoiceListNewInvoice.getInvoiceList().remove(invoiceListNewInvoice);
                        oldInsertedByOfInvoiceListNewInvoice = em.merge(oldInsertedByOfInvoiceListNewInvoice);
                    }
                }
            }
            for (Document documentListOldDocument : documentListOld) {
                if (!documentListNew.contains(documentListOldDocument)) {
                    documentListOldDocument.setInsertedBy(null);
                    documentListOldDocument = em.merge(documentListOldDocument);
                }
            }
            for (Document documentListNewDocument : documentListNew) {
                if (!documentListOld.contains(documentListNewDocument)) {
                    Users oldInsertedByOfDocumentListNewDocument = documentListNewDocument.getInsertedBy();
                    documentListNewDocument.setInsertedBy(users);
                    documentListNewDocument = em.merge(documentListNewDocument);
                    if (oldInsertedByOfDocumentListNewDocument != null && !oldInsertedByOfDocumentListNewDocument.equals(users)) {
                        oldInsertedByOfDocumentListNewDocument.getDocumentList().remove(documentListNewDocument);
                        oldInsertedByOfDocumentListNewDocument = em.merge(oldInsertedByOfDocumentListNewDocument);
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
                Integer id = users.getUserId();
                if (findUsers(id) == null) {
                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
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
            Users users;
            try {
                users = em.getReference(Users.class, id);
                users.getUserId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
            }
            Collection<Groups> groupsCollection = users.getGroupsCollection();
            for (Groups groupsCollectionGroups : groupsCollection) {
                groupsCollectionGroups.getUsersCollection().remove(users);
                groupsCollectionGroups = em.merge(groupsCollectionGroups);
            }
            List<Invoice> invoiceList = users.getInvoiceList();
            for (Invoice invoiceListInvoice : invoiceList) {
                invoiceListInvoice.setInsertedBy(null);
                invoiceListInvoice = em.merge(invoiceListInvoice);
            }
            List<Document> documentList = users.getDocumentList();
            for (Document documentListDocument : documentList) {
                documentListDocument.setInsertedBy(null);
                documentListDocument = em.merge(documentListDocument);
            }
            em.remove(users);
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

    public List<Users> findUsersEntities() {
        return findUsersEntities(true, -1, -1);
    }

    public List<Users> findUsersEntities(int maxResults, int firstResult) {
        return findUsersEntities(false, maxResults, firstResult);
    }

    private List<Users> findUsersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Users.class));
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

    public Users findUsers(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Users> rt = cq.from(Users.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Users findUserByLogin(String login) {
        EntityManager em = getEntityManager();
        try {
            String query = "SELECT u FROM Users u WHERE u.username = :username1;";
            Query q = em.createQuery(query, Users.class);
            q.setParameter("username1", login);
            return (Users) q.getSingleResult();
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        } finally {
            em.close();
        }
    }

}
