package mx.bs.cross.security.provisioning.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mx.bs.cross.db.commons.controllers.PersistentController;
import mx.bs.cross.security.db.model.SecurityApplicationSecretEntity;
import mx.bs.cross.security.db.services.SecurityApplicationSecretService;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class SecurityApplicationSecretController
		extends PersistentController<SecurityApplicationSecretService, SecurityApplicationSecretEntity> {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected SecurityApplicationSecretController(SecurityApplicationSecretService service) {
		super(service);

	}

	@Autowired
	private SecurityApplicationSecretService securityApplicationSecretService;

	@RequestMapping(value = "secrets", method = RequestMethod.GET)
	public ResponseEntity<List<SecurityApplicationSecretEntity>> getSecrets() {
		List<SecurityApplicationSecretEntity> secretEntity = new ArrayList<SecurityApplicationSecretEntity>();
		secretEntity = securityApplicationSecretService.findAll();
		return new ResponseEntity<List<SecurityApplicationSecretEntity>>(secretEntity, HttpStatus.OK);

	}

	@RequestMapping(value = "secrets/{secretId}", method = RequestMethod.GET)
	public ResponseEntity<SecurityApplicationSecretEntity> getSecret(@PathVariable String secretId) {
		SecurityApplicationSecretEntity secretEntity = new SecurityApplicationSecretEntity();
		secretEntity = securityApplicationSecretService.findById(secretId);
		return new ResponseEntity<SecurityApplicationSecretEntity>(secretEntity, HttpStatus.OK);
	}

	@RequestMapping(value = "applications/{securityApplicationId}/secrets", method = RequestMethod.GET)
	public ResponseEntity<SecurityApplicationSecretEntity> getSecretsBysecrurityApp(
			@PathVariable String securityApplicationId) {
		SecurityApplicationSecretEntity secretEntity = new SecurityApplicationSecretEntity();
		secretEntity = securityApplicationSecretService.findSecretById(securityApplicationId);
		return new ResponseEntity<SecurityApplicationSecretEntity>(secretEntity, HttpStatus.OK);
	}

	@RequestMapping(value = "/applications/{securityApplicationId}/secrets/{secretId}", method = RequestMethod.GET)
	public ResponseEntity<SecurityApplicationSecretEntity> getBySecurityAppAndSecretId(
			@PathVariable String securityApplicationId, @PathVariable String secretId) {
		SecurityApplicationSecretEntity secretEntity = new SecurityApplicationSecretEntity();
		secretEntity = securityApplicationSecretService.findSecretById(secretId);
		return new ResponseEntity<SecurityApplicationSecretEntity>(secretEntity, HttpStatus.OK);
	}

	@RequestMapping(value = "applications/{securityApplicationId}/secrets", method = RequestMethod.POST)
	public ResponseEntity<SecurityApplicationSecretEntity> saveSecret(
			@RequestBody SecurityApplicationSecretEntity request) {
		SecurityApplicationSecretEntity secretEntity = new SecurityApplicationSecretEntity();
		secretEntity = securityApplicationSecretService.save(request);
		return new ResponseEntity<SecurityApplicationSecretEntity>(secretEntity, HttpStatus.OK);
	}

	@RequestMapping(value = "applications/{securityApplicationId}/secrets", method = RequestMethod.PUT)
	public ResponseEntity<SecurityApplicationSecretEntity> updateSecret(@PathVariable String secretId,
			@RequestBody SecurityApplicationSecretEntity request) {
		SecurityApplicationSecretEntity secretEntity = new SecurityApplicationSecretEntity();
		secretEntity = securityApplicationSecretService.findById(secretId);
		secretEntity.setActive(request.getActive());
		securityApplicationSecretService.save(secretEntity);
		return new ResponseEntity<SecurityApplicationSecretEntity>(secretEntity, HttpStatus.OK);
	}

	@RequestMapping(value = "applications/{securityApplicationId}/secrets", method = RequestMethod.DELETE)
	public ResponseEntity<SecurityApplicationSecretEntity> deleteSecret(@PathVariable String secretId) {
		SecurityApplicationSecretEntity secretEntity = new SecurityApplicationSecretEntity();
		secretEntity = securityApplicationSecretService.findById(secretId);
		secretEntity.setActive(false);
		securityApplicationSecretService.save(secretEntity);
		return new ResponseEntity<SecurityApplicationSecretEntity>(secretEntity, HttpStatus.OK);
	}
}
