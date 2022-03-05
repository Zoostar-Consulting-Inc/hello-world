package net.zoostar.hw.web.api;

import javax.persistence.EntityNotFoundException;

import net.zoostar.hw.entity.SourceEntity;
import net.zoostar.hw.entity.SourceEntityMapper;
import net.zoostar.hw.service.EntityService;
import net.zoostar.hw.service.SourceEntityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

public abstract class AbstractSourceApi<E extends SourceEntity<T>, T> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Getter
	@Autowired
	private EntityService<E, T> entityManager;

	@Getter
	@Autowired
	private SourceEntityService<E, T> sourceEntityManager;

	protected abstract String getEndPoint();
	
	protected abstract Class<? extends SourceEntityMapper<E, T>> getEntityMapperClazz();
	
	protected ResponseEntity<E> cudOperation(String sourceCode, String sourceId) {
		sourceCode = sanitize(sourceCode);
		sourceId = sanitize(sourceId);
		ResponseEntity<E> response = null;
		try {
			var entity = getEntityManager().retrieve(sourceCode, sourceId);
			log.info("Entity retrieved: {}", entity);
			response = sourceEntityManager.update(entity, getEndPoint(), getEntityMapperClazz());
		} catch(EntityNotFoundException e) {
			log.info("{}", e.getMessage());
			response = new ResponseEntity<>(getSourceEntityManager().create(
					sourceCode, sourceId, getEndPoint(), getEntityMapperClazz()), HttpStatus.CREATED);
		}
		log.info("Response: {}", response);
		return response;
	}

	private String sanitize(String dirty) {
		return dirty.replaceAll("[\n\r\t]", "_");
	}
}
