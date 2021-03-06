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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import org.n52.iceland.util.LocalizedProducer;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class ProcedureDescriptionEnrichments {
    private String identifier;
    private SosProcedureDescription<?> description;
    private String version;
    private String procedureDescriptionFormat;
    private Map<String, Procedure> procedureCache;
    private Session session;
    private HibernateProcedureConverter converter;
    private TimePeriod validTime;
    private final Locale language;
    private final LocalizedProducer<OwsServiceProvider> serviceProvider;
    private String typeOfIdentifier;
    private String typeOfFormat;
    private final DaoFactory daoFactory;

    private ProcedureDescriptionEnrichments(Locale locale, LocalizedProducer<OwsServiceProvider> serviceProvider, DaoFactory daoFactory) {
        this.serviceProvider = serviceProvider;
        this.language = locale;
        this.daoFactory = daoFactory;
    }

    public ProcedureDescriptionEnrichments setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ProcedureDescriptionEnrichments setDescription(
            SosProcedureDescription<?> description) {
        this.description = description;
        return this;
    }

    public ProcedureDescriptionEnrichments setVersion(String version) {
        this.version = version;
        return this;
    }

    public ProcedureDescriptionEnrichments setProcedureDescriptionFormat(
            String pdf) {
        this.procedureDescriptionFormat = pdf;
        return this;
    }

    public ProcedureDescriptionEnrichments setProcedureCache(
            Map<String, Procedure> procedureCache) {
        this.procedureCache = procedureCache;
        return this;
    }

    public ProcedureDescriptionEnrichments setSession(Session session) {
        this.session = session;
        return this;
    }

    public ProcedureDescriptionEnrichments setConverter(
            HibernateProcedureConverter converter) {
        this.converter = converter;
        return this;

    }

    public ProcedureDescriptionEnrichments setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
        return this;
    }

    public ProcedureDescriptionEnrichments setTypeOfIdentifier(String typeOfIdentifier) {
        this.typeOfIdentifier = typeOfIdentifier;
        return this;
    }

    public ProcedureDescriptionEnrichments setTypeOfFormat(String typeOfFormat) {
        this.typeOfFormat = typeOfFormat;
        return this;
    }

    public Iterable<ProcedureDescriptionEnrichment> createAll() {
        return Iterables.filter(
                Lists.newArrayList(
                        createFeatureOfInterestEnrichment(),
                        createRelatedProceduresEnrichment(),
                        createOfferingEnrichment(),
                        createValidTimeEnrichment(),
                        createBoundingBoxEnrichment(),
                        createClassifierEnrichment(),
                        createIdentificationEnrichment(),
                        createContactsEnrichment(),
                        createKeywordEnrichment(),
                        createValidTimeEnrichment(),
                        createObservablePropertyEnrichment(),
                        createTypeOfEnrichmentEnrichment()),
                ProcedureDescriptionEnrichment.predicate());
    }

    public void enrichAll() throws OwsExceptionReport {
        for (ProcedureDescriptionEnrichment enrichment : createAll()) {
            enrichment.enrich();
        }
    }

    public BoundingBoxEnrichment createBoundingBoxEnrichment() {
        return setValues(new BoundingBoxEnrichment());
    }

    public ClassifierEnrichment createClassifierEnrichment() {
        return setValues(new ClassifierEnrichment());
    }

    public IdentificationEnrichment createIdentificationEnrichment() {
        return setValues(new IdentificationEnrichment(this.daoFactory.getI18NDAORepository()));
    }

    public ContactsEnrichment createContactsEnrichment() {
        return setValues(new ContactsEnrichment(this.language, this.serviceProvider));
    }

    public KeywordEnrichment createKeywordEnrichment() {
        return setValues(new KeywordEnrichment());
    }

    public FeatureOfInterestEnrichment createFeatureOfInterestEnrichment() {
        return setValues(new FeatureOfInterestEnrichment());
    }

    public RelatedProceduresEnrichment createRelatedProceduresEnrichment() {
        return setValues(new RelatedProceduresEnrichment(daoFactory))
                .setConverter(converter).setProcedureCache(procedureCache)
                .setProcedureDescriptionFormat(procedureDescriptionFormat)
                .setValidTime(validTime);
    }

    public OfferingEnrichment createOfferingEnrichment() {
        return setValues(new OfferingEnrichment());
    }

    public ValidTimeEnrichment createValidTimeEnrichment() {
        return setValues(new ValidTimeEnrichment()).setValidTime(validTime);
    }

    private ObservablePropertyEnrichment createObservablePropertyEnrichment() {
        return setValues(new ObservablePropertyEnrichment(this.daoFactory.getI18NDAORepository()));
    }

    private TypeOfEnrichment createTypeOfEnrichmentEnrichment() {
        return setValues(new TypeOfEnrichment()).setTypeOfIdentifier(typeOfIdentifier).setTypeOfFormat(typeOfFormat);
    }

    private <T extends ProcedureDescriptionEnrichment> T setValues(T enrichment) {
        enrichment.setDescription(description)
                .setIdentifier(identifier)
                .setVersion(version)
                .setLocale(language)
                .setSession(session);
        return enrichment;
    }

    public static ProcedureDescriptionEnrichments create(Locale language, LocalizedProducer<OwsServiceProvider> serviceProvider, DaoFactory daoFactory) {
        return new ProcedureDescriptionEnrichments(language, serviceProvider, daoFactory);
    }
}
