/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.transformation.wiki.internal;

import javax.inject.Inject;

import org.xwiki.contrib.transformation.wiki.WikiTransformation;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.TransformationContext;

/**
 * Helper for implementing {@link WikiTransformation}.
 *
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWikiTransformation implements WikiTransformation
{
    /**
     * The default priority.
     */
    public static final int DEFAULT_PRIORITY = 100;

    @Inject
    protected WikiTransformationManager wikiTransformationManager;

    @Override
    public int getPriority()
    {
        return DEFAULT_PRIORITY;
    }

    @Override
    public boolean isApplicable(Block block, TransformationContext context)
    {
        boolean foundApplicableBlock = false;
        for (int i = 0; i < getApplicableBlocks().size() && !foundApplicableBlock; i++) {
            foundApplicableBlock = getApplicableBlocks().get(i).isInstance(block);
        }

        if (foundApplicableBlock) {
            return wikiTransformationManager.appliesToEntity(this, context.getId());
        }

        return false;
    }

    protected abstract boolean isApplicableInternal(Block block, TransformationContext context);
}
