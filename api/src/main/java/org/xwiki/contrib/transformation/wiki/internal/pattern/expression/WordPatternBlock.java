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

import org.apache.commons.lang3.StringUtils;
import org.xwiki.rendering.block.WordBlock;

/**
 * Pattern block for {@link WordBlock}.
 *
 * @version $Id$
 * @since 1.0
 */
public class WordPatternBlock implements PatternBlock<WordBlock>
{
    private String stringPattern = StringUtils.EMPTY;

    private java.util.regex.Pattern pattern;

    /**
     * Add a new character to the string pattern.
     *
     * @param c the char to add
     */
    public void addChar(char c)
    {
        stringPattern = stringPattern + c;
    }

    @Override
    public Class<WordBlock> getBlockClass()
    {
        return WordBlock.class;
    }

    @Override
    public boolean matches(WordBlock block)
    {
        return pattern.matcher(block.getWord()).find();
    }

    /**
     * Build the pattern based on the string created through the successive calls to {@link #addChar(char)}.
     */
    public void buildPattern()
    {
        pattern = java.util.regex.Pattern.compile(stringPattern);
    }
}
