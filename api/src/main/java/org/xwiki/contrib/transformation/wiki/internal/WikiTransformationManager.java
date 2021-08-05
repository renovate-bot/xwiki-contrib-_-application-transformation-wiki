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
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.transformation.wiki.WikiTransformation;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;

/**
 * Internal manager for working around {@link WikiTransformation}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component(roles = WikiTransformationManager.class)
@Singleton
public class WikiTransformationManager
{
    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    /**
     * Checks if the transformation is applicable to the given entity reference.
     *
     * @param transformation the transformation
     * @param entityReference the entity reference
     * @return true if the transformation is applicable
     */
    public boolean appliesToEntity(WikiTransformation transformation, EntityReference entityReference)
    {
        for (EntityReference applicableEntity : transformation.getApplicableEntities()) {
            if (applicableEntity.getReversedReferenceChain().contains(entityReference)) {
                return true;
            }
        }

        return transformation.getApplicableEntities().isEmpty();
    }

    /**
     * Checks if the transformation is applicable to the given serialized entity reference.
     *
     * @param transformation the transformation
     * @param serializedEntityReference the entity reference
     * @return true if the transformation is applicable
     */
    public boolean appliesToEntity(WikiTransformation transformation, String serializedEntityReference)
    {
        return appliesToEntity(transformation, documentReferenceResolver.resolve(serializedEntityReference));
    }
}
