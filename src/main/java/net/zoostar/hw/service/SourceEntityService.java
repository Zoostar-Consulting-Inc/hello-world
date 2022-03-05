package net.zoostar.hw.service;

import net.zoostar.hw.entity.SourceEntity;
import net.zoostar.hw.entity.SourceEntityMapper;

import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;

public interface SourceEntityService<E extends SourceEntity<T>, P extends Persistable<T>, T>
extends EntityService<P, T> {
    
    String GZIP = "gzip";
    String CONTENT_ENCODING = "Content-Encoding";
	String ACCEPT_ENCODING = "Accept-Encoding";
	String PROTOCOL = "https://";

	E create(String sourceCode, String sourceId,
			String endPoint, Class<? extends SourceEntityMapper<E, T>> clazz);
	SourceEntityMapper<E, T> retrieve(String sourceCode, String sourceId,
			String endPoint, Class<? extends SourceEntityMapper<E, T>> clazz);
	ResponseEntity<E> update(E entity, String endPoint, Class<? extends SourceEntityMapper<E, T>> clazz);

}
