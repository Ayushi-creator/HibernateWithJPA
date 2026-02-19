package org.example;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JpaUtil {

    private static final EntityManagerFactory emf;
    private static final EmbeddedCacheManager cacheManager;

    static {
        // Step 1: Infinispan GlobalConfig — local mode (no clustering)
        GlobalConfigurationBuilder globalBuilder = new GlobalConfigurationBuilder();
        globalBuilder.nonClusteredDefault();

        // Step 2: Default cache config
        ConfigurationBuilder defaultCacheConfig = new ConfigurationBuilder();
        defaultCacheConfig
                .expiration()
                .lifespan(300, TimeUnit.SECONDS)
                .maxIdle(60, TimeUnit.SECONDS)
                .memory()
                .maxCount(1000);

        // Step 3: CacheManager banao
        cacheManager = new DefaultCacheManager(
                globalBuilder.build(),
                defaultCacheConfig.build()
        );

        // Step 4: Employee entity ke liye specific cache define karo
        ConfigurationBuilder employeeCacheConfig = new ConfigurationBuilder();
        employeeCacheConfig
                .expiration()
                .lifespan(300, TimeUnit.SECONDS)
                .maxIdle(60, TimeUnit.SECONDS)
                .memory()
                .maxCount(1000);

        cacheManager.defineConfiguration("org.example.Employee", employeeCacheConfig.build());
        cacheManager.defineConfiguration("default-update-timestamps-region",
                new ConfigurationBuilder()
                        .expiration().lifespan(-1, TimeUnit.SECONDS) // never expire
                        .build());
        cacheManager.defineConfiguration("default-query-results-region",
                new ConfigurationBuilder()
                        .expiration().lifespan(120, TimeUnit.SECONDS)
                        .build());

        // Step 5: Hibernate ko CacheManager pass karo
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.cache.use_second_level_cache", "true");
        props.put("hibernate.cache.use_query_cache", "true");
        props.put("hibernate.generate_statistics", "true");
        props.put("hibernate.cache.region.factory_class",
                "org.infinispan.hibernate.cache.v62.InfinispanRegionFactory");
        props.put("infinispan.hibernate.cache.manager", cacheManager); // ← key line

        emf = Persistence.createEntityManagerFactory("employeePU", props);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        if (emf != null) emf.close();
        if (cacheManager != null) cacheManager.stop();
    }
}