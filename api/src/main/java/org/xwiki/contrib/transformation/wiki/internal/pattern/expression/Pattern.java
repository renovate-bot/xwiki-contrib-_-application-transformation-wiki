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
    private List<BlockPattern<? extends Block>> blockPatterns = new ArrayList<>();

    private BlockPattern<? extends Block> primaryBlockPattern;

    private int primaryPatternBlockPosition;

    /**
     * Add a new {@link BlockPattern}.
     *
     * @param blockPattern the pattern block to add
     */
    public void addPatternBlock(BlockPattern<? extends Block> blockPattern)
    {
        this.blockPatterns.add(blockPattern);

        // At each new block, try to update the primary pattern block to a special symbol block
        // We try to have a primary block that is as less frequent as possible in documents, in order to start
        // matching on a few elements.
        // We take these patterns in increasing order of priority :
        // * Word patterns
        // * Symbol patterns
        // * Symbol patterns that have a specific symbol defined
        boolean isPrimaryPatternNull = (primaryBlockPattern == null);
        boolean isPrimaryBlockPatternASymbol =
            isPrimaryPatternNull || primaryBlockPattern.getBlockClass().equals(SpecialSymbolBlock.class);
        boolean isBlockPatternASymbol = blockPattern.getBlockClass().equals(SpecialSymbolBlock.class);

        boolean wordToSymbol = (!isPrimaryBlockPatternASymbol && isBlockPatternASymbol);
        boolean specificSymbol = (isPrimaryBlockPatternASymbol && isBlockPatternASymbol
            && ((SpecialSymbolBlockPattern) primaryBlockPattern).getSymbol() == '\u0000'
            && ((SpecialSymbolBlockPattern) blockPattern).getSymbol() != '\u0000');

        if (primaryBlockPattern == null || wordToSymbol || specificSymbol) {
            primaryBlockPattern = blockPattern;
            primaryPatternBlockPosition = blockPatterns.size() - 1;
        }
    }

    /**
     * @return the list of {@link BlockPattern} that constitute the pattern
     */
    public List<BlockPattern<? extends Block>> getBlockPatterns()
    {
        return blockPatterns;
    }

    /**
     * @return the first pattern block that will be matched in the pattern
     */
    public BlockPattern<? extends Block> getPrimaryBlockPattern()
    {
        return primaryBlockPattern;
    }

    /**
     * @return the position of the first pattern block
     */
    public int getPrimaryBlockPatternPosition()
    {
        return primaryPatternBlockPosition;
    }

    /**
     * Checks if the given block and its siblings match the pattern.
     *
     * @param block the block to match
     * @return true if the match succeeds
     */
    public boolean matches(Block block)
    {
        return getMatcher(block, true).matches();
    }

    /**
     * Get the {@link Matcher} corresponding to the given block evaluation.
     *
     * @param block the block to match
     * @return the matcher
     */
    public Matcher getMatcher(Block block)
    {
        return getMatcher(block, false);
    }

    /**
     * Get the {@link Matcher} corresponding to the given block evaluation.
     *
     * @param block the block to match
     * @param stopOnNoMatch true if the match should stop on first no match, useful to save computing resources
     * when performing a lot of matches
     * @return the matcher
     */
    public Matcher getMatcher(Block block, boolean stopOnNoMatch)
    {
        return new Matcher(this, block, stopOnNoMatch);
    }
}
