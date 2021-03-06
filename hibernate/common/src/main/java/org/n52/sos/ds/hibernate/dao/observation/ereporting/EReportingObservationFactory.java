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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.ContextualReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.TemporalReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingTextObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class EReportingObservationFactory extends SeriesObservationFactory {
    protected EReportingObservationFactory() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends EReportingObservation> observationClass() {
        return AbstractEReportingObservation.class;
    }

    @Override
    public Class<? extends ContextualReferencedEReportingObservation> contextualReferencedClass() {
        return ContextualReferencedEReportingObservation.class;
    }

    @Override
    public Class<? extends TemporalReferencedEReportingObservation> temporalReferencedClass() {
        return TemporalReferencedEReportingObservation.class;
    }

    @Override
    public Class<? extends BlobObservation> blobClass() {
        return EReportingBlobObservation.class;
    }

    @Override
    public Class<? extends BooleanObservation> truthClass() {
        return EReportingBooleanObservation.class;
    }

    @Override
    public Class<? extends CategoryObservation> categoryClass() {
        return EReportingCategoryObservation.class;
    }

    @Override
    public Class<? extends CountObservation> countClass() {
        return EReportingCountObservation.class;
    }

    @Override
    public Class<? extends GeometryObservation> geometryClass() {
        return EReportingGeometryObservation.class;
    }

    @Override
    public Class<? extends NumericObservation> numericClass() {
        return EReportingNumericObservation.class;
    }

    @Override
    public Class<? extends SweDataArrayObservation> sweDataArrayClass() {
        return EReportingSweDataArrayObservation.class;
    }

    @Override
    public Class<? extends TextObservation> textClass() {
        return EReportingTextObservation.class;
    }

    @Override
    public Class<? extends ComplexObservation> complexClass() {
        return EReportingComplexObservation.class;
    }

    @Override
    public Series series() {
        return new EReportingSeries();
    }

    @Override
    public Class<? extends Series> seriesClass() {
        return EReportingSeries.class;
    }

    public static EReportingObservationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final EReportingObservationFactory INSTANCE
                = new EReportingObservationFactory();

        private Holder() {
        }
    }

}
