package net.zoostar.hw.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Optional;
import java.util.UUID;

import net.zoostar.hw.AbstractHelloWorldTestHarness;
import net.zoostar.hw.entity.Product;
import net.zoostar.hw.repository.EntityRepository;
import net.zoostar.hw.web.request.ProductRequest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class ProductApiTest extends AbstractHelloWorldTestHarness<EntityRepository<Product, String>, Product, String> {

	@Test
	void testCreate() throws Exception {
		//given
		var request = toProductRequest("source", "sourceId");
		String url = "/api/product/update/" + request.getSource() + "?sourceId=" + request.getSourceId();
		
		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(repository.save(request.toEntity())).
				thenReturn(entity);
		
		when(sourceManager.create(request.getSource(), request.getSourceId(), ProductRequest.class)).
				thenReturn(entity);
		
		var result = api.perform(get(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request))).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		log.debug("Result: {}", result);
		
		var response = result.getResponse();
		assertThat(response).isNotNull();
		log.debug("Response status: {}", response.getStatus());
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		
		var actual = mapper.readValue(response.getContentAsString(), Product.class);
		assertThat(actual).isNotNull();
		log.info("Created entity: {}.", actual);
		
		var duplicate = actual;
		assertThat(duplicate).isEqualTo(actual);
		assertThat(actual.getClass()).isEqualTo(entity.getClass());
		assertThat(actual).hasSameHashCodeAs(entity);
		assertThat(actual.getId()).isEqualTo(entity.getId());
		assertThat(actual.getName()).isEqualTo(entity.getName());
		assertThat(actual.getSku()).isEqualTo(entity.getSku());
		assertThat(actual.getSource()).isEqualTo(entity.getSource());
		assertThat(actual.getSourceId()).isEqualTo(entity.getSourceId());
		assertThat(actual.isNew()).isFalse();
		assertThat(request.toEntity().isNew()).isTrue();
		
		entity.setSourceId(actual.getSourceId().substring(0, actual.getSourceId().length()-1));
		assertThat(actual).isNotEqualTo(entity);

		entity.setSourceId(actual.getSourceId());
		entity.setSource(actual.getSource().substring(0, actual.getSource().length()-1));
		assertThat(actual).isNotEqualTo(entity);
	}

	@Test
	void testUpdate() throws Exception {
		//given
		var request = toProductRequest("source", "sourceId");
		String url = "/api/product/update/" + request.getSource() + "?sourceId=" + request.getSourceId();
		
		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(repository.findBySourceCodeAndId(request.getSource(), request.getSourceId())).
				thenReturn(Optional.of(entity));
		
		var persistable = toProductRequest(request.getSource(), request.getSourceId());
		persistable.setDesc(persistable.getDesc() + "_update");
		var updatedEntity = persistable.toEntity();
		updatedEntity.setId(entity.getId());
		when(sourceManager.update(entity)).
				thenReturn(new ResponseEntity<>(updatedEntity, HttpStatus.OK));
		
		var result = api.perform(get(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request))).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		log.debug("Result: {}", result);
		
		var response = result.getResponse();
		assertThat(response).isNotNull();
		log.debug("Response status: {}", response.getStatus());
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
		var actual = mapper.readValue(response.getContentAsString(), Product.class);
		assertThat(actual).isNotNull();
		log.info("Updated entity: {}.", actual);
		
		var duplicate = actual;
		assertThat(duplicate).isEqualTo(actual);
		assertThat(actual.getClass()).isEqualTo(updatedEntity.getClass());
		assertThat(actual).hasSameHashCodeAs(updatedEntity);
		assertThat(actual.getId()).isEqualTo(updatedEntity.getId());
		assertThat(actual.getName()).isEqualTo(updatedEntity.getName());
		assertThat(actual.getSku()).isEqualTo(updatedEntity.getSku());
		assertThat(actual.getSource()).isEqualTo(updatedEntity.getSource());
		assertThat(actual.getSourceId()).isEqualTo(updatedEntity.getSourceId());
		assertThat(actual.isNew()).isFalse();
		assertThat(request.toEntity().isNew()).isTrue();
	}

	@Test
	void testDelete() throws Exception {
		//given
		var request = toProductRequest("source", "sourceId");
		String url = "/api/product/update/" + request.getSource() + "?sourceId=" + request.getSourceId();
		
		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(repository.findBySourceCodeAndId(request.getSource(), request.getSourceId())).
				thenReturn(Optional.of(entity));
		
		var persistable = toProductRequest(request.getSource(), request.getSourceId());
		persistable.setDesc(persistable.getDesc() + "_update");
		var updatedEntity = persistable.toEntity();
		updatedEntity.setId(entity.getId());
		when(sourceManager.update(entity)).
				thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
		
		var result = api.perform(get(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request))).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		log.debug("Result: {}", result);
		
		var response = result.getResponse();
		assertThat(response).isNotNull();
		log.debug("Response status: {}", response.getStatus());
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	protected ProductRequest toProductRequest(String sourceCode, String sourceId) {
		var request = new ProductRequest();
		request.setDesc("This is a product description");
		request.setSku("SKU");
		request.setSource(sourceCode);
		request.setSourceId(sourceId);
		log.info("Created ProductMapper: {}", request.toString());
		return request;
	}

}
