package net.zoostar.hw.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.zoostar.hw.AbstractMockTestHarness;
import net.zoostar.hw.entity.Product;
import net.zoostar.hw.entity.Source;
import net.zoostar.hw.service.SourceEntityService;
import net.zoostar.hw.web.request.SourceRequest;
import net.zoostar.hw.web.response.PageResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;

class SourceApiTest extends AbstractMockTestHarness<Source, String> {

	private static final int PAGE_LIMIT = 3;
	
	@Autowired
	protected SourceEntityService<Product, String> sourceManager;
	
	@Test
	void testCreate() throws Exception {
		//given
		var request = new SourceRequest(toSource("source"));
		String url = "/api/source/create";

		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(sourceRepository.save(request.toEntity())).
				thenReturn(entity);
		
		var result = service.perform(MockMvcRequestBuilders.post(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request)).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		
		var actual = mapper.readValue(response.getContentAsByteArray(), Source.class);
		assertThat(actual).isNotNull();
		assertThat(actual.getBaseUrl()).isEqualTo(request.getBaseUrl());
		assertThat(actual.getClass()).isNotEqualTo(request.getClass());
		assertThat(actual.getClass()).isEqualTo(entity.getClass());
		assertThat(actual.getId()).isEqualTo(entity.getId());
		assertThat(actual.getName()).isEqualTo(request.getName());
		assertThat(actual.getSourceCode()).isEqualTo(request.getSourceCode());
		
		var duplicate = actual;
		assertThat(duplicate).isEqualTo(actual);
	}
	
	@Test
	void testCreateThrowingEntityExistsException() throws Exception {
		//given
		var request = new SourceRequest(toSource("source"));
		String url = "/api/source/create";

		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(sourceRepository.findBySourceCode(request.getSourceCode())).
				thenReturn(Optional.of(entity));
		
		var result = service.perform(MockMvcRequestBuilders.post(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request)).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	void testRetrieveThrowsEntityNotFoundException() throws Exception {
		//given
		var request = new SourceRequest(toSource("source"));
		String url = "/api/source/retrieve";

		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		when(sourceRepository.findBySourceCode(request.getSourceCode())).
				thenReturn(Optional.of(entity));
		
		var result = service.perform(MockMvcRequestBuilders.post(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request)).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	void testRetrieve() throws Exception {
		int pageNum = 0;
		testRetrieveByFirstPage(pageNum++);
		testRetrieveByNextPage(pageNum++);
		testRetrieveByLastPage(pageNum++);
	}

	@Test
	void testUpdate() throws Exception {
		//given
		String nameBefore = "Name Before";
		String nameAfter = "Name After";
		var request = new SourceRequest(toSource("source"));
		request.setName(nameAfter);
		String url = "/api/source/update";

		//mock-when
		var entity = request.toEntity();
		entity.setId(UUID.randomUUID().toString());
		entity.setName(nameBefore);
		
		var expected = request.toEntity();
		expected.setId(entity.getId());
		
		when(sourceRepository.findBySourceCode(request.getSourceCode())).
				thenReturn(Optional.of(entity));
		when(sourceRepository.save(expected)).
				thenReturn(expected);
		
		var result = service.perform(MockMvcRequestBuilders.post(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request)).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
		var actual = mapper.readValue(response.getContentAsByteArray(), Source.class);
		assertThat(actual).isNotNull();
		assertThat(actual.getBaseUrl()).isEqualTo(request.getBaseUrl());
		assertThat(actual.getClass()).isNotEqualTo(request.getClass());
		assertThat(actual.getClass()).isEqualTo(entity.getClass());
		assertThat(actual.getId()).isEqualTo(entity.getId());
		assertThat(actual.getSourceCode()).isEqualTo(request.getSourceCode());
		assertThat(actual.getName()).isEqualTo(nameAfter);
	}
	
	@Test
	void testUpdateThrowingNoEntityFoundException() throws Exception {
		//given
		var request = new SourceRequest(toSource("source"));
		String url = "/api/source/update";

		//mock-when
		when(sourceRepository.findBySourceCode(request.getSourceCode())).
				thenReturn(Optional.empty());
		
		var result = service.perform(MockMvcRequestBuilders.post(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request)).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	private void testRetrieveByPage(int pageNum, int pageSize) throws Exception {
		var url = new StringBuilder("/api/source/retrieve/").
				append(pageNum).append("?size=").append(pageSize);
		
		//mock-when
		var request = PageRequest.of(pageNum, pageSize);
		var page = page(records, request);
		when(sourceRepository.findAll(request)).
				thenReturn(page);
		
		var result = service.perform(MockMvcRequestBuilders.get(url.toString()).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
		PageResponse<Source> actual = mapper.readValue(response.getContentAsString(),
				mapper.constructType(getPageTypeReference()));
		assertThat(actual).isNotNull();
		assertThat(actual.getNumberOfElements()).isEqualTo(page.getNumberOfElements());
		assertThat(actual.getPageNum()).isEqualTo(pageNum);
		assertThat(actual.getSize()).isEqualTo(pageSize);
		assertThat(actual.getTotalElements()).isEqualTo(records.size());
		assertThat(actual.getTotalPages()).isEqualTo(
				(records.size() / pageSize) + (records.size() % pageSize > 0 ? 1 : 0));
		List<Source> contents = actual.getContents();
		int offset = pageNum * pageSize;
		for(int i = 0; i < page.getNumberOfElements(); i++) {
			var source = contents.get(i);
			assertThat(source).isEqualTo(records.get(offset + i));
			log.info("Source: {}", source.toString());
		}
	}
	
	@Test
	void testUpdateThrowingRequiredFieldValidationException() throws Exception {
		//given
		var request = new SourceRequest(toSource(""));
		String url = "/api/source/update";

		//mock-when
		var result = service.perform(MockMvcRequestBuilders.post(url).
				contentType(MediaType.APPLICATION_JSON).
				content(mapper.writeValueAsString(request)).
				accept(MediaType.APPLICATION_JSON)).
				andReturn();
		
		//then
		assertThat(result).isNotNull();
		var response = result.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		log.warn("{}", response.getErrorMessage());
	}
	
	private void testRetrieveByFirstPage(int pageNum) throws Exception {
		testRetrieveByPage(pageNum, PAGE_LIMIT);
	}
	
	private void testRetrieveByNextPage(int pageNum) throws Exception {
		testRetrieveByPage(pageNum, PAGE_LIMIT);
	}

	private void testRetrieveByLastPage(int pageNum) throws Exception {
		testRetrieveByPage(pageNum, PAGE_LIMIT);		
	}

	@Override
	protected String getResourcePath() {
		String resourcePath = "data/sources.json";
		log.info("Loading records from resource path: {}", resourcePath);
		return resourcePath;
	}

	@Override
	protected TypeReference<List<Source>> getListTypeReference() {
		return new TypeReference<>() {};
	}

	protected TypeReference<PageResponse<Source>> getPageTypeReference() {
		return new TypeReference<>() {};
	}
}