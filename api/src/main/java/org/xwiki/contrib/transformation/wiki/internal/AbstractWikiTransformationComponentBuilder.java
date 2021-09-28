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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.wiki.WikiComponent;
import org.xwiki.component.wiki.WikiComponentException;
import org.xwiki.component.wiki.internal.bridge.WikiBaseObjectComponentBuilder;
import org.xwiki.security.authorization.AuthorizationManager;
import org.xwiki.security.authorization.Right;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.objects.BaseObject;

/**
 * Abstract for creating new wiki transformation components.
 *
 * @param <T> the wiki transformation used for this builder
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWikiTransformationComponentBuilder<T extends AbstractWikiTransformation>
    implements WikiBaseObjectComponentBuilder
{
    @Inject
    protected Logger logger;

    @Inject
    protected ComponentManager componentManager;

    @Inject
    protected AuthorizationManager authorizationManager;

    @Inject
    protected Provider<XWikiContext> xWikiContextProvider;

    @Override
    public List<WikiComponent> buildComponents(BaseObject baseObject) throws WikiComponentException
    {

        if (!authorizationManager.hasAccess(Right.PROGRAM, baseObject.getOwnerDocument().getAuthorReference(),
            baseObject.getOwnerDocument().getDocumentReference())) {
            logger.info("User [{}] has insufficient rights to create transformation from [{}]",
                xWikiContextProvider.get().getUserReference(), baseObject.getReference());
            return Collections.emptyList();
        }

        return buildComponentsInternal(baseObject);
    }

    protected abstract List<WikiComponent> buildComponentsInternal(BaseObject baseObject) throws WikiComponentException;
}
