/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2021, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.qa.appsint.tests.eapamqstreams.metadata;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;

/**
 * Test servlet which can be used to invoke common JMS tasks in test classes.
 */
@WebServlet("/metadata")
public class MetadataTestServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(MetadataTestServlet.class.toString());

	@Inject
	BasicMetadataBean basicMetadataBean;

	private static final long TIMEOUT = 15000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter pw = resp.getWriter();
		pw.println("Starting BasicMetadataBean metadata servlet; waiting for data...");

		// Let's wait until bean receives all expected data.
		try {
			if (basicMetadataBean.getLatch().await(TIMEOUT, TimeUnit.MILLISECONDS)) {
				Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> map2 = basicMetadataBean
						.getTesting2Metadatas();
				Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> map3 = basicMetadataBean
						.getTesting3Metadatas();

				pw.println("Map 2 contains '" + map2.size() + "' items");
				pw.println("Map 3 contains '" + map3.size() + "' items");

				// Print the data, so we can check the content is correct.
				for (int i = 1; i <= 2; i++) {
					IncomingKafkaRecordMetadata metadata = map2.get(i);
					if (metadata != null) {
						pw.println("Map 2, data '" + i + "', topic '" + metadata.getTopic() + "', key '" + metadata.getKey()
								+ "', header '" + headersToString(metadata.getHeaders()) + "', timestamp '"
								+ metadata.getTimestamp() + "'");
					}
				}
				for (int i = 3; i <= 4; i++) {
					IncomingKafkaRecordMetadata metadata = map3.get(i);
					if (metadata != null) {
						pw.println("Map 3, data '" + i + "', topic '" + metadata.getTopic() + "', key '" + metadata.getKey()
								+ "', header '" + headersToString(metadata.getHeaders()) + "', timestamp '"
								+ metadata.getTimestamp() + "'");
					}
				}
			} else {
				pw.println("Timed out. BasicMetadataBean hasn't received all expected messages in time.");
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Wait interrupted...", e);
			pw.println(e.getMessage());
		}
		pw.close();
	}

	private String headersToString(Headers headers) {
		String result = "";

		if (headers != null) {
			for (Header header : headers) {
				result += header.key() + "=" + Arrays.toString(header.value());
			}
			return result;
		}

		return "null";
	}
}
