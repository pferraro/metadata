/*
 * Copyright The JBoss Metadata Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.metadata.ejb.spec;

import java.util.ArrayList;

import org.jboss.metadata.merge.MergeUtil;

/**
 * AroundInvokesMetaData.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class AroundInvokesMetaData extends ArrayList<AroundInvokeMetaData> {
    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = 3091116374471499482L;

    /**
     * Create a new AroundInvokesMetaData.
     */
    public AroundInvokesMetaData() {
        // For serialization
    }

    public void merge(AroundInvokesMetaData override, AroundInvokesMetaData original) {
        MergeUtil.merge(this, override, original);
    }
}
