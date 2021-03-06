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
package org.n52.sos.ds.hibernate.util.observation;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationValueCreator implements ValuedObservationVisitor<Value<?>> {

    private DecoderRepository decoderRepository;

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Override
    public QuantityValue visit(NumericValuedObservation o) {
        QuantityValue v = new QuantityValue(o.getValue());
        addUnit(o, v);
        return v;
    }


    @Override
    public UnknownValue visit(BlobValuedObservation o) {
        UnknownValue v = new UnknownValue(o.getValue());
        addUnit(o, v);
        return v;
    }

    @Override
    public BooleanValue visit(BooleanValuedObservation o) {
        BooleanValue v = new BooleanValue(o.getValue());
        addUnit(o, v);
        return v;
    }

    @Override
    public CategoryValue visit(CategoryValuedObservation o) {
        CategoryValue v = new CategoryValue(o.getValue());
        addUnit(o, v);
        return v;
    }

    @Override
    public ComplexValue visit(ComplexValuedObservation o) throws OwsExceptionReport {
        SweAbstractDataComponentCreator visitor
                = new SweAbstractDataComponentCreator();
        SweDataRecord record = visitor.visit(o);
        return new ComplexValue(record);
    }

    @Override
    public CountValue visit(CountValuedObservation o) {
        return new CountValue(o.getValue());
    }

    @Override
    public GeometryValue visit(GeometryValuedObservation o) {
        GeometryValue v = new GeometryValue(o.getValue());
        addUnit(o, v);
        return v;
    }

    @Override
    public TextValue visit(TextValuedObservation o) {
        TextValue v = new TextValue(o.getValue());
        addUnit(o, v);
        return v;
    }

    @Override
    public SweDataArrayValue visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport {

        try {
            XmlObject xml = XmlHelper.parseXmlString(o.getValue());
            SweDataArray array = (SweDataArray)decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
            return new SweDataArrayValue(array);
        } catch (DecodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        }

    }

    protected void addUnit(ValuedObservation<?> o, Value<?> v) {
        if (!v.isSetUnit() && o.isSetUnit()) {
            v.setUnit(o.getUnit().getUnit());
        }
    }



}
