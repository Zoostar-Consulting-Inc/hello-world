package net.zoostar.hw.service;

import javax.xml.bind.ValidationException;

import net.zoostar.hw.util.Generated;

@Generated
public interface Validator<T> {
	
	default void validate() throws ValidationException{}
	
	default void validate(T object) throws ValidationException{}
}
