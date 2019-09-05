package mx.bs.cross.security.provisioning;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import mx.bs.cross.db.commons.model.DataException;
import mx.bs.cross.security.commons.EndpointType;
import mx.bs.cross.security.commons.SecurityApplicationType;
import mx.bs.cross.security.commons.SecurityLevel;
import mx.bs.cross.security.commons.SecurityMethodType;
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
import mx.bs.cross.security.db.services.BusinessGroupService;
import mx.bs.cross.security.db.services.EndpointService;
import mx.bs.cross.security.db.services.SecurityApplicationService;
import mx.bs.cross.security.db.services.SecurityClientService;
import mx.bs.cross.security.db.services.SecurityMethodService;

/**
 * Load initial data, first user client_id.
 * 
 * @author Arq. Jesús Israel Anaya Salazar
 */
@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(DataLoader.class);

	private final String CLIENT_ID = "admin";
	private final String SECRET = "admin";

	private final String DUMMY_APP_ID = "APPDUM";

	private final String SCOPES = "read write trust";
	private final int ACCESS_TOKEN_VALIDITY_ONE_MINUTE = 60;
	private final int ACCESS_TOKEN_VALIDITY_TEN_MINUTES = 10 * ACCESS_TOKEN_VALIDITY_ONE_MINUTE;
	private final int ACCESS_TOKEN_VALIDITY_THIRTY_MINUTES = 30 * ACCESS_TOKEN_VALIDITY_ONE_MINUTE;

	@Autowired
	SecurityClientService securityClientService;

	@Autowired
	SecurityApplicationService securityApplicationService;

	@Autowired
	SecurityMethodService securityMethodService;

	@Autowired
	EndpointService endpointService;

	@Autowired
	BusinessGroupService businessGroupService;

	@Autowired
	AuthorizeSecurityMethodService authorizeSecurityMethodService;

	@Autowired
	AuthorizeEndpointService authorizeEndpointService;

	@Autowired
	AuthorizeBusinessGroupService authorizeBusinessGroupService;

	void createClients() {

		SecurityClientEntity firstUser = securityClientService.findByClientId(CLIENT_ID);
		if (firstUser == null) {
			firstUser = new SecurityClientEntity();
			firstUser.setClientId(CLIENT_ID);
			firstUser.setClientSecret(SECRET);
			firstUser.setComments("Default admin user");
			firstUser.setScopes(SCOPES);
			firstUser.setActive(true);

			securityClientService.save(firstUser);
		}
	}

	void createApps() {

		Random rand = new Random();
		for (int i = 0; i < 20; i++) {
			String idApp = DUMMY_APP_ID + i;
			SecurityApplicationEntity securityApplication = securityApplicationService.findByIdString(idApp);
			if (securityApplication == null) {
				int type = rand.nextInt(3);
				SecurityApplicationType appType = SecurityApplicationType.values()[type];

				securityApplication = new SecurityApplicationEntity();
				securityApplication.setIdString(idApp);
				securityApplication.setType(appType);
				securityApplication.setActive(true);
				securityApplication.setDescription("App Dummy" + i);
				securityApplicationService.save(securityApplication);
			}
		}
	}

	void createMethods() {
		SecurityMethodEntity[] methods = new SecurityMethodEntity[] {
				new SecurityMethodEntity("f05d384b-c791-41d8-a4bd-3b64e95566a1",
						"mx-ms-sapi-cross-security-sso-legacy-token-ib", SecurityMethodType.Authentication,
						SecurityLevel.AUTHENTICATED),
				new SecurityMethodEntity("dbee4e41-a5cd-4f6a-8ee7-57b2ec898908",
						"mx-ms-sapi-cross-security-identify-user-oam", SecurityMethodType.Identification,
						SecurityLevel.IDENTIFIED),
				new SecurityMethodEntity("ca300e3f-8e89-41f6-9261-c1bca456c1ac",
						"mx-ms-sapi-cross-security-authenticate-user-pass-oam", SecurityMethodType.Authentication,
						SecurityLevel.AUTHENTICATED), };

		for (SecurityMethodEntity securityMethodEntity : methods) {
			try {
				SecurityMethodEntity buffer = securityMethodService.findById(securityMethodEntity.getId());
			} catch (DataException ex) {
				securityMethodService.save(securityMethodEntity);
			}
		}
	}

	void createSecurityEndpoints() {
		String[] endpoints = new String[] { "/security/sso/token/anonymous", "/security/sso/token/identify",
				"/security/sso/token/authenticate" };

		for (String endpoint : endpoints) {
			EndpointEntity endpointEntity = endpointService.findByPath(endpoint);
			if (endpointEntity == null) {
				endpointEntity = new EndpointEntity();
				endpointEntity.setName(endpoint);
				endpointEntity.setDescription("Security API");
				endpointEntity.setPath(endpoint);
				endpointEntity.setType(EndpointType.SAPI);
				endpointEntity.setVersion("1.0.0");
				endpointEntity.setApiName("Security");
				endpointService.save(endpointEntity);
			}
		}
	}

	void createDummyEndpoints() {
		String[] endpoints = new String[] { "/ms/sextype/level00", "/ms/sextype/level10", "/ms/sextype/level20",
				"/ms/sextype/level30" };

		for (String endpoint : endpoints) {
			EndpointEntity endpointEntity = endpointService.findByPath(endpoint);
			if (endpointEntity == null) {
				endpointEntity = new EndpointEntity();
				endpointEntity.setName("sextype " + endpoint);
				endpointEntity.setDescription("Catalogo de sexo");
				endpointEntity.setPath(endpoint);
				endpointEntity.setType(EndpointType.SAPI);
				endpointEntity.setVersion("1.0.0");
				endpointEntity.setApiName("CATALOG");
				endpointService.save(endpointEntity);
			}
		}
	}

	void createBusinessGroups() {
		String[] groupsName = new String[] { "Inversiones", "Financiamiento" };

		for (String groupName : groupsName) {
			BusinessGroupEntity businessGroupEntity = businessGroupService.findByName(groupName);
			if (businessGroupEntity == null) {
				businessGroupEntity = new BusinessGroupEntity();
				businessGroupEntity.setName(groupName);
				businessGroupService.save(businessGroupEntity);
			}
		}
	}

	void authSecurityMethods() {
		List<SecurityMethodEntity> randomMethod = securityMethodService.findAll();

		for (SecurityApplicationEntity securityApplication : securityApplicationService.findAll()) {
			List<AuthorizeSecurityMethodEntity> methods = authorizeSecurityMethodService
					.findBySecurityApplication(securityApplication);

			if (methods.isEmpty()) {
				for (SecurityMethodEntity securityMethodEntity : randomMethod) {
					AuthorizeSecurityMethodEntity auth = new AuthorizeSecurityMethodEntity();
					auth.setSecurityApplication(securityApplication);
					auth.setSecurityMethod(securityMethodEntity);
					auth.setPredefined(true);
					auth.setTokenValidSeconds(ACCESS_TOKEN_VALIDITY_THIRTY_MINUTES);
					authorizeSecurityMethodService.save(auth);
				}
			}
		}
	}

	void authEndpoint() {
		List<EndpointEntity> randomEndpoints = endpointService.findAll();

		for (BusinessGroupEntity businessGroupEntity : businessGroupService.findAll()) {
			List<AuthorizeEndpointEntity> endpoints = authorizeEndpointService.findByBusinessGroup(businessGroupEntity);

			if (endpoints.isEmpty()) {
				for (EndpointEntity endpointEntity : randomEndpoints) {
					AuthorizeEndpointEntity endpoint = new AuthorizeEndpointEntity();
					endpoint.setBusinessGroup(businessGroupEntity);
					endpoint.setEndpoint(endpointEntity);
					authorizeEndpointService.save(endpoint);
				}
			}
		}

	}

	void authBusinessGroup() {
		List<BusinessGroupEntity> randomGroups = businessGroupService.findAll();
		Random rand = new Random();

		for (SecurityApplicationEntity securityApplicationEntity : securityApplicationService.findAll()) {
			List<AuthorizeBusinessGroupEntity> groups = authorizeBusinessGroupService
					.findBySecurityApplication(securityApplicationEntity);

			if (groups.isEmpty()) {
				AuthorizeBusinessGroupEntity group = new AuthorizeBusinessGroupEntity();
				group.setSecurityApplication(securityApplicationEntity);
				group.setBusinessGroup(randomGroups.get(rand.nextInt(randomGroups.size())));
				authorizeBusinessGroupService.save(group);
			}
		}
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Cargando datos iniciales...");

		createClients();
		createApps();
		createMethods();
		createSecurityEndpoints();
		createDummyEndpoints();
		createBusinessGroups();

		authSecurityMethods();
		authBusinessGroup();
		authEndpoint();
	}
}
