/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.core.web.converter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Mapping Request Modify Response Jackson2 Http Message Converter
 *
 * @author kyleju
 */
public class MappingRequestModifyResponseJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
	private final DefaultPrettyPrinter ssePrettyPrinter;
	private ObjectMapper responseObjectMapper;

	public MappingRequestModifyResponseJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		super(objectMapper);
		initResponseObjectMapper(objectMapper);

		DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
		prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
		this.ssePrettyPrinter = prettyPrinter;
	}

	private void initResponseObjectMapper(ObjectMapper objectMapper) {
		ObjectMapper objectMapperResponse = objectMapper.copy();
		objectMapperResponse.setSerializerFactory(objectMapperResponse.getSerializerFactory().withSerializerModifier(new NezhaBeanSerializerModifier()));
		objectMapperResponse.getSerializerProvider().setNullValueSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
		this.responseObjectMapper = objectMapperResponse;
	}


	@Override
	public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
		if (!canWrite(mediaType)) {
			return false;
		}
		AtomicReference<Throwable> causeRef = new AtomicReference<>();
		if (this.objectMapper.canSerialize(clazz, causeRef)) {
			return true;
		}
		logWarningIfNecessary(clazz, causeRef.get());
		return false;
	}

	@Override
	protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage)
		throws IOException, HttpMessageNotWritableException {

		MediaType contentType = outputMessage.getHeaders().getContentType();
		JsonEncoding encoding = getJsonEncoding(contentType);
		JsonGenerator generator = this.responseObjectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
		try {
			writePrefix(generator, object);

			Object value = object;
			Class<?> serializationView = null;
			FilterProvider filters = null;
			JavaType javaType = null;

			if (object instanceof MappingJacksonValue) {
				MappingJacksonValue container = (MappingJacksonValue) object;
				value = container.getValue();
				serializationView = container.getSerializationView();
				filters = container.getFilters();
			}

			if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
				javaType = getJavaType(type, null);
			}

			ObjectWriter objectWriter = (serializationView != null ?
				this.responseObjectMapper.writerWithView(serializationView) : this.responseObjectMapper.writer());
			if (filters != null) {
				objectWriter = objectWriter.with(filters);
			}
			if (javaType != null && javaType.isContainerType()) {
				objectWriter = objectWriter.forType(javaType);
			}
			SerializationConfig config = objectWriter.getConfig();
			if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
				config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
				objectWriter = objectWriter.with(this.ssePrettyPrinter);
			}
			objectWriter.writeValue(generator, value);

			writeSuffix(generator, object);
			generator.flush();
		} catch (InvalidDefinitionException ex) {
			throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
		}
	}


	public static class NezhaBeanSerializerModifier extends BeanSerializerModifier {
		@Override
		public List<BeanPropertyWriter> changeProperties(
			SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties) {
			// get beanPropertyWriter
			beanProperties.forEach(writer -> {
				// if have null serializer @JsonSerialize(nullsUsing) return;
				if (writer.hasNullSerializer()) {
					return;
				}
				JavaType type = writer.getType();
				Class<?> clazz = type.getRawClass();
				if (type.isTypeOrSubTypeOf(Number.class)) {
					writer.assignNullSerializer(NullJsonSerializers.NUMBER_JSON_SERIALIZER);
				} else if (type.isTypeOrSubTypeOf(Boolean.class)) {
					writer.assignNullSerializer(NullJsonSerializers.BOOLEAN_JSON_SERIALIZER);
				} else if (type.isTypeOrSubTypeOf(Character.class)) {
					writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
				} else if (type.isTypeOrSubTypeOf(String.class)) {
					writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
				} else if (type.isArrayType() || clazz.isArray() || type.isTypeOrSubTypeOf(Collection.class)) {
					writer.assignNullSerializer(NullJsonSerializers.ARRAY_JSON_SERIALIZER);
				} else if (type.isTypeOrSubTypeOf(OffsetDateTime.class)) {
					writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
				} else if (type.isTypeOrSubTypeOf(Date.class) || type.isTypeOrSubTypeOf(TemporalAccessor.class)) {
					writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
				} else {
					writer.assignNullSerializer(NullJsonSerializers.OBJECT_JSON_SERIALIZER);
				}
			});
			return super.changeProperties(config, beanDesc, beanProperties);
		}
	}

	public static class NullJsonSerializers {

		public static JsonSerializer<Object> STRING_JSON_SERIALIZER = new JsonSerializer<Object>() {
			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeString(StrUtil.EMPTY);
			}
		};

		public static JsonSerializer<Object> NUMBER_JSON_SERIALIZER = new JsonSerializer<Object>() {
			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeNumber(StrUtil.INDEX_NOT_FOUND);
			}
		};

		public static JsonSerializer<Object> BOOLEAN_JSON_SERIALIZER = new JsonSerializer<Object>() {
			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeObject(Boolean.FALSE);
			}
		};

		public static JsonSerializer<Object> ARRAY_JSON_SERIALIZER = new JsonSerializer<Object>() {
			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeStartArray();
				gen.writeEndArray();
			}
		};

		public static final JsonSerializer<Object> OBJECT_JSON_SERIALIZER = new JsonSerializer<Object>() {
			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeStartObject();
				gen.writeEndObject();
			}
		};

	}
}
