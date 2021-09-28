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

import java.util.ArrayList;
import java.util.List;

import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.SpecialSymbolBlock;

/**
 * Defines a wiki transformation pattern. The pattern will allow to match a block and its sibling against a set
 * of rules. See {@link PatternBuilder} for creating new patterns.
 *
 * @version $Id$
 * @since 1.0
 */
public class Pattern
{
    private List<PatternBlock<? extends Block>> patternBlocks = new ArrayList<>();

    private PatternBlock<? extends Block> primaryPatternBlock;

    private int primaryPatternBlockPosition;

    /**
     * Add a new {@link PatternBlock}.
     *
     * @param patternBlock the pattern block to add
     */
    public void addPatternBlock(PatternBlock<? extends Block> patternBlock)
    {
        this.patternBlocks.add(patternBlock);

        // At each new block, try to update the primary pattern block to a special symbol block
        // we consider that by default, documents should have less special symbol blocks than word blocks
        // and as such, matching on the special symbols first should reduce the complexity of the wiki
        // transformation overall.
        if (primaryPatternBlock == null
            || (!primaryPatternBlock.getBlockClass().equals(SpecialSymbolBlock.class)
                && patternBlock.getBlockClass().equals(SpecialSymbolBlock.class))) {
            primaryPatternBlock = patternBlock;
            primaryPatternBlockPosition = patternBlocks.size() - 1;
        }
    }

    /**
     * @return the first pattern block that will be matched in the pattern
     */
    public PatternBlock<? extends Block> getPrimaryPatternBlock()
    {
        return primaryPatternBlock;
    }

    /**
     * Checks if the given block and its siblings match the pattern.
     *
     * @param block the block to match
     * @return true if the match succeeds
     */
    public boolean matches(Block block)
    {
        // First match against the first matcher block
        if (checkMatch(block, primaryPatternBlock)) {
            // Go back to the start of the expression
            Block currentBlock = block.getPreviousSibling();
            for (int i = primaryPatternBlockPosition - 1; i >= 0; i--) {
                if (currentBlock != null && checkMatch(currentBlock, patternBlocks.get(i))) {
                    currentBlock = currentBlock.getPreviousSibling();
                } else {
                    return false;
                }
            }

            // Now, go to the end of the expression
            currentBlock = block.getNextSibling();
            for (int i = primaryPatternBlockPosition + 1; i < patternBlocks.size(); i++) {
                if (currentBlock != null && checkMatch(currentBlock, patternBlocks.get(i))) {
                    currentBlock = currentBlock.getNextSibling();
                } else {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private boolean checkMatch(Block block, PatternBlock patternBlock)
    {
        return patternBlock.getBlockClass().isInstance(block) && patternBlock.matches(block);
    }
}
