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

import java.util.HashMap;

import javax.script.Bindings;

import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.TransformationContext;

/**
 * Binding used to get information in a transformation template.
 *
 * @version $Id$
 * @since 1.0
 */
public class WikiTransformationBinding extends HashMap<String, Object> implements Bindings
{
    protected static final String BLOCK = "block";

    protected static final String TRANSFORMATION_CONTEXT = "transformationContext";

    protected static final String RESULT = "result";

    /**
     * Build a new {@link WikiTransformationBinding}.
     *
     * @param block the block being transformed
     * @param context the transformation context
     */
    public WikiTransformationBinding(Block block, TransformationContext context)
    {
        this.put(BLOCK, block);
        this.put(TRANSFORMATION_CONTEXT, context);
    }

    /**
     * @return the block being transformed
     */
    public Block getBlock()
    {
        return (Block) this.get(BLOCK);
    }

    /**
     * @return the transformation context
     */
    public TransformationContext getTransformationContext()
    {
        return (TransformationContext) this.get(TRANSFORMATION_CONTEXT);
    }

    /**
     * @param block the result block
     */
    public void setResult(Block block)
    {
        if (this.containsKey(RESULT)) {
            Block oldResult = (Block) this.get(RESULT);
            oldResult.getParent().replaceChild(block, oldResult);
        } else {
            this.getBlock().getParent().replaceChild(block, this.getBlock());
        }

        this.put(RESULT, block);
    }

    /**
     * @return the result block
     */
    public Block getResult()
    {
        return (Block) this.get(RESULT);
    }
}
