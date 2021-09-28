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

import java.util.Arrays;
import java.util.List;

import org.xwiki.model.reference.EntityReference;

import com.xpn.xwiki.doc.AbstractMandatoryClassInitializer;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.TextAreaClass;

/**
 * Abstarct for creating wiki transformations classes. It should be coupled with the
 * {@link AbstractWikiTransformationComponentBuilder}.
 *
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWikiTransformationClassInitializer extends AbstractMandatoryClassInitializer
{
    /**
     * Space in which all the transformation classes should be defined.
     */
    public static final String XCLASS_PREFIX = "Transformation.Code.";

    /**
     * Spaces as a list.
     */
    public static final List<String> XCLASS_SPACE = Arrays.asList("Transformation", "Code");

    /**
     * The ID of the wiki transformation.
     */
    public static final String PROP_ID = "id";

    /**
     * The priority of the wiki transformation.
     */
    public static final String PROP_PRIORITY = "priority";

    /**
     * The template to use when performing the transformation.
     */
    public static final String PROP_TEMPLATE = "template";

    private static final String INTEGER = "integer";

    /**
     * Create a new {@link AbstractWikiTransformationClassInitializer}.
     *
     * @param reference the reference of the class
     * @param name the name of the class
     */
    public AbstractWikiTransformationClassInitializer(EntityReference reference, String name)
    {
        super(reference, name);
    }

    /**
     * Add implementation-specific fields to the new xclass.
     *
     * @param xclass the class to enhance
     */
    protected abstract void createWikiTransformationClass(BaseClass xclass);

    @Override
    protected void createClass(BaseClass xclass)
    {
        xclass.addTextField(PROP_ID, "ID", 128);
        xclass.addNumberField(PROP_PRIORITY, "Priority", 5, INTEGER);
        xclass.addTextAreaField(PROP_TEMPLATE, "Template", 100, 50, TextAreaClass.EditorType.TEXT);

        this.createWikiTransformationClass(xclass);
    }
}
