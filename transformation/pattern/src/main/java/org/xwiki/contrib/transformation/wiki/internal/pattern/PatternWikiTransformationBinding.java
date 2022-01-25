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

import java.util.List;

import org.xwiki.contrib.transformation.wiki.internal.WikiTransformationBinding;
import org.xwiki.contrib.xdom.regex.Matcher;
import org.xwiki.contrib.xdom.regex.Pattern;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.TransformationContext;

/**
 * Wiki transformation binding that exposes the pattern and the matcher used in the wiki transformation.
 *
 * @version $Id$
 * @since 1.0
 */
public class PatternWikiTransformationBinding extends WikiTransformationBinding
{
    private static final String PATTERN = "pattern";

    private static final String MATCHER = "matcher";

    /**
     * Build a new {@link WikiTransformationBinding}.
     *
     * @param block the block being transformed
     * @param context the transformation context
     * @param pattern the pattern
     * @param matcher the matcher
     */
    public PatternWikiTransformationBinding(Block block, TransformationContext context,
        Pattern pattern, Matcher matcher)
    {
        super(block, context);

        this.put(PATTERN, pattern);
        this.put(MATCHER, matcher);
    }

    /**
     * @return the pattern
     */
    public Pattern getPattern()
    {
        return (Pattern) this.get(PATTERN);
    }

    /**
     * @return the matcher
     */
    public Matcher getMatcher()
    {
        return (Matcher) this.get(MATCHER);
    }

    @Override
    public void setResult(Block block)
    {
        if (this.containsKey(RESULT)) {
            this.getResult().getParent().replaceChild(block, this.getResult());
        } else {
            // In the case of the pattern wiki transformation, we need to remove every matched block form the final XDOM
            List<Block> matchedBlocks = getMatcher().getMatchedBlocks();

            // We assume that every block has the same parent
            Block parent = matchedBlocks.get(0).getParent();
            parent.replaceChild(block, matchedBlocks.get(0));

            for (int i = 1; i < matchedBlocks.size(); i++) {
                parent.removeBlock(matchedBlocks.get(i));
            }
        }

        this.put(RESULT, block);
    }
}
