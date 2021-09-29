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
package org.xwiki.contrib.transformation.wiki.internal.pattern;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.transformation.wiki.internal.AbstractWikiComponentWikiTransformation;
import org.xwiki.contrib.transformation.wiki.internal.WikiTransformationBinding;
import org.xwiki.contrib.transformation.wiki.internal.pattern.expression.Pattern;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.TransformationContext;

/**
 * Wiki component transformation using a pattern to validate if the transformation is applicable against a given block.
 *
 * @version $Id$
 * @since 1.0
 */
public class PatternWikiTransformation extends AbstractWikiComponentWikiTransformation
{
    private Pattern pattern;

    /**
     * Create a new {@link PatternWikiTransformation}.
     *
     * @param roleHint the role hint
     * @param documentReference the document holding the object
     * @param authorReference the author reference of the document
     * @param componentManager the component manager
     * @param transformationTemplate the transformation template
     * @param pattern the pattern to match on
     * @throws ComponentLookupException if an error happened
     */
    public PatternWikiTransformation(String roleHint, DocumentReference documentReference,
        DocumentReference authorReference, ComponentManager componentManager, String transformationTemplate,
        Pattern pattern) throws ComponentLookupException
    {
        super(roleHint, documentReference, authorReference, transformationTemplate, componentManager);

        this.pattern = pattern;
    }

    @Override
    protected boolean isApplicableInternal(Block block, TransformationContext context)
    {
        return pattern.matches(block);
    }

    @Override
    protected WikiTransformationBinding getWikiTransformationBinding(Block block,
        TransformationContext transformationContext)
    {
        return new PatternWikiTransformationBinding(block, transformationContext, pattern, pattern.getMatcher(block));
    }
}
