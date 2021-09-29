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

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.inject.Provider;
import javax.script.ScriptContext;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.component.wiki.WikiComponent;
import org.xwiki.component.wiki.WikiComponentScope;
import org.xwiki.contrib.transformation.wiki.WikiTransformation;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.parser.Parser;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.TransformationContext;
import org.xwiki.rendering.transformation.TransformationException;
import org.xwiki.rendering.transformation.TransformationManager;
import org.xwiki.script.ScriptContextManager;

import com.xpn.xwiki.XWikiContext;

/**
 * Helper for implementing wiki transformation based on wiki components.
 *
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWikiComponentWikiTransformation extends AbstractWikiTransformation
    implements WikiComponent
{
    /**
     * The name of the transformation binding.
     */
    public static final String BINDING_TRANSFORMATION = "transformation";

    protected String roleHint;

    protected DocumentReference documentReference;

    protected DocumentReference authorReference;

    protected String transformationTemplate;

    protected List<EntityReference> applicableEntities;

    protected List<Class<? extends Block>> applicableBlocks;

    protected Provider<XWikiContext> contextProvider;

    protected TransformationManager transformationManager;

    protected Parser parser;

    protected ScriptContextManager scriptContextManager;

    protected AbstractWikiComponentWikiTransformation(String roleHint,
        DocumentReference documentReference,
        DocumentReference authorReference,
        String transformationTemplate,
        ComponentManager componentManager) throws ComponentLookupException
    {
        this.roleHint = roleHint;
        this.documentReference = documentReference;
        this.authorReference = authorReference;
        this.transformationTemplate = transformationTemplate;
        this.applicableEntities = Collections.EMPTY_LIST;
        this.applicableBlocks = Collections.EMPTY_LIST;

        this.wikiTransformationManager = componentManager.getInstance(WikiTransformationManager.class);
        this.contextProvider = componentManager.getInstance(
            new DefaultParameterizedType(null, Provider.class, XWikiContext.class));
        this.transformationManager = componentManager.getInstance(TransformationManager.class);
        this.parser = componentManager.getInstance(Parser.class, Syntax.XWIKI_2_1.toIdString());
        this.scriptContextManager = componentManager.getInstance(ScriptContextManager.class);
    }

    @Override
    public DocumentReference getDocumentReference()
    {
        return documentReference;
    }

    @Override
    public DocumentReference getAuthorReference()
    {
        return authorReference;
    }

    @Override
    public Type getRoleType()
    {
        return WikiTransformation.class;
    }

    @Override
    public String getRoleHint()
    {
        return roleHint;
    }

    @Override
    public WikiComponentScope getScope()
    {
        return WikiComponentScope.WIKI;
    }

    @Override
    public List<EntityReference> getApplicableEntities()
    {
        return applicableEntities;
    }

    /**
     * @see #getApplicableEntities()
     * @param applicableEntities the applicable entities
     */
    public void setApplicableEntities(List<EntityReference> applicableEntities)
    {
        this.applicableEntities = applicableEntities;
    }

    @Override
    public List<Class<? extends Block>> getApplicableBlocks()
    {
        return applicableBlocks;
    }

    /**
     * @see #getApplicableBlocks()
     * @param applicableBlocks the applicable blocks
     */
    public void setApplicableBlocks(List<Class<? extends Block>> applicableBlocks)
    {
        this.applicableBlocks = applicableBlocks;
    }

    /**
     * Create the script binding that will be available in the template to inject information about the transformation.
     *
     * @param block the block being transformed
     * @param transformationContext the transformation context of the document
     * @return the binding
     */
    protected abstract WikiTransformationBinding getWikiTransformationBinding(Block block,
        TransformationContext transformationContext);

    @Override
    public void transform(Block block, TransformationContext context) throws TransformationException
    {
        try {
            // Inject information in the context to perform the transformation
            WikiTransformationBinding transformationBinding = getWikiTransformationBinding(block, context);
            scriptContextManager.getCurrentScriptContext().setAttribute(
                BINDING_TRANSFORMATION, transformationBinding, ScriptContext.ENGINE_SCOPE);

            Block transformationBlock = parser.parse(new StringReader(transformationTemplate));
            transformationManager.performTransformations(transformationBlock, context);

            scriptContextManager.getCurrentScriptContext().removeAttribute(
                BINDING_TRANSFORMATION, ScriptContext.ENGINE_SCOPE);
        } catch (Exception e) {
            throw new TransformationException(String.format("Failed to run transformation on content with Wiki "
                + "Transformation [%s]", this.getRoleHint()), e);
        }
    }
}
