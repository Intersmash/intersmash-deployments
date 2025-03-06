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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Taken from WildFly testsuite, see
 * org.wildfly.test.integration.microprofile.reactive.messaging.kafka.tx.TransactionalBean.
 */
@ApplicationScoped
public class TransactionalBean {

	@PersistenceContext(unitName = "test")
	EntityManager em;

	@Transactional
	public void storeValue(String name) {
		ContextEntity entity = new ContextEntity();
		entity.setName(name);
		em.persist(entity);
	}

	@Transactional
	public Set<String> getDbRecords() {
		TypedQuery<ContextEntity> query = em.createQuery("SELECT c from ContextEntity c", ContextEntity.class);
		return query.getResultList().stream().map(v -> v.getName()).collect(Collectors.toSet());
	}

	@Transactional
	public int getCount() {
		TypedQuery<Long> query = em.createQuery("SELECT count(c) from ContextEntity c", Long.class);
		List<Long> result = query.getResultList();
		return result.get(0).intValue();
	}
}
