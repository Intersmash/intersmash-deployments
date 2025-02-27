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

package org.jboss.intersmash.deployments.wildfly.microprofile.reactive.messaging.kafka.tx;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test servlet which can be used to invoke common JMS tasks in test classes.
 */
@WebServlet("/tx")
public class TxTestServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(TxTestServlet.class.toString());

	@Inject
	Bean bean;

	@Inject
	TransactionalBean txBean;

	private static final long TIMEOUT = 15000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter pw = resp.getWriter();
		pw.println("Starting tx servlet; waiting for data...");

		// Let's wait until bean receives all expected data.
		try {
			if (bean.getLatch().await(TIMEOUT, TimeUnit.MILLISECONDS)) {
				// Storing of data to the database may change order of the send data.
				List<String> list = bean.getWords();

				// Print items we've got to the final 'sink' method
				pw.println("items: " + list.size());
				for (String item : list) {
					pw.println("item - " + item);
				}

				// Print items that have been pushed to the database
				pw.println("database records: " + txBean.getCount());
				for (String item : txBean.getDbRecords()) {
					pw.println("db record - " + item);
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
}
