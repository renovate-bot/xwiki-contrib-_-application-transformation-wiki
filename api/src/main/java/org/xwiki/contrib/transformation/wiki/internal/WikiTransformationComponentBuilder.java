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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.wiki.WikiComponent;
import org.xwiki.component.wiki.WikiObjectComponentBuilder;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.rendering.block.Block;

import com.xpn.xwiki.XWikiContext;
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
@Named(WikiTransformationComponentBuilder.TRANSFORMATION_CONFIG_STRING_REFERENCE)
public class WikiTransformationComponentBuilder implements WikiObjectComponentBuilder
{
    /**
     * The reference to the transformation configuration.
     */
    public static final LocalDocumentReference TRANSFORMATION_CONFIG_REFERENCE =
        new LocalDocumentReference(Arrays.asList("Transformation", "Code"), "TransformationConfigurationClass");

    /**
     * The serialized reference to the transformation configuration.
     */
    public static final String TRANSFORMATION_CONFIG_STRING_REFERENCE =
        "Transformation.Code.TransformationConfigurationClass";

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    @Inject
    private ComponentManager componentManager;

    @Override
    public EntityReference getClassReference()
    {
        return TRANSFORMATION_CONFIG_REFERENCE;
    }

    @Override
    public List<WikiComponent> buildComponents(ObjectReference reference)
    {
        XWikiContext context = contextProvider.get();

        try {
            XWikiDocument document = context.getWiki().getDocument(reference.getDocumentReference(), context);
            BaseObject object = document.getXObject(reference);

            String id = object.getStringValue("id");
            Pattern pattern = Pattern.compile(object.getStringValue("pattern"));

            String[] matchingRawBlocks = object.getStringValue("block").split(",");

            String transformationTemplate = object.getLargeStringValue("template");

            List<Class<? extends Block>> applicableBlocks = new ArrayList<>();
            for (String matchingRawBlock : matchingRawBlocks) {
                applicableBlocks.add((Class<? extends Block>) Class.forName(matchingRawBlock));
            }

            PatternWikiComponentWikiTransformation transformation = new PatternWikiComponentWikiTransformation(id,
                reference.getDocumentReference(), document.getAuthorReference(),
                componentManager, transformationTemplate, pattern);
            transformation.setApplicableBlocks(applicableBlocks);

            return Collections.singletonList(transformation);

        } catch (Exception e) {
            logger.error("Failed to build wiki transformation component for [{}]", reference, e);

            return Collections.emptyList();
        }
    }
}
