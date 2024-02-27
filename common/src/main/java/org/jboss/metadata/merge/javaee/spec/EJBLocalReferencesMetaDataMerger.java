/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.metadata.merge.javaee.spec;

import org.jboss.metadata.javaee.spec.EJBLocalReferenceMetaData;
import org.jboss.metadata.javaee.spec.EJBLocalReferencesMetaData;

/**
 * EJBLocalReferencesMetaData.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class EJBLocalReferencesMetaDataMerger {

    /**
     * Merge ejb local references
     *
     * @param override      the override references
     * @param overriden     the overriden references
     * @param overridenFile the overriden file name
     * @param overrideFile  the override file
     * @return the merged referencees
     */
    public static EJBLocalReferencesMetaData merge(EJBLocalReferencesMetaData override, EJBLocalReferencesMetaData overriden,
                                                   String overridenFile, String overrideFile) {
        if (override == null && overriden == null)
            return null;

        if (override == null)
            return overriden;

        EJBLocalReferencesMetaData merged = new EJBLocalReferencesMetaData();
        return merge(merged, overriden, override, "ejb-local-ref", overridenFile, overrideFile, false);
    }

    /**
     * From avaEEMetaDataUtil.java
     */
    private static EJBLocalReferencesMetaData merge(EJBLocalReferencesMetaData merged, EJBLocalReferencesMetaData overriden,
                                                    EJBLocalReferencesMetaData mapped, String context, String overridenFile, String overrideFile, boolean mustOverride) {
        if (merged == null)
            throw new IllegalArgumentException("Null merged");

        // Nothing to do
        if (overriden == null && mapped == null)
            return merged;

        // No override
        if (overriden == null || overriden.isEmpty()) {
            // There are no overrides and no overriden
            // Note: it has been really silly to call upon merge, but allas
            if (mapped == null)
                return merged;

            if (mapped.isEmpty() == false && mustOverride)
                throw new IllegalStateException(overridenFile + " has no " + context + "s but " + overrideFile + " has "
                        + mapped.keySet());
            if (mapped != merged)
                merged.addAll(mapped);
            return merged;
        }

        // Wolf: I want to maintain the order of originals (/ override)
        // Process the originals
        for (EJBLocalReferenceMetaData original : overriden) {
            String key = original.getKey();
            if (mapped != null && mapped.containsKey(key)) {
                EJBLocalReferenceMetaData override = mapped.get(key);
                EJBLocalReferenceMetaData tnew = EJBLocalReferenceMetaDataMerger.merge(override, original);
                merged.add(tnew);
            } else {
                merged.add(original);
            }
        }

        // Process the remaining overrides
        if (mapped != null) {
            for (EJBLocalReferenceMetaData override : mapped) {
                String key = override.getKey();
                if (merged.containsKey(key))
                    continue;
                if (mustOverride)
                    throw new IllegalStateException(key + " in " + overrideFile + ", but not in " + overridenFile);
                merged.add(override);
            }
        }

        return merged;
    }

    public static void augment(EJBLocalReferencesMetaData dest, EJBLocalReferencesMetaData augment,
                               EJBLocalReferencesMetaData main, boolean resolveConflicts) {
        for (EJBLocalReferenceMetaData ejbLocalReference : augment) {
            if (dest.containsKey(ejbLocalReference.getKey())) {
                if (!resolveConflicts && (main == null || !main.containsKey(ejbLocalReference.getKey()))) {
                    throw new IllegalStateException("Unresolved conflict on ejb local reference named: "
                            + ejbLocalReference.getKey());
                } else {
                    EJBLocalReferenceMetaDataMerger.augment(dest.get(ejbLocalReference.getKey()), ejbLocalReference,
                            (main != null) ? main.get(ejbLocalReference.getKey()) : null, resolveConflicts);
                }
            } else {
                dest.add(ejbLocalReference);
            }
        }
    }

}
