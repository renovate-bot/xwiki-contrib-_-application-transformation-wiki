package org.xwiki.contrib.transformation.wiki;

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

import java.util.List;

import org.xwiki.component.annotation.Role;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.TransformationContext;
import org.xwiki.rendering.transformation.TransformationException;

/**
 * Define an XDOM transformation on XWiki documents.
 *
 * @version $Id$
 * @since 1.0
 */
@Role
public interface WikiTransformation
{
    /**
     * @return the priority of the transformation, the transformation with the lowest priority should be executed first.
     */
    int getPriority();

    /**
     * @return the list of entities to which the transformation can be applied. If no entity is provided, the
     * transformation can be applied to any entity.
     */
    List<EntityReference> getApplicableEntities();

    /**
     * @return the list of XDOM blocks on which the transformation is applicable.
     */
    List<Class<? extends Block>> getApplicableBlocks();

    /**
     * @param block the block being transformed
     * @param context the context of the transformation
     * @return true if the transformation is applicable on the given block, in the given context
     */
    boolean isApplicable(Block block, TransformationContext context);

    /**
     * Perform the transformation on the given block.
     *
     * @param block the block to transform
     * @param context the transformation context
     * @throws TransformationException if an error happens
     */
    void transform(Block block, TransformationContext context) throws TransformationException;
}
