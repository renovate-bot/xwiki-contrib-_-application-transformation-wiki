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

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.transformation.wiki.internal.AbstractWikiTransformationClassInitializer;
import org.xwiki.model.reference.LocalDocumentReference;

import com.xpn.xwiki.objects.classes.BaseClass;

/**
 * Create a new {@link #XCLASS_FULLNAME} class.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named(PatternWikiTransformationClassInitializer.XCLASS_FULLNAME)
public class PatternWikiTransformationClassInitializer extends AbstractWikiTransformationClassInitializer
{
    /**
     * The name of the XClass.
     */
    public static final String XCLASS_NAME = "PatternWikiTransformationClass";

    /**
     * The full name of the XClass.
     */
    public static final String XCLASS_FULLNAME = XCLASS_PREFIX + XCLASS_NAME;

    /**
     * The local reference of the XClass.
     */
    public static final LocalDocumentReference XCLASS_REFERENCE = new LocalDocumentReference(XCLASS_SPACE, XCLASS_NAME);

    /**
     * The pattern property.
     */
    public static final String PROP_PATTERN = "pattern";

    /**
     * Builds a new {@link PatternWikiTransformationClassInitializer}.
     */
    public PatternWikiTransformationClassInitializer()
    {
        super(XCLASS_REFERENCE, XCLASS_NAME);
    }

    @Override
    protected void createWikiTransformationClass(BaseClass xclass)
    {
        xclass.addTextField(PROP_PATTERN, "Pattern", 256);
    }
}
