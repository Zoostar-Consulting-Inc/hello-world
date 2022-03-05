package net.zoostar.hw.service;

import org.springframework.data.domain.Persistable;

public interface EntityService<P extends Persistable<T>, T> {
	P create(P persistable);
	P retrieveByKey(P persistable);
	P update(P persistable);
	void delete(T id);
}
