package net.zoostar.hw.entity;

import org.springframework.data.domain.Persistable;

public interface PersistableEntityMapper<P extends Persistable<T>, T> {
	P toEntity();
}
