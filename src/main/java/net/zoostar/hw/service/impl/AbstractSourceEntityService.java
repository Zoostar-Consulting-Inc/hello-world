package net.zoostar.hw.service.impl;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import net.zoostar.hw.entity.SourceEntity;
import net.zoostar.hw.repository.SourceEntityRepository;
import net.zoostar.hw.service.EntityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractSourceEntityService<P extends SourceEntity<T>, T>
implements EntityService<P, T> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public abstract SourceEntityRepository<P, T> getRepository();
	
	@Override
	public P create(P entity) {
		log.info("Persisting entity: {}...", entity);
		return getRepository().save(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public P retrieve(String sourceCode, String sourceId) {
		log.info("Find Entity by Source Code: {} and Source ID: {}", sourceCode, sourceId);
		Optional<P> optional = getRepository().findBySourceCodeAndId(sourceCode, sourceId);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException(String.format("No Entity found for sourceCode: %s and sourceId: %s", sourceCode, sourceId));
		}
		return optional.get();
	}

	@Override
	public P update(P entity, P persistable) {
		log.info("Updating {} with ID: {}", persistable, entity.getId());
		persistable.setId(entity.getId());
		return getRepository().save(persistable);
	}

	@Override
	public void delete(T id) {
		log.info("Deleting entity for id: {}...", id);
		getRepository().deleteById(id);
	}

}
