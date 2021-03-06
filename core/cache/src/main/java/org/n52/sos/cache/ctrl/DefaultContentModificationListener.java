/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.cache.ctrl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.cache.ContentCacheUpdate;
import org.n52.janmayen.event.Event;
import org.n52.janmayen.event.EventListener;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.cache.ctrl.action.ObservationInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultTemplateInsertionUpdate;
import org.n52.sos.cache.ctrl.action.SensorDeletionUpdate;
import org.n52.sos.cache.ctrl.action.SensorInsertionUpdate;
import org.n52.sos.ds.CacheFeederHandler;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.event.events.ResultInsertion;
import org.n52.sos.event.events.ResultTemplateInsertion;
import org.n52.sos.event.events.SensorDeletion;
import org.n52.sos.event.events.SensorInsertion;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public class DefaultContentModificationListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContentModificationListener.class);

    private static final Set<Class<? extends Event>> TYPES = new HashSet<>(Arrays.asList(
            SensorInsertion.class,
            ObservationInsertion.class,
            ResultTemplateInsertion.class,
            SensorDeletion.class,
            ResultInsertion.class));

    private final CacheFeederHandler handler;
    private final ContentCacheController controller;

    @Inject
    public DefaultContentModificationListener(CacheFeederHandler handler, ContentCacheController controller) {
        this.handler = handler;
        this.controller = controller;
    }

    @Override
    public Set<Class<? extends Event>> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    @Override
    public void handle(Event event) {
        ContentCacheUpdate update = createUpdate(event);
        LOGGER.debug("Updating Cache after content modification: {}", update);
        try {
            this.controller.update(update);
        } catch (OwsExceptionReport ex) {
            LOGGER.error("Error processing Event", ex);
        }
    }

    private ContentCacheUpdate createUpdate(Event event) {
        if (event instanceof SensorInsertion) {
            return createUpdate((SensorInsertion) event);
        } else if (event instanceof ObservationInsertion) {
            return createUpdate((ObservationInsertion) event);
        } else if (event instanceof ResultTemplateInsertion) {
            return createUpdate((ResultTemplateInsertion) event);
        } else if (event instanceof SensorDeletion) {
            return createUpdate((SensorDeletion) event);
        } else if (event instanceof ResultInsertion) {
            return createUpdate((ResultInsertion) event);
        } else {
            throw new AssertionError();
        }
    }

    private ContentCacheUpdate createUpdate(ResultInsertion e) {
        return new ResultInsertionUpdate(e.getRequest().getTemplateIdentifier(), e.getResponse().getObservations());
    }

    private ContentCacheUpdate createUpdate(SensorDeletion e) {
        return new SensorDeletionUpdate(this.handler, e.getRequest());
    }

    private ContentCacheUpdate createUpdate(ResultTemplateInsertion e) {
        return new ResultTemplateInsertionUpdate(e.getRequest(), e.getResponse());
    }

    private ContentCacheUpdate createUpdate(ObservationInsertion e) {
        return new ObservationInsertionUpdate(e.getRequest());
    }

    private ContentCacheUpdate createUpdate(SensorInsertion e) {
        return new SensorInsertionUpdate(e.getRequest(), e.getResponse());
    }

}
