package be.ttime.core.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setMaxBytesLocalHeap("50M");

        // I don't want to set a Name or a default cache is possible || config.setName("mainCache"); || config.addDefaultCache(cacheConfig("default", "5M", false, 1 * 60, null));

        //config.addCache(cacheConfig("org.hibernate.cache.internal.StandardQueryCache", "5M", false, 24 * 60 * 60, null)); // holds the cached query results.
        //config.addCache(cacheConfig("org.hibernate.cache.spi.UpdateTimestampsCache", "5M", true, null, null)); // holds timestamps of the most recent updates to queryable tables.
        config.addCache(cacheConfig("localizedMessage", null, true, null, null));
        config.addCache(cacheConfig("block", null, true, null, null));
        config.addCache(cacheConfig("blockByName", null, true, null, null));
        config.addCache(cacheConfig("navBlock", null, true, null, null));
        config.addCache(cacheConfig("renderedBlock", null, true, null, null));
        config.addCache(cacheConfig("page", "10M", true, null, null));
        config.addCache(cacheConfig("user", "5M", true, null, null));
        config.addCache(cacheConfig("persistentLogin", "5M", true, null, null));

        return net.sf.ehcache.CacheManager.create(config);
    }

    private CacheConfiguration cacheConfig(String name, String maxSizeHeap, boolean eternal, Integer timeToLive, Integer timeToIdle) {

        CacheConfiguration config = new CacheConfiguration();
        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        persistenceConfiguration.setStrategy("none"); // none => only memory (default) | "localTempSwap"
        config.persistence(persistenceConfiguration);
        config.setName(name);
        config.setEternal(eternal);
        config.setMemoryStoreEvictionPolicy("LRU");
        if (maxSizeHeap != null) {
            config.setMaxBytesLocalHeap(maxSizeHeap);
        }

        if (timeToLive != null)
            config.setTimeToLiveSeconds(timeToLive);

        if (timeToIdle != null)
            config.setTimeToIdleSeconds(timeToIdle);

        return config;

        // other setting
        // messageCache.setMemoryStoreEvictionPolicy("LRU"); // memoryStoreEvictionPolicy: Politique d'éviction : LRU -> le moins récemment utilisé | LFU -> le moins fréquemment utilisé | FIFO
        // setMaxEntriesLocalHeap(1000); // Nombre d'entrée maximum
        // messageCache.setMaxBytesLocalDisk("100M"); // 30Mo pour se cache max
        // messageCache.setEternal(true); // définit si les éléments sont éternels. Si c’est le cas, le timeout sera ignoré et l'élément n'est jamais expiré.
        // setTimeToIdleSeconds: C'est le nombre de secondes que l'élément doit vivre depuis sa dernière utilisation.La valeur par défaut est 0, l'élément reste toujours en repos
        // setTimeToLiveSeconds: C'est le nombre de secondes que l'élément doit vivre depuis sa création en cache.La valeur par défaut est 0, l’élément vivra éternellement.
        // diskExpiryThreadIntervalSeconds: Nombre de secondes entre deux exécutions du processus de contrôle d'éviction.
        // diskPersistent: Permet la mémorisation des objets sur le disque pour une récupération des objets entre deux démarrages de la VM.
        // overflowToDisk: Détermine si les objets peuvent être stockés sur le disque en cas d'atteinte du maximum d'éléments en mémoire
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    /**
     * For basic purposes, the default key generation system for cached data works. The parameters to your method become the key in your cache store.
     * But this becomes problematic when you want cache the result of another method that also takes in the same value
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            // This will generate a unique key of the class names, the method names,
            // and all method parameters appended.
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}