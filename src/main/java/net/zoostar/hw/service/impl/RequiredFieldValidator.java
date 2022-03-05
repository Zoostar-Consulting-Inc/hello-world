package net.zoostar.hw.service.impl;

import javax.xml.bind.ValidationException;

import net.zoostar.hw.entity.Source;
import net.zoostar.hw.service.Validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequiredFieldValidator implements Validator<Source> {

	@Override
	public void validate(Source source) throws ValidationException {
		log.info("Validating: {}...", source);
		if(source == null || !StringUtils.hasText(source.getSourceCode())) {
			throw new ValidationException("Missing required field: sourceCode");
		}
	}

}
