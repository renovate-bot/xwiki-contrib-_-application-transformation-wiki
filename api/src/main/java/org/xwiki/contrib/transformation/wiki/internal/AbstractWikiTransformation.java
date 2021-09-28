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
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.Transformation;
import org.xwiki.rendering.transformation.TransformationContext;
import org.xwiki.rendering.transformation.TransformationException;

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

    protected int priority = DEFAULT_PRIORITY;

    @Inject
    protected WikiTransformationManager wikiTransformationManager;

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public boolean isApplicable(Block block, TransformationContext context)
    {
        boolean foundApplicableBlocks = false;
        for (int i = 0; i < getApplicableBlocks().size() && !foundApplicableBlocks; i++) {
            foundApplicableBlocks |= getApplicableBlocks().get(i).isInstance(block);
        }

        if (foundApplicableBlocks) {
            return wikiTransformationManager.appliesToEntity(this, context.getId());
        }

        return false;
    }

    @Override
    @Deprecated
    public void transform(XDOM dom, Syntax syntax) throws TransformationException
    {
        transform(dom, new TransformationContext(dom, syntax));
    }

    @Override
    public int compareTo(Transformation transformation)
    {
        return getPriority() - transformation.getPriority();
    }

    protected abstract boolean isApplicableInternal(Block block, TransformationContext transformationContext);
}
