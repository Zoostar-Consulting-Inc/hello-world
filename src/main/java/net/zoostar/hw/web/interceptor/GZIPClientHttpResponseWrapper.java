package net.zoostar.hw.web.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import net.zoostar.hw.service.SourceEntityService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GZIPClientHttpResponseWrapper implements ClientHttpResponse {

	private final ClientHttpResponse response;
	
	public GZIPClientHttpResponseWrapper(ClientHttpResponse response) {
		this.response = response;
	}
	
	@Override
	public InputStream getBody() throws IOException {
		InputStream is = null;
		var value = response.getHeaders().getFirst(SourceEntityService.CONTENT_ENCODING);
		if(SourceEntityService.GZIP.equalsIgnoreCase(value)) {
			log.info("Received {}: {}", SourceEntityService.CONTENT_ENCODING, value);
			is = new GZIPInputStream(response.getBody());
		} else {
			is = response.getBody();
		}
		return is;
	}

	@Override
	public HttpHeaders getHeaders() {
		return response.getHeaders();
	}

	@Override
	public HttpStatus getStatusCode() throws IOException {
		return response.getStatusCode();
	}

	@Override
	public int getRawStatusCode() throws IOException {
		return response.getRawStatusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return response.getStatusText();
	}

	@Override
	public void close() {
		response.close();
	}

}
