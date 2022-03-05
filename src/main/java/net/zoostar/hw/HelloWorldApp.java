package net.zoostar.hw;

import java.util.ArrayList;
import java.util.List;

import net.zoostar.hw.util.Generated;
import net.zoostar.hw.web.interceptor.GZIPClientHttpRequestInterceptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
@EntityScan(basePackages = {"net.zoostar.hw.entity"})
@EnableJpaRepositories(basePackages = {"net.zoostar.hw.repository"})
public class HelloWorldApp {
	
	@Generated
	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApp.class, args);
	}

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2).
				select().
				apis(RequestHandlerSelectors.any()).
				paths(PathSelectors.any()).
				build();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		var bean = new RestTemplate();
		bean.setInterceptors(interceptorBuilder());
		return bean;
	}

	private List<ClientHttpRequestInterceptor> interceptorBuilder() {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new GZIPClientHttpRequestInterceptor());
		return interceptors;
	}

}
