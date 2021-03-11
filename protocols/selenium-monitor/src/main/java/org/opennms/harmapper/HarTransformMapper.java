/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.harmapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;

public class HarTransformMapper {

	private final String DEFAULT_JSTL_HAR_TRANSFORM_FILE = "hartransform-0-1.jslt";

	private final ObjectMapper mapper = new ObjectMapper();

	private final Expression jslt;

	public HarTransformMapper() {
		String jsltTransform;
		try {
			jsltTransform = getResourceFileAsString(DEFAULT_JSTL_HAR_TRANSFORM_FILE);
			jslt = Parser.compileString(jsltTransform);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public HarTransformMapper(String jsltTransform) {
		jslt = Parser.compileString(jsltTransform);
	};

	public HarTransformMapper(File jstlTransformFile) {
		jslt = Parser.compile(jstlTransformFile);
	};

	public JsonNode transform(String inputString, Object metaData)
			throws JsonMappingException, JsonProcessingException {
		JsonNode input = mapper.readTree(inputString);
		return transform(input, metaData);
	}

	public JsonNode transform(File inputFile, Object metaData) throws IOException {
		JsonNode input = mapper.readTree(inputFile);
		return transform(input, metaData);
	}

	public ArrayNode transform(JsonNode input, Object pollMetaData) throws JsonMappingException, JsonProcessingException {
		ArrayNode  output;

		// injecting values
		if (pollMetaData != null) {
			JsonNode jsonMetaData = mapper.convertValue(pollMetaData, JsonNode.class);
			Map<String, JsonNode> injectedValues = new LinkedHashMap<>();
			injectedValues.put("pollMetaData", jsonMetaData);
			output = (ArrayNode) jslt.apply(injectedValues, input);
		} else {
			output = (ArrayNode) jslt.apply(input);
		}
		return output;
	}

	private static String getResourceFileAsString(String fileName) throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName)) {
			if (is == null)
				return null;
			try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(isr)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}

}
