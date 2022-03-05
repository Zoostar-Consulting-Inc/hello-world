package net.zoostar.hw.web.api;

import javax.persistence.EntityExistsException;

import net.zoostar.hw.entity.PersistableEntityMapper;
import net.zoostar.hw.service.EntityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractPersistableApi<E extends Persistable<T>, T> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected abstract EntityService<E, T> getEntityManager();

	protected ResponseEntity<E> create(PersistableEntityMapper<E, T> request) {
		log.info("Request received to create new entity: {}", request);
		ResponseEntity<E> response = null;
		try {
			response = new ResponseEntity<>(
					getEntityManager().create(request.toEntity()), HttpStatus.CREATED);
		} catch(EntityExistsException e) {
			log.warn(e.getMessage());
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	private String stringSanitizerAndLogger(String dirty) {
		String sanitized = dirty.replaceAll("[\n\r\t]", "_");
		log.info("Request Param: {}", sanitized);
		return sanitized;
	}

}
