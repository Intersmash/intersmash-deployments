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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;

/**
 * Test servlet which can be used to invoke common JMS tasks in test classes.
 */
@WebServlet("/partitionsMetadata")
public class SpecifyPartitionMetadataTestServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SpecifyPartitionMetadataTestServlet.class.toString());

	@Inject
	SpecifyPartitionBean specifyPartitionBean;

	private static final long TIMEOUT = 15000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter pw = resp.getWriter();
		pw.println("Starting SpecifyPartitionBean metadata servlet; waiting for data...");

		// Let's wait until bean receives all expected data.
		try {
			/*
			 * The first 10 messages are assigned the partition by the partitioner - the last 10 specify it in the metadata.
			 * There are two sets of each - the first specifies 1 as the partition for the specified ones, the second does 2.
			 */
			if (specifyPartitionBean.getLatch().await(TIMEOUT, TimeUnit.MILLISECONDS)) {
				Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> noPartition4 = specifyPartitionBean
						.getNoPartitionSpecifiedMetadata4();
				Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> definedPartition4 = specifyPartitionBean
						.getPartitionSpecifiedMetadata4();

				pw.println("Metadata4 unspecified partition: " + noPartition4.size());
				pw.println("Metadata4 specified partition: " + definedPartition4.size());

				for (int i = 1; i <= 10; i++) {
					pw.println("Metadata4, item " + i + " partition " + noPartition4.get(i).getPartition());
				}
				for (int i = 11; i <= 20; i++) {
					pw.println("Metadata4, item " + i + " partition " + definedPartition4.get(i).getPartition());
				}

				Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> noPartition5 = specifyPartitionBean
						.getNoPartitionSpecifiedMetadata5();
				Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> definedPartition5 = specifyPartitionBean
						.getPartitionSpecifiedMetadata5();

				pw.println("Metadata5 unspecified partition: " + noPartition5.size());
				pw.println("Metadata5 specified partition: " + definedPartition5.size());

				for (int i = 1; i <= 10; i++) {
					pw.println("Metadata5, item " + i + " partition " + noPartition5.get(i).getPartition());
				}
				for (int i = 11; i <= 20; i++) {
					pw.println("Metadata5, item " + i + " partition " + definedPartition5.get(i).getPartition());
				}
			} else {
				pw.println("Timed out. SpecifyPartitionBean hasn't received all expected messages in time.");
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Wait interrupted...", e);
			pw.println(e.getMessage());
		}
		pw.close();
	}
}
