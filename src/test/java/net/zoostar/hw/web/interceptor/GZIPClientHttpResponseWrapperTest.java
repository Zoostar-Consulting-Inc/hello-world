package net.zoostar.hw.web.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import net.zoostar.hw.service.SourceEntityService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
class GZIPClientHttpResponseWrapperTest {
	
	@Value("${repositories.path:data}")
	String path;

	@Test
	void testGetUnzippedBody() throws IOException {
		ClientHttpRequestInterceptor interceptor = new GZIPClientHttpRequestInterceptor();
		String expected = "Hello World";
		byte[] body = expected.getBytes();
		
		ClientHttpRequestExecution execution = new ClientHttpRequestExecution() {
			
			@Override
			public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
				return new GZIPClientHttpResponseWrapper(new MockClientHttpResponse(body, HttpStatus.OK));
			}
		
		};
		
		try(ClientHttpResponse response = interceptor.intercept(new MockClientHttpRequest(), body, execution)) {
			var wrapper = new GZIPClientHttpResponseWrapper(response);
			assertThat(wrapper).isNotNull();
			assertThat(wrapper.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(wrapper.getRawStatusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(wrapper.getStatusText()).isEqualTo("OK");
			try(InputStream is = wrapper.getBody()) {
				assertThat(is).isNotNull();
				var actual = new String(is.readAllBytes());
				assertThat(actual).isEqualTo(expected);
			}
		}
	}

	@Test
	void testGetZippedBody() throws IOException {
		String expected = "Zipped Hello World";
		File file = File.createTempFile("product", ".zip");
		try (OutputStream os = new GZIPOutputStream(new FileOutputStream(file))) {
			os.write(expected.getBytes());
		}
		log.info("Zipped content written to file: {}", file.getName());
		
		try(InputStream fis = new FileInputStream(file)) {
			ClientHttpResponse response = new MockClientHttpResponse(fis, HttpStatus.OK);
			response.getHeaders().add(SourceEntityService.CONTENT_ENCODING, SourceEntityService.GZIP);
			try(var wrapper = new GZIPClientHttpResponseWrapper(response)) {
				assertThat(wrapper.getStatusCode()).isEqualTo(HttpStatus.OK);
				try(InputStream is = wrapper.getBody()) {
					assertThat(is).isNotNull();
					var actual = new String(is.readAllBytes());
					assertThat(actual).isEqualTo(expected);
					assertThat(actual.length() > 0).isTrue();
					log.info("Actual: {}", actual);
				}
			}
		}
	}

}
