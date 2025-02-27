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

package org.jboss.qa.appsint.tests.eapamqstreams.serializer;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test servlet which can be used to invoke common JMS tasks in test classes.
 */
@WebServlet("/serializer")
public class SerializerTestServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SerializerTestServlet.class.toString());

	@Inject
	Bean bean;

	private static final long TIMEOUT = 15000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter pw = resp.getWriter();
		pw.println("Starting serializer servlet; waiting for data...");

		// Let's wait until bean receives all expected data.
		try {
			if (bean.getLatch().await(TIMEOUT, TimeUnit.MILLISECONDS)) {
				List<Person> list = bean.getReceived();

				// Let's print basic data for check that serialization -> deserialization went just fine.
				for (int i = 0; i < list.size(); i++) {
					pw.println(i + ". Name: " + list.get(i).getName() + "; Age: " + list.get(i).getAge() + "; Data-Partition: "
							+ bean.getPartitionReceived().get(i));
				}

				// Now let's also check order of the data.
				// Kafka messages only have order per partition, so do some massaging of the data.
				// This simply creates a map with list of persons assigned to the partitions as they were received.
				Map<Integer, List<Person>> actualMap = new HashMap<>();
				for (int i = 0; i < list.size(); i++) {
					List<Person> persons = actualMap.computeIfAbsent(bean.getPartitionReceived().get(i),
							ind -> new ArrayList<>());
					persons.add(list.get(i));
				}

				if (checkDataOrder(actualMap, pw)) {
					pw.println("Data order checked and is as expected.");
				} else {
					pw.println("Data order checked and is unexpected!");
				}
			} else {
				pw.println("Timed out. Bean hasn't received all expected messages in time.");
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Wait interrupted...", e);
			pw.println(e.getMessage());
		}
		pw.close();
	}

	private boolean checkDataOrder(Map<Integer, List<Person>> actualMap, PrintWriter pw) {
		String[] expectedOrder = { "Kabir", "Bob", "Roger", "Franta", "Pepa", "Karel", "Jaromir", "Vita", "Suzie", "Paja" };

		for (String item : expectedOrder) {
			if (assertPersonNextOnAPartition(actualMap, item) == null) {
				pw.println("Expected order was: " + Arrays.toString(expectedOrder));
				pw.println("But the order was broken on position where '" + item + "' was expected.");
				return false;
			}
		}
		return true;
	}

	private Person assertPersonNextOnAPartition(Map<Integer, List<Person>> map, String name) {
		Person found = null;
		int remove = -1;
		for (Map.Entry<Integer, List<Person>> entry : map.entrySet()) {
			List<Person> persons = entry.getValue();
			Person p = persons.get(0);
			if (p.getName().equals(name)) {
				found = p;
				persons.remove(0);
				if (persons.size() == 0) {
					remove = entry.getKey();
				}
			}
		}
		map.remove(remove);
		return found;
	}
}
