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

package org.jboss.intersmash.deployments.wildfly.microprofile.reactive.messaging.kafka.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Taken from WildFly testsuite, see
 * org.wildfly.test.integration.microprofile.reactive.messaging.kafka.serializer.PersonDeserializer.
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class PersonDeserializer implements Deserializer<Person> {

	private static final Logger LOGGER = Logger.getLogger(PersonDeserializer.class.toString());

	@Override
	public Person deserialize(String topic, byte[] data) {
		if (data == null) {
			return null;
		}
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
			String name = in.readUTF();
			int age = in.readInt();
			in.close();
			return new Person(name, age);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Couldn't deserialize data!", e);
			throw new RuntimeException(e);
		}
	}
}
