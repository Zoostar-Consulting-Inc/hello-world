package net.zoostar.hw.service.impl;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;

import net.zoostar.hw.entity.Source;
import net.zoostar.hw.entity.SourceEntity;
import net.zoostar.hw.entity.SourceEntityMapper;
import net.zoostar.hw.repository.SourceRepository;
import net.zoostar.hw.service.EntityService;
import net.zoostar.hw.service.SourceEntityService;
import net.zoostar.hw.service.Validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SourceServiceImpl<E extends SourceEntity<T>, T>
implements SourceEntityService<E, T>, InitializingBean {

    @Autowired
	protected SourceRepository repository;
	
	@Getter
	@Autowired
	protected EntityService<E, T> entityManager;
	
	@Autowired
	protected RestTemplate api;
	
	@Autowired
	protected Validator<Source> requiredFieldValidator;
	
	protected HttpHeaders headers;

	@Override
	@Transactional(readOnly = true)
	public Source retrieve(String sourceCode) {
		log.info("Find Entity by Source Code: {}...", sourceCode);
		var entity = repository.findBySourceCode(sourceCode);
		if(entity.isEmpty()) {
			throw new EntityNotFoundException(String.format("No entity found for source code %s", sourceCode));
		}
		return entity.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public SourceEntityMapper<E, T> retrieve(String sourceCode, String sourceId,
			String endPoint, Class<? extends SourceEntityMapper<E, T>> clazz) {
		Source source = retrieve(sourceCode);
		log.info("Retrieved Entity from Repository: {}", source);
		String url = new StringBuilder(PROTOCOL).append(source.getBaseUrl()).
				append(endPoint).append("?id=").append(sourceId).toString();
		log.info("Fetching entity from source: {}...", url);
		var response = api.getForEntity(url, clazz, headers);
		if(response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
			throw new EntityNotFoundException(String.format("No entity found for source code %s and source id %s", sourceCode, sourceId));
		}
		log.info("Retrieved response from Source: {}", response);
		return response.getBody();
	}

	@Override
	public E create(String sourceCode, String sourceId,
			String endPoint, Class<? extends SourceEntityMapper<E, T>> clazz) {
		var persistable = retrieve(sourceCode, sourceId, endPoint, clazz);
		log.info("Retrieved Entity from Source: {}", persistable);
		return getEntityManager().create(persistable.toEntity());
	}

	@Override
	public ResponseEntity<E> update(E entity, String endPoint, Class<? extends SourceEntityMapper<E, T>> clazz) {
		ResponseEntity<E> response = null;
		try {
			var persistable = retrieve(entity.getSourceCode(), entity.getSourceId(), endPoint, clazz);
			log.info("Retrieved Entity from Source: {}", persistable);
			response = new ResponseEntity<>(getEntityManager().update(entity, persistable.toEntity()), HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			log.info("{}", "Deleting Entity not found in source...");
			getEntityManager().delete(entity.getId());
			response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return response;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initializeHttpHeaders();
	}

	protected void initializeHttpHeaders() {
		headers = new HttpHeaders();
		headers.add(ACCEPT_ENCODING, GZIP);
		log.debug("Http Headers initialized: {}", headers);
	}

	@Override
	public Source create(Source source) {
		log.info("Saving new entity: {}...", source);
		var entity = repository.findBySourceCode(source.getSourceCode());
		if(entity.isPresent()) {
			throw new EntityExistsException("Existing entity found for create: " + entity);
		}
		return repository.save(source);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Source> retrieve(int pageNum, int pageSize) {
		log.info("Retrieving a max of {} records for page {}...", pageSize, pageNum);
		return repository.findAll(PageRequest.of(pageNum, pageSize));
	}

	@Override
	public Source update(Source persistable) throws ValidationException {
		validate(persistable);
		var entity = repository.findBySourceCode(persistable.getSourceCode());
		if(entity.isEmpty()) {
			throw new EntityNotFoundException("No existing entity found for update: " + persistable.toString());
		}
		persistable.setId(entity.get().getId());
		return repository.save(persistable);
	}

	protected void validate(Source persistable) throws ValidationException {
		requiredFieldValidator.validate(persistable);
	}

}
