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

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.api.KafkaMetadataUtil;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

/**
 * Taken from WildFly testsuite, see
 * org.wildfly.test.integration.microprofile.reactive.messaging.kafka.api.SpecifyPartitionBean.
 * <p/>
 * This generates some data and sends them to an AMQ-Stream instance to 'testing' topic via 'serializer-to-kafka'
 * outgoing interface. At the same time it reads from AMQ-Streams instance from 'testing' topic via
 * 'serializer-from-kafka' incoming interface (see 'microprofile-config.properties' for more context).
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class SpecifyPartitionBean {

	private final CountDownLatch latch = new CountDownLatch(40);
	private final Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> noPartitionSpecifiedMetadata4 = Collections
			.synchronizedMap(new HashMap<>());
	private final Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> partitionSpecifiedMetadata4 = Collections
			.synchronizedMap(new HashMap<>());
	private final Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> noPartitionSpecifiedMetadata5 = Collections
			.synchronizedMap(new HashMap<>());
	private final Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> partitionSpecifiedMetadata5 = Collections
			.synchronizedMap(new HashMap<>());

	public CountDownLatch getLatch() {
		return latch;
	}

	public Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> getNoPartitionSpecifiedMetadata4() {
		return noPartitionSpecifiedMetadata4;
	}

	public Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> getPartitionSpecifiedMetadata4() {
		return partitionSpecifiedMetadata4;
	}

	public Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> getNoPartitionSpecifiedMetadata5() {
		return noPartitionSpecifiedMetadata5;
	}

	public Map<Integer, IncomingKafkaRecordMetadata<String, Integer>> getPartitionSpecifiedMetadata5() {
		return partitionSpecifiedMetadata5;
	}

	@Outgoing("invm2")
	public Publisher<Integer> source4() {
		return ReactiveStreams.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20).buildRs();
	}

	@Incoming("invm2")
	@Outgoing("to-kafka4")
	public Message<Integer> sendToKafka4(Integer i) {
		Message<Integer> msg = Message.of(i);

		OutgoingKafkaRecordMetadata.OutgoingKafkaRecordMetadataBuilder<String> mb = OutgoingKafkaRecordMetadata
				.<String> builder()
				.withKey("KEY-" + i);

		if (i > 10) {
			mb.withPartition(1);
		}

		msg = KafkaMetadataUtil.writeOutgoingKafkaMetadata(msg, mb.build());
		return msg;
	}

	@Incoming("from-kafka4")
	public CompletionStage<Void> receiveFromKafka4(Message<Integer> msg) {
		IncomingKafkaRecordMetadata<String, Integer> metadata = KafkaMetadataUtil.readIncomingKafkaMetadata(msg).get();

		if (msg.getPayload() <= 10) {
			noPartitionSpecifiedMetadata4.put(msg.getPayload(), metadata);
		} else {
			partitionSpecifiedMetadata4.put(msg.getPayload(), metadata);
		}
		latch.countDown();
		return msg.ack();
	}

	@Outgoing("invm3")
	public Publisher<Integer> source5() {
		return ReactiveStreams.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20).buildRs();
	}

	@Incoming("invm3")
	@Outgoing("to-kafka5")
	public Message<Integer> sendToKafka5(Integer i) {
		Message<Integer> msg = Message.of(i);

		OutgoingKafkaRecordMetadata.OutgoingKafkaRecordMetadataBuilder<String> mb = OutgoingKafkaRecordMetadata
				.<String> builder()
				.withKey("KEY-" + i);
		if (i > 10) {
			mb.withPartition(0);
		}

		msg = KafkaMetadataUtil.writeOutgoingKafkaMetadata(msg, mb.build());
		return msg;
	}

	@Incoming("from-kafka5")
	public CompletionStage<Void> receiveFromKafka5(Message<Integer> msg) {
		IncomingKafkaRecordMetadata<String, Integer> metadata = KafkaMetadataUtil.readIncomingKafkaMetadata(msg).get();

		if (msg.getPayload() <= 10) {
			noPartitionSpecifiedMetadata5.put(msg.getPayload(), metadata);
		} else {
			partitionSpecifiedMetadata5.put(msg.getPayload(), metadata);
		}
		latch.countDown();
		return msg.ack();
	}
}
