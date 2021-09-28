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

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.internal.multi.ComponentManagerManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.transformation.wiki.WikiTransformation;
import org.xwiki.job.event.status.JobProgressManager;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.match.ClassBlockMatcher;
import org.xwiki.rendering.transformation.AbstractTransformation;
import org.xwiki.rendering.transformation.TransformationContext;
import org.xwiki.rendering.transformation.TransformationException;

import com.xpn.xwiki.XWikiContext;

/**
 * Transformation used for running every wiki transformation.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named("wikiTransformation")
public class WikiTransformationTransformation extends AbstractTransformation
{
    /**
     * Context key indicating that the WikiTransformationTransformation is already called in the current context.
     * This allows to avoid situation where a transformation generate a wiki content that needs to be transformed
     * again, and again, …
     */
    public static final String IS_IN_WIKI_TRANSFORMATION = "isInWikiTransformation";

    private static final String WIKI_NAMESPACE_PREFIX = "wiki:";

    @Inject
    private ComponentManagerManager componentManagerManager;

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Inject
    private Logger logger;

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private JobProgressManager jobProgressManager;

    @Inject
    private WikiTransformationManager wikiTransformationManager;

    @Override
    public int getPriority()
    {
        // We actually want the transformation to run before any other, and especially before the MacroTransformation
        // so that macros generated through the WikiTransformation get correctly rendered.
        return 1;
    }

    @Override
    public void transform(Block block, TransformationContext context) throws TransformationException
    {
        XWikiContext xWikiContext = contextProvider.get();

        if (xWikiContext.get(IS_IN_WIKI_TRANSFORMATION) == null) {
            xWikiContext.put(IS_IN_WIKI_TRANSFORMATION, true);

            jobProgressManager.pushLevelProgress(this);
            jobProgressManager.startStep(this, "wikiTransformation.progress.startTransformation",
                "Execute Wiki Transformations");

            try {
                // This component is instantiated within the root component manager of XWiki.
                // In order to access the components that are registered within a wiki, we need to get the component
                // manager corresponding to the wiki.
                // We do that by using the #getId() of the transformation context, which
                // contains a serialized DocumentReference to the document being rendered.
                DocumentReference documentReference = documentReferenceResolver.resolve(context.getId());
                ComponentManager wikiComponentManager = componentManagerManager.getComponentManager(
                    WIKI_NAMESPACE_PREFIX + documentReference.getWikiReference().getName(), false);

                if (wikiComponentManager != null) {
                    Map<String, WikiTransformation> transformations =
                        wikiComponentManager.getInstanceMap(WikiTransformation.class);

                    // From the available transformations, we will remove any that is not compatible
                    // with the current XDOM
                    transformations.entrySet().stream()
                        .filter(entry -> wikiTransformationManager.appliesToEntity(entry.getValue(), documentReference))
                        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));


                    for (Map.Entry<String, WikiTransformation> transformation : transformations.entrySet()) {
                        WikiTransformation wikiTransformation = transformation.getValue();

                        jobProgressManager.pushLevelProgress(wikiTransformation);
                        jobProgressManager.startStep(transformation,
                            "wikiTransformation.progress.applyTransfromation",
                            "Apply transformation [{}]", transformation.getKey());

                        // Search for every block that may be concerned by the transformation
                        for (Class<? extends Block> applicableBlockType : wikiTransformation.getApplicableBlocks()) {
                            for (Block b : block.getBlocks(new ClassBlockMatcher(applicableBlockType),
                                Block.Axes.DESCENDANT_OR_SELF)) {
                                if (wikiTransformation.isApplicable(b, context)) {
                                    wikiTransformation.transform(b, context);
                                }
                            }
                        }

                        jobProgressManager.popLevelProgress(wikiTransformation);
                    }
                }
            } catch (ComponentLookupException e) {
                logger.error("Failed to apply wiki transformations.", e);
            }

            xWikiContext.remove(IS_IN_WIKI_TRANSFORMATION);

            jobProgressManager.popLevelProgress(this);
        }
    }
}
