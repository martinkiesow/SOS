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
package org.n52.sos.ds.hibernate.dao.observation.series;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;

/**
 * Abstract value time data access object class for {@link SeriesValueTime}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public abstract class AbstractSeriesValueTimeDAO extends AbstractValueTimeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesValueTimeDAO.class);

    /**
     * Get the concrete {@link SeriesValueTime} class.
     *
     * @return The concrete {@link SeriesValueTime} class
     */
    protected abstract Class<?> getSeriesValueTimeClass();

    /**
     * Get {@link ObservationTimeExtrema} for a {@link Series} with temporal
     * filter.
     *
     * @param request
     *            {@link GetObservationRequest} request
     * @param series
     *            {@link Series} to get time extrema for
     * @param temporalFilterCriterion
     *            Temporal filter
     * @param session
     *            Hibernate session
     * @return Time extrema for {@link Series}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public ObservationTimeExtrema getTimeExtremaForSeries(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, null, session);
        addMinMaxTimeProjection(c);
        LOGGER.debug("QUERY getTimeExtremaForSeries(request, series, temporalFilter): {}",
                HibernateHelper.getSqlString(c));
        return parseMinMaxTime((Object[]) c.uniqueResult());

    }

    /**
     * Get {@link ObservationTimeExtrema} for a {@link Series}.
     *
     * @param request
     *            {@link GetObservationRequest} request
     * @param series
     *            {@link Series} to get time extrema for
     * @param session
     *            Hibernate session
     * @return Time extrema for {@link Series}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public ObservationTimeExtrema getTimeExtremaForSeries(GetObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return getTimeExtremaForSeries(request, series, null, session);
    }

    /**
     * Query the minimum {@link SeriesValueTime} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting minimum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public TemporalReferencedSeriesObservation getMinSeriesValueFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
                ExtendedIndeterminateTime.FIRST, session).uniqueResult();
    }

    /**
     * Query the maximum {@link SeriesValueTime} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting maximum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public TemporalReferencedSeriesObservation getMaxSeriesValueFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
                ExtendedIndeterminateTime.LATEST, session).uniqueResult();
    }

    /**
     * Query the minimum {@link SeriesValueTime} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Resulting minimum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public TemporalReferencedSeriesObservation getMinSeriesValueFor(GetObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, null, ExtendedIndeterminateTime.FIRST, session)
                .uniqueResult();
    }

    /**
     * Query the maximum {@link SeriesValueTime} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Resulting maximum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public TemporalReferencedSeriesObservation getMaxSeriesValueFor(GetObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, null, ExtendedIndeterminateTime.LATEST, session)
                .uniqueResult();
    }

    /**
     * Get default {@link Criteria} for {@link Class}
     *
     * @param session
     *            Hibernate Session
     * @return Default {@link Criteria}
     */
    protected Criteria getDefaultObservationCriteria(Session session) {
        return getDefaultCriteria(getSeriesValueTimeClass(), session);
//        return session.createCriteria().add(Restrictions.eq(TemporalReferencedSeriesObservation.DELETED, false))
//                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    private void addMinMaxTimeProjection(Criteria c) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min(TemporalReferencedSeriesObservation.PHENOMENON_TIME_START));
        projectionList.add(Projections.max(TemporalReferencedSeriesObservation.PHENOMENON_TIME_END));
        projectionList.add(Projections.max(TemporalReferencedSeriesObservation.RESULT_TIME));
        if (HibernateHelper.isColumnSupported(getSeriesValueTimeClass(), TemporalReferencedSeriesObservation.VALID_TIME_START)
                && HibernateHelper
                        .isColumnSupported(getSeriesValueTimeClass(), TemporalReferencedSeriesObservation.VALID_TIME_END)) {
            projectionList.add(Projections.min(TemporalReferencedSeriesObservation.VALID_TIME_START));
            projectionList.add(Projections.max(TemporalReferencedSeriesObservation.VALID_TIME_END));
        }
        c.setProjection(projectionList);
    }

    private ObservationTimeExtrema parseMinMaxTime(Object[] result) {
        ObservationTimeExtrema ote = new ObservationTimeExtrema();
        if (result != null) {
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            ote.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
            ote.setMaxResultTime(DateTimeHelper.makeDateTime(result[2]));
            if (result.length == 5) {
                ote.setMinValidTime(DateTimeHelper.makeDateTime(result[3]));
                ote.setMaxValidTime(DateTimeHelper.makeDateTime(result[4]));
            }
        }
        return ote;
    }

    /**
     * Create {@link Criteria} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param sosIndeterminateTime
     *            first/latest indicator
     * @param session
     *            Hibernate Session
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getSeriesValueCriteriaFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, IndeterminateValue sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session).createAlias(TemporalReferencedSeriesObservation.SERIES, "s");
        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.add(Restrictions.eq("s." + Series.ID, series));

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(TemporalReferencedSeriesObservation.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, series, offerings";
        if (temporalFilterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(temporalFilterCriterion);
        }
        if (sosIndeterminateTime != null) {
            logArgs += ", sosIndeterminateTime";
            addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        }
        addSpecificRestrictions(c, request);
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c;
    }
}
