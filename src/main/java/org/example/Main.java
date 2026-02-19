package org.example;

import org.example.Dao.EmployeeDao;
import org.example.Dao.EmployeeDaoImpl;
import org.hibernate.stat.Statistics;

public class Main {

    public static void main(String[] args) {

        // ✅ 1. Statistics PEHLE enable karo
        Statistics cache = JpaUtil.getEntityManagerFactory()
                .unwrap(org.hibernate.SessionFactory.class)
                .getStatistics();
        cache.setStatisticsEnabled(true);

        // ✅ 2. Phir DAO operations
        EmployeeDao dao = new EmployeeDaoImpl();

        Employee emp1 = new Employee("Ayushi", "IT", 60000);
        dao.saveEmployee(emp1);
        System.out.println("Employee Saved!");

        System.out.println("\n--- First getEmployee call ---");
        Employee fetched1 = dao.getEmployee(emp1.getId());

        System.out.println("\n--- Second getEmployee call ---");
        Employee fetched2 = dao.getEmployee(emp1.getId());

        System.out.println("\nL2 Cache Hit Count : " + cache.getSecondLevelCacheHitCount());
        System.out.println("L2 Cache Miss Count: " + cache.getSecondLevelCacheMissCount());
        System.out.println("L2 Cache Put Count : " + cache.getSecondLevelCachePutCount());

        JpaUtil.getEntityManagerFactory().close();
    }
}