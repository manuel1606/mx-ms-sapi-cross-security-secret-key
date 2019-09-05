package mx.bs.cross.security.provisioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import mx.bs.cross.security.provisioning.cache.ProvisioningCache;

/**
 * Load initial cache config.
 * 
 * @author Arq. Jesús Israel Anaya Salazar
 */
@Component
@Order(2)
public class CacheLoader implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(CacheLoader.class);

	@Autowired
	ProvisioningCache provisioningCache;

	@Scheduled(cron = "${config-cache.scheduled.cron}")
	public void Load() {
		logger.info("Loading config cache...");
		provisioningCache.updateClients();
		provisioningCache.updateApplications();
		logger.info("Loaded config cache.");
	}

	@Override
	public void run(String... args) throws Exception {
		Load();
	}
}
