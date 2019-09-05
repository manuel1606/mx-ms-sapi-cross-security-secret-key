package mx.bs.cross.security.provisioning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.bs.cross.db.commons.controllers.PersistentController;
import mx.bs.cross.security.db.model.SecurityMethodEntity;
import mx.bs.cross.security.db.services.SecurityMethodService;


@RestController
@RequestMapping("/securitymethod")
@CrossOrigin(origins = "*")
public class SecurityMethodController extends PersistentController<SecurityMethodService, SecurityMethodEntity> {

	@Autowired
	protected SecurityMethodController(SecurityMethodService service) {
		super(service);
	}
	
	
}
