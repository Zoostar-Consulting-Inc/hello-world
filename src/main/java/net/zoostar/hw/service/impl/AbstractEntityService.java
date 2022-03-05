package net.zoostar.hw.service.impl;

import javax.persistence.EntityExistsException;

import net.zoostar.hw.service.EntityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractEntityService<P extends Persistable<T>, T> implements EntityService<P, T> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected abstract PagingAndSortingRepository<P, T> getEntityRepository();

	protected abstract void validate(P persistable);
	
	public P create(P persistable) {
		log.info("Creating new entity from persistable: {}...", persistable);
		if(!persistable.isNew()) {
			throw new EntityExistsException("Provided persistable object for creation is not new!");
		}
		validate(persistable);
		return getEntityRepository().save(persistable);
	}
}
