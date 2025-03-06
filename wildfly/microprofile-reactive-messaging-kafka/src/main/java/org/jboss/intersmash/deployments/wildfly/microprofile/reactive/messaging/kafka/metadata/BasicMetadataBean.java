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

package org.jboss.intersmash.deployments.wildfly.microprofile.reactive.messaging.kafka.metadata;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.api.KafkaMetadataUtil;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;

/**
 * Taken from WildFly testsuite, see
 * org.wildfly.test.integration.microprofile.reactive.messaging.kafka.api.BasicMetadataBean
 * and other beans there too.
 * <p/>
 * This generates some data and sends them to an AMQ-Stream instance to
 * 'testing' topic via 'serializer-to-kafka' outgoing interface. At the same
 * time it reads from AMQ-Streams instance from 'testing' topic via
 * 'serializer-from-kafka' incoming interface (see
 * 'microprofile-config.properties' for more context).
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class BasicMetadataBean {

	private final CountDownLatch latch = new CountDownLatch(4);
	private final Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> testing2Metadatas = Collections
			.synchronizedMap(new HashMap<>());
	private final Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> testing3Metadatas = Collections
			.synchronizedMap(new HashMap<>());

	private final Instant timestamp = Instant.now().minus(Duration.ofSeconds(10)).truncatedTo(ChronoUnit.SECONDS);

	public CountDownLatch getLatch() {
		return latch;
	}

	public Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> getTesting2Metadatas() {
		return testing2Metadatas;
	}

	public Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> getTesting3Metadatas() {
		return testing3Metadatas;
	}

	@Outgoing("invm1")
	public Publisher<Integer> source() {
		return ReactiveStreams.of(1, 2, 3, 4).buildRs();
	}

	@Incoming("invm1")
	@Outgoing("to-kafka2or3-default-to-2")
	public Message<Integer> sendToKafka2or3(Integer i) {
		Message<Integer> msg = Message.of(i);

		OutgoingKafkaRecordMetadata.OutgoingKafkaRecordMetadataBuilder<String> mb = OutgoingKafkaRecordMetadata
				.<String>builder();
		if (i % 2 == 0) {
			// Only set the key for half the messages
			mb.withKey("KEY-" + i);
		}
		if (i % 2 != 0) {
			// Set some headers and timestamp for half the messages
			mb.withHeaders(Collections.singletonList(new RecordHeader("header-" + i, new byte[]{0, 1, 2})));
			mb.withTimestamp(timestamp);
		}
		if (i >= 3) {
			mb.withTopic("testing3");
		}
		msg = KafkaMetadataUtil.writeOutgoingKafkaMetadata(msg, mb.build());
		return msg;
	}

	// Messages which did not have the topic set in the metadata should end up here
	// (as this is the topic set up in the MP Config)
	@Incoming("from-kafka2")
	public CompletionStage<Void> receiveFromKafka2(Message<Integer> msg) {
		IncomingKafkaRecordMetadata<String, Integer> metadata = KafkaMetadataUtil.readIncomingKafkaMetadata(msg).get();
		testing2Metadatas.put(msg.getPayload(), metadata);
		latch.countDown();
		return msg.ack();
	}

	// Messages which had the topic set in the metadata should end up here
	@Incoming("from-kafka3")
	public CompletionStage<Void> receiveFromKafka3(Message<Integer> msg) {
		IncomingKafkaRecordMetadata<String, Integer> metadata = KafkaMetadataUtil.readIncomingKafkaMetadata(msg).get();
		testing3Metadatas.put(msg.getPayload(), metadata);
		latch.countDown();
		return msg.ack();
	}
}
