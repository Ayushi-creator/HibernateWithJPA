package org.example.Dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.Employee;
import org.example.JpaUtil;

public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public void saveEmployee(Employee emp) {

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(emp);
            tx.commit();
        } finally {
            em.close();
        }
    }
//---------------------------------------------JPA
//    @Override
//    public Employee getEmployee(int id) {
//
//        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
//        try {
//            return em.find(Employee.class, id);
//        } finally {
//            em.close();
//        }
//    }
    //-----------------------------------------------L1cache jpa se
//@Override
//public Employee getEmployee(int id) {
//
//    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
//    EntityTransaction tx = em.getTransaction();
//
//    try {
//        tx.begin();
//
//        Employee emp1 = em.find(Employee.class, id);
//        System.out.println("First fetch");
//
//        Employee emp2 = em.find(Employee.class, id);
//        System.out.println("Second fetch");
//
//        tx.commit();
//        return emp1;
//
//    } finally {
//        em.close();
//    }
//}
    //criteria Api  L1 cache
//@Override
//public Employee getEmployee(int id) {
//
//    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
//    EntityTransaction tx = em.getTransaction();
//
//    try {
//        tx.begin();
//
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
//        Root<Employee> root = cq.from(Employee.class);
//
//        cq.select(root).where(cb.equal(root.get("id"), id));
//        Employee emp1 = em.createQuery(cq).getSingleResult();
//        System.out.println("First fetch: " + emp1);
//
//        cq.select(root).where(cb.equal(root.get("id"), id));
//        Employee emp2 = em.createQuery(cq).getSingleResult();
//        System.out.println("Second fetch: " + emp2);
//
//        System.out.println("emp1 == emp2 ? " + (emp1 == emp2));
//
//        tx.commit();
//        return emp1;
//
//    } finally {
//        em.close();
//    }
//}


    //l2 cache
    @Override
    public Employee getEmployee(int id) {

        // (Session 1) ──
        EntityManager em1 = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx1 = em1.getTransaction();

        Employee emp1;
        try {
            tx1.begin();
            emp1 = em1.find(Employee.class, id);
            System.out.println("Session 1 fetch: " + emp1);
            tx1.commit();
        } finally {
            em1.close();
        }

        //(Session 2) ──
        EntityManager em2 = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx2 = em2.getTransaction();

        Employee emp2;
        try {
            tx2.begin();
            emp2 = em2.find(Employee.class, id);
            System.out.println("Session 2 fetch: " + emp2);
            tx2.commit();
        } finally {
            em2.close();
        }

        return emp2;
    }

    @Override
    public void updateEmployee(Employee emp) {

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(emp);   // JPA me update ki jagah merge
            tx.commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteEmployee(int id) {

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Employee emp = em.find(Employee.class, id);
            if (emp != null) {
                em.remove(emp);
            }
            tx.commit();
        } finally {
            em.close();
        }
    }
}