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
package org.n52.sos.decode.json.inspire;

import org.n52.faroe.Validation;
import org.n52.shetland.inspire.base.Identifier;
import org.n52.sos.util.AQDJSONConstants;
import org.n52.svalbard.decode.exception.DecodingException;

import com.fasterxml.jackson.databind.JsonNode;

public class InspireIDJSONDecode extends AbstractJSONDecoder<Identifier> {

    public InspireIDJSONDecode() {
        super(Identifier.class);
    }

    @Override
    public Identifier decodeJSON(JsonNode node, boolean validate) throws DecodingException {
        Identifier inspireID = new Identifier(getLocalId(node), getNamespace(node));
        inspireID.setVersionId(parseNillableString(node.path(AQDJSONConstants.VERSION_ID)));
        return inspireID;
    }

    private String getNamespace(JsonNode node) throws DecodingException {
        String namespace = node.path(AQDJSONConstants.NAMESPACE).textValue();
        Validation.notNullOrEmpty("InspireId namespace", namespace);
        return namespace;
    }

    private String getLocalId(JsonNode node) throws DecodingException {
        String localId = node.path(AQDJSONConstants.LOCAL_ID).textValue();
        Validation.notNullOrEmpty("InspireId localId", localId);
        return localId;
    }

}
