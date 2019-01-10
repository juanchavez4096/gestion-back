package com.empresa.consumo.masivo.gestion.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;




@Configuration
@EnableCaching
public class CachingConfig {
	
	
	
	@Bean
	public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCache = new CaffeineCacheManager("usuarioImageShort", "usuarioImageBig", "usuarioImageMed", "materialImageShort", "materialImageBig", "materialImageMed", "productoImageShort", "productoImageBig", "productoImageMed");
        caffeineCache.setCaffeine(Caffeine.newBuilder().expireAfterAccess(2, TimeUnit.MINUTES).maximumSize(500));
        return caffeineCache;
	}

}