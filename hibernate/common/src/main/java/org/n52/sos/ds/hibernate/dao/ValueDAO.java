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
package org.n52.sos.ds.hibernate.dao;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.ExtensionFesFilterCriteriaAdder;

/**
 * Implementation of {@link AbstractValueDAO} for old concept
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public class ValueDAO extends AbstractValueDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueDAO.class);

    /**
     * Query streaming value for parameter as {@link ScrollableResults}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting {@link ScrollableResults}
     * @throws HibernateException
     *             If an error occurs when querying the {@link AbstractValuedLegacyObservation}s
     * @throws OwsExceptionReport
     *             If an error occurs when querying the {@link AbstractValuedLegacyObservation}s
     */
    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, @Nullable Criterion temporalFilterCriterion, Session session)
            throws HibernateException, OwsExceptionReport {
        return getCriteria(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, session)
                .scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Query streaming value for parameter as {@link ScrollableResults}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param session
     *            Hibernate Session
     * @return Resulting {@link ScrollableResults}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the {@link AbstractValuedLegacyObservation}s
     */
    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, Session session) throws OwsExceptionReport {
        return getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest, null, session);
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param chunkSize
     *            chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the {@link AbstractValuedLegacyObservation}s
     */
    @SuppressWarnings("unchecked")
    public List<ValuedObservation<?>> getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, Criterion temporalFilterCriterion, int chunkSize,
            int currentRow, Session session) throws OwsExceptionReport {
        Criteria c =
                getCriteria(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, session);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request);
        LOGGER.debug("QUERY getStreamingValuesFor(): {}", HibernateHelper.getSqlString(c));
        return (List<ValuedObservation<?>>) c.list();
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param chunkSize
     *            Chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the {@link AbstractValuedLegacyObservation}s
     */
    @SuppressWarnings("unchecked")
    public List<ValuedObservation<?>> getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, int chunkSize, int currentRow, Session session)
            throws OwsExceptionReport {
        Criteria c = getCriteria(request, procedure, observableProperty, featureOfInterest, null, session);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request);
        LOGGER.debug("QUERY getStreamingValuesFor(): {}", HibernateHelper.getSqlString(c));
        return (List<ValuedObservation<?>>) c.list();
    }

    /**
     * Get {@link Criteria} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getCriteria(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest, Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getDefaultObservationCriteria(AbstractValuedLegacyObservation.class, session)
                        .createAlias(AbstractValuedLegacyObservation.PROCEDURE, "p")
                        .createAlias(AbstractValuedLegacyObservation.FEATURE_OF_INTEREST, "f")
                        .createAlias(AbstractValuedLegacyObservation.OBSERVABLE_PROPERTY, "o");

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.add(Restrictions.eq("p." + Procedure.ID, procedure));
        c.add(Restrictions.eq("o." + ObservableProperty.ID, observableProperty));
        c.add(Restrictions.eq("f." + FeatureOfInterest.ID, featureOfInterest));

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(AbstractValuedLegacyObservation.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, series, offerings";
        if (temporalFilterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(temporalFilterCriterion);
        }
        addSpecificRestrictions(c, request);
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(c, request.getFesFilterExtensions()).add();
        }
        LOGGER.debug("QUERY getStreamingValuesFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.setReadOnly(true);
    }

    /**
     * Get default {@link Criteria} for {@link Class}
     *
     * @param clazz
     *            {@link Class} to get default {@link Criteria} for
     * @param session
     *            Hibernate Session
     * @return Default {@link Criteria}
     */
    public Criteria getDefaultObservationCriteria(Class<?> clazz, Session session) {
        return session.createCriteria(clazz)
                .add(Restrictions.eq(AbstractValuedLegacyObservation.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Query unit for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param session
     *            Hibernate Session
     * @return Unit or null if no unit is set
     * @throws OwsExceptionReport
     *             If an error occurs when querying the unit
     */
    public String getUnit(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest, Session session)
            throws OwsExceptionReport {
        Criteria c = getCriteria(request, procedure, observableProperty, featureOfInterest, null, session)
                .setMaxResults(1).setProjection(Projections.property(AbstractValuedLegacyObservation.UNIT));
        return Optional.ofNullable((Unit) c.uniqueResult()).filter(Unit::isSetUnit).map(Unit::getUnit).orElse(null);

    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws OwsExceptionReport {
        // nothing  to add
    }

}
