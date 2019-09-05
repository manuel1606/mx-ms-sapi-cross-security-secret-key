package mx.bs.cross.security.provisioning.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.bs.cross.security.cache.model.EndpointCache;
import mx.bs.cross.security.cache.model.SecurityApplicationCache;
import mx.bs.cross.security.cache.model.SecurityClientCache;
import mx.bs.cross.security.cache.model.SecurityMethodCache;
import mx.bs.cross.security.cache.repositories.SecurityApplicationCacheRepository;
import mx.bs.cross.security.cache.repositories.SecurityClientCacheRepository;
import mx.bs.cross.security.db.model.AuthorizeBusinessGroupEntity;
import mx.bs.cross.security.db.model.AuthorizeEndpointEntity;
import mx.bs.cross.security.db.model.AuthorizeSecurityMethodEntity;
import mx.bs.cross.security.db.model.BusinessGroupEntity;
import mx.bs.cross.security.db.model.EndpointEntity;
import mx.bs.cross.security.db.model.SecurityApplicationEntity;
import mx.bs.cross.security.db.model.SecurityClientEntity;
import mx.bs.cross.security.db.model.SecurityMethodEntity;
import mx.bs.cross.security.db.services.AuthorizeBusinessGroupService;
import mx.bs.cross.security.db.services.AuthorizeEndpointService;
import mx.bs.cross.security.db.services.AuthorizeSecurityMethodService;
import mx.bs.cross.security.db.services.SecurityApplicationService;
import mx.bs.cross.security.db.services.SecurityClientService;

@Component
public class ProvisioningCache {

	@Autowired
	SecurityClientCacheRepository securityClientCacheRepository;

	@Autowired
	SecurityApplicationCacheRepository securityApplicationCacheRepository;

	@Autowired
	SecurityClientService securityClientService;

	@Autowired
	SecurityApplicationService securityApplicationService;

	@Autowired
	AuthorizeBusinessGroupService authorizeBusinessGroupService;

	@Autowired
	AuthorizeEndpointService authorizeEndpointService;

	@Autowired
	AuthorizeSecurityMethodService authorizeSecurityMethodService;

	public void updateClients() {
		List<SecurityClientEntity> clients = securityClientService.findAll();
		for (SecurityClientEntity securityClientEntity : clients) {
			SecurityClientCache securityClientCache = new SecurityClientCache();
			securityClientCache.setClientId(securityClientEntity.getClientId());
			securityClientCache.setClientSecret(securityClientEntity.getClientSecret());
			securityClientCache.setScopes(securityClientEntity.getScopes());
			securityClientCache.setActive(securityClientEntity.getActive());
			securityClientCacheRepository.save(securityClientCache);
		}
	}

	Map<String, EndpointCache> loadEndpoints(SecurityApplicationEntity securityApplicationEntity) {
		Map<String, EndpointCache> buffer = new HashMap<>(); // All endpoints the application

		// Authorized Groups
		List<AuthorizeBusinessGroupEntity> groups;
		groups = authorizeBusinessGroupService.findBySecurityApplication(securityApplicationEntity);

		for (AuthorizeBusinessGroupEntity authorizeBusinessGroupEntity : groups) {
			BusinessGroupEntity businessGroupEntity = authorizeBusinessGroupEntity.getBusinessGroup();

			if (authorizeBusinessGroupEntity.getActive() && businessGroupEntity.getActive()) {
				// Authorized Endpoints
				List<AuthorizeEndpointEntity> endpoints;
				endpoints = authorizeEndpointService.findByBusinessGroup(businessGroupEntity);

				for (AuthorizeEndpointEntity authorizeEndpointEntity : endpoints) {
					EndpointEntity endpointEntity = authorizeEndpointEntity.getEndpoint();

					if (authorizeEndpointEntity.getActive() && endpointEntity.getActive()) {
						EndpointCache endpointCache = new EndpointCache();
						endpointCache.setName(endpointEntity.getName());
						endpointCache.setDescription(endpointEntity.getDescription());
						endpointCache.setPath(endpointEntity.getPath());
						endpointCache.setType(endpointEntity.getType());
						endpointCache.setVersion(endpointEntity.getVersion());
						endpointCache.setApiName(endpointEntity.getApiName());
						buffer.put(endpointCache.getPath(), endpointCache);
					}
				}
			}
		}
		return buffer;
	}

	Map<String, SecurityMethodCache> loadMethods(SecurityApplicationEntity securityApplicationEntity) {
		Map<String, SecurityMethodCache> buffer = new HashMap<>();
		List<AuthorizeSecurityMethodEntity> methods;
		methods = authorizeSecurityMethodService.findBySecurityApplication(securityApplicationEntity);
		for (AuthorizeSecurityMethodEntity authorizeSecurityMethodEntity : methods) {
			SecurityMethodEntity securityMethodEntity = authorizeSecurityMethodEntity.getSecurityMethod();

			if (securityMethodEntity.getActive()) {
				SecurityMethodCache securityMethodCache = new SecurityMethodCache();
				securityMethodCache.setId(securityMethodEntity.getId());
				securityMethodCache.setName(securityMethodEntity.getName());
				securityMethodCache.setType(securityMethodEntity.getType());
				securityMethodCache.setSecurityLevel(securityMethodEntity.getSecurityLevel());
				securityMethodCache.setPredefined(authorizeSecurityMethodEntity.isPredefined());
				securityMethodCache.setTokenValidSeconds(authorizeSecurityMethodEntity.getTokenValidSeconds());
				
				buffer.put(securityMethodCache.getId(), securityMethodCache);
			}
		}

		return buffer;
	}

	public void updateApplications() {
		List<SecurityApplicationEntity> apps = securityApplicationService.findAll();
		for (SecurityApplicationEntity securityApplicationEntity : apps) {
			SecurityApplicationCache securityApplicationCache = new SecurityApplicationCache();
			securityApplicationCache.setIdString(securityApplicationEntity.getIdString());
			securityApplicationCache.setDescription(securityApplicationEntity.getDescription());
			securityApplicationCache.setType(securityApplicationEntity.getType());

			Map<String, EndpointCache> endpoints = loadEndpoints(securityApplicationEntity);
			securityApplicationCache.setEndpoints(endpoints);
			
			Map<String, SecurityMethodCache> methods = loadMethods(securityApplicationEntity);
			securityApplicationCache.setMethods(methods);
			
			securityApplicationCacheRepository.save(securityApplicationCache);
		}
	}
}
