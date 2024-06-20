package com.example.demo.model.response;

import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class GenericTypeReference<T> extends TypeReference<List<T>> {

	private final Class<T> elementType;

	public GenericTypeReference(Class<T> elementType) {
		this.elementType = elementType;
	}

	@Override
	public Type getType() {
		return TypeFactory.defaultInstance().constructCollectionType(List.class, elementType);
	}

}
