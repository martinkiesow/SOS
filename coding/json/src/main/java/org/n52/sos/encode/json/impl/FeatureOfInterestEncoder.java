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
package org.n52.sos.encode.json.impl;

import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.JSONEncoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class FeatureOfInterestEncoder extends JSONEncoder<AbstractFeature> {
    public FeatureOfInterestEncoder() {
        super(AbstractFeature.class);
    }

    @Override
    public JsonNode encodeJSON(AbstractFeature t) throws EncodingException {
        if (t instanceof FeatureCollection) {
            return encodeFeatureCollection(t);
        } else if (t instanceof SamplingFeature) {
            return encodeSamplingFeature(t);
        } else {
            throw new UnsupportedEncoderInputException(this, t);
        }
    }

    private JsonNode encodeSamplingFeature(AbstractFeature t) throws EncodingException {
        SamplingFeature sf = (SamplingFeature) t;
        if (sf.isSetUrl()) {
            return nodeFactory().textNode(sf.getUrl());
        } else if (!sf.isSetGeometry()) {
            return nodeFactory().textNode(sf.getIdentifierCodeWithAuthority().getValue());
        } else {
            ObjectNode json = nodeFactory().objectNode();
            encodeIdentifier(sf, json);
            encodeNames(sf, json);
            encodeSampledFeatures(sf, json);
            encodeGeometry(sf, json);
            return json;
        }
    }

    private JsonNode encodeFeatureCollection(AbstractFeature t) throws EncodingException {
        FeatureCollection featureCollection = (FeatureCollection) t;
        ArrayNode a = nodeFactory().arrayNode();
        for (AbstractFeature af : featureCollection) {
            a.add(encodeObjectToJson(af));
        }
        return a;
    }

    private void encodeIdentifier(SamplingFeature sf, ObjectNode json) {
        if (sf.isSetIdentifier()) {
            json.set(JSONConstants.IDENTIFIER, encodeCodeWithAuthority(sf.getIdentifierCodeWithAuthority()));
        }

    }

    private void encodeNames(SamplingFeature samplingFeature, ObjectNode json) {
        if (samplingFeature.isSetName()) {
            if (samplingFeature.getName().size() == 1) {
                json.set(JSONConstants.NAME, encodeCodeType(samplingFeature.getName().iterator().next()));
            } else {
                ArrayNode names = json.putArray(JSONConstants.NAME);
                for (CodeType name : samplingFeature.getName()) {
                    names.add(encodeCodeType(name));
                }
            }
        }
    }

    private void encodeSampledFeatures(SamplingFeature sf, ObjectNode json) throws EncodingException {
        if (sf.isSetSampledFeatures()) {
            if (sf.getSampledFeatures().size() == 1) {
                json.set(JSONConstants.SAMPLED_FEATURE, encodeObjectToJson(sf.getSampledFeatures().iterator().next()));
            } else {
                ArrayNode sampledFeatures = json.putArray(JSONConstants.SAMPLED_FEATURE);
                for (AbstractFeature sampledFeature : sf.getSampledFeatures()) {
                    sampledFeatures.add(encodeObjectToJson(sampledFeature));
                }
            }
        }
    }

    private void encodeGeometry(SamplingFeature sf, ObjectNode json) throws EncodingException {
        if (sf.isSetGeometry()) {
            json.set(JSONConstants.GEOMETRY, encodeObjectToJson(sf.getGeometry()));
        }
    }
}
