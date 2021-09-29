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
package org.xwiki.contrib.transformation.wiki.internal.pattern.expression;

/**
 * A {@link BlockMatcher} for {@link org.xwiki.rendering.block.WordBlock}.
 * It wraps a {@link java.util.regex.Matcher} to easily provide regex matching results to transformations.
 *
 * @version $Id$
 * @since 1.0
 */
public class WordBlockMatcher extends BlockMatcher
{
    private java.util.regex.Matcher matcher;

    /**
     * Build a new {@link WordBlockMatcher}.
     *
     * @param matcher the matcher to use
     */
    public WordBlockMatcher(java.util.regex.Matcher matcher)
    {
        super(matcher.matches());
        this.matcher = matcher;
    }

    /**
     * @return the matcher
     */
    public java.util.regex.Matcher getMatcher()
    {
        return matcher;
    }
}
