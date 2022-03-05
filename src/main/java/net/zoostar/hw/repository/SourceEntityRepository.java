package net.zoostar.hw.repository;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface SourceEntityRepository<E, T> extends PagingAndSortingRepository<E, T> {
	Optional<E> findBySourceCodeAndId(String sourceCode, String sourceId);
}
