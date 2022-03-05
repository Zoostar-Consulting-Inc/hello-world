package net.zoostar.hw.web.api;

import static net.zoostar.hw.service.SourceEntityService.PROTOCOL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.zoostar.hw.AbstractMockTestHarness;
import net.zoostar.hw.entity.Product;
import net.zoostar.hw.entity.Source;
import net.zoostar.hw.service.SourceEntityService;
import net.zoostar.hw.web.request.ProductRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;

class ProductApiTest extends AbstractMockTestHarness<Product, String> {
	
	@Autowired
	protected SourceEntityService<Product, String> sourceManager;
	
	@Test
	void testCreate() throws Exception {
		//given
		var request = toProductRequest("source", "sourceId");
		String url = "/api/product/update/" + request.getSource() + "?sourceId=" + request.getSourceId();
		
		//mock-when
		Source source = toSource(request.getSource());
		assertThat(source.isNew()).isFalse();
		assertThat(source.hashCode()).isNotZero();
		
		when(sourceRepository.findBySourceCode(request.getSource())).
				thenReturn(Optional.of(source));
		
		var headers = new HttpHeaders();
		headers.add(SourceEntityService.CONTENT_ENCODING, SourceEntityService.GZIP);
		when(rest.getForEntity(Mockito.eq(PROTOCOL + source.getBaseUrl() + "/product/retrieve?id=" + request.getSourceId()),
				Mockito.eq(ProductRequest.class), Mockito.any(HttpHeaders.class))).thenReturn(new ResponseEntity<>(request, HttpStatus.OK));
		
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(repository.save(request.toEntity())).
				thenReturn(entity);
		
		var result = service.perform(get(url).
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
		assertThat(actual.getSourceCode()).isEqualTo(entity.getSourceCode());
		assertThat(actual.getSourceId()).isEqualTo(entity.getSourceId());
		assertThat(actual.isNew()).isFalse();
		assertThat(request.toEntity().isNew()).isTrue();
		
		entity.setSourceId(actual.getSourceId().substring(0, actual.getSourceId().length()-1));
		assertThat(actual).isNotEqualTo(entity);

		entity.setSourceId(actual.getSourceId());
		entity.setSourceCode(actual.getSourceCode().substring(0, actual.getSourceCode().length()-1));
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

		var source = toSource(request.getSource());
		assertThat(source.isNew()).isFalse();
		when(sourceRepository.findBySourceCode(request.getSource())).
				thenReturn(Optional.of(source));
	
		var persistable = toProductRequest(request.getSource(), request.getSourceId());
		persistable.setDesc(persistable.getDesc() + "_update");
		when(rest.getForEntity(Mockito.eq(PROTOCOL + source.getBaseUrl() + "/product/retrieve?id=" + request.getSourceId()),
				Mockito.eq(ProductRequest.class), Mockito.any(HttpHeaders.class))).thenReturn(new ResponseEntity<>(persistable, HttpStatus.OK));
		
		var updatedEntity = persistable.toEntity();
		updatedEntity.setId(entity.getId());
		when(repository.save(updatedEntity)).
				thenReturn(updatedEntity);
		
		var result = service.perform(get(url).
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
		assertThat(actual.getSourceCode()).isEqualTo(updatedEntity.getSourceCode());
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

		var source = toSource(request.getSource());
		assertThat(source.isNew()).isFalse();
		when(sourceRepository.findBySourceCode(request.getSource())).
				thenReturn(Optional.of(source));
	
		when(rest.getForEntity(Mockito.eq(PROTOCOL + source.getBaseUrl() + "/product/retrieve?id=" + request.getSourceId()),
				Mockito.eq(ProductRequest.class), Mockito.any(HttpHeaders.class))).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
		
		var result = service.perform(get(url).
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

	@Override
	protected String getResourcePath() {
		String resourcePath = "data/products.json";
		log.info("Loading records from resource path: {}", resourcePath);
		return resourcePath;
	}

	@Override
	protected TypeReference<List<Product>> getListTypeReference() {
		return new TypeReference<>() {};
	}
	
}
