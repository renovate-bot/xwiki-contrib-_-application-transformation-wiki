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

import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.wiki.WikiComponent;
import org.xwiki.component.wiki.WikiComponentException;
import org.xwiki.contrib.transformation.wiki.internal.AbstractWikiTransformationComponentBuilder;
import org.xwiki.contrib.transformation.wiki.internal.pattern.expression.Pattern;
import org.xwiki.contrib.transformation.wiki.internal.pattern.expression.PatternBuilder;
import org.xwiki.model.reference.EntityReference;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * Builder for transformation wiki object components.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named(PatternWikiTransformationClassInitializer.XCLASS_FULLNAME)
public class PatternWikiTransformationComponentBuilder
    extends AbstractWikiTransformationComponentBuilder<PatternWikiTransformation>
{
    @Override
    public EntityReference getClassReference()
    {
        return PatternWikiTransformationClassInitializer.XCLASS_REFERENCE;
    }

    protected List<WikiComponent> buildComponentsInternal(BaseObject baseObject) throws WikiComponentException
    {
        XWikiDocument document = baseObject.getOwnerDocument();

        String id = baseObject.getStringValue("id");
        String stringPattern = baseObject.getStringValue("pattern");
        String transformationTemplate = baseObject.getLargeStringValue("template");

        try {
            Pattern pattern = new PatternBuilder().build(stringPattern);

            PatternWikiTransformation transformation = new PatternWikiTransformation(id,
                baseObject.getDocumentReference(), document.getAuthorReference(),
                componentManager, transformationTemplate, pattern);
            transformation.setApplicableBlocks(
                Collections.singletonList(pattern.getPrimaryBlockPattern().getBlockClass()));

            return Collections.singletonList(transformation);
        } catch (ComponentLookupException e) {
            throw new WikiComponentException(
                String.format("Failed to instantiate transformation component from object [%s]",
                    baseObject.getReference()), e);
        }
    }
}
