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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.SpecialSymbolBlock;
import org.xwiki.rendering.block.WordBlock;

/**
 * Matcher for {@link Pattern}.
 *
 * @version $Id$
 * @since 1.0
 */
public class Matcher
{
    private Pattern pattern;

    private Block initialBlock;

    private boolean stopOnNoMatch;

    private boolean matches;

    private LinkedList<Pair<Block, BlockMatcher>> matchedBlockResults;

    private String matchedString = StringUtils.EMPTY;

    private enum InsertionStrategy
    {
        FIRST,
        LAST
    }

    /**
     * Build a new matcher.
     *
     * @param pattern the pattern to use as a reference
     * @param block the block that should be used for starting the match
     */
    public Matcher(Pattern pattern, Block block)
    {
        this(pattern, block, false);
    }

    /**
     * Build a new matcher.
     *
     * @param pattern the pattern to use as a reference
     * @param block the block that should be used for starting the match
     * @param stopOnNoMatch if true, the match will stop directly as soon as one of the blocks doesn't match. This is
     * useful to save time when checking if an expression matches
     */
    public Matcher(Pattern pattern, Block block, boolean stopOnNoMatch)
    {
        this.pattern = pattern;
        this.initialBlock = block;
        this.stopOnNoMatch = stopOnNoMatch;
        this.matchedBlockResults = new LinkedList<>();

        computeMatch();
    }

    /**
     * @return true if the match matches
     */
    public boolean matches()
    {
        return matches;
    }

    /**
     * @return the string corresponding to the matched blocks
     */
    public String getMatchedString()
    {
        if (matchedString.equals(StringUtils.EMPTY)) {
            StringBuilder sb = new StringBuilder();

            for (Pair<Block, BlockMatcher> pair : matchedBlockResults) {
                if (pair.getLeft() instanceof WordBlock) {
                    sb.append(((WordBlock) pair.getLeft()).getWord());
                } else if (pair.getLeft() instanceof SpecialSymbolBlock) {
                    sb.append(((SpecialSymbolBlock) pair.getLeft()).getSymbol());
                } else {
                    sb.append(' ');
                }
            }

            matchedString = sb.toString();
        }

        return matchedString;
    }

    /**
     * @return the list of matched blocks
     */
    public List<Block> getMatchedBlocks()
    {
        return Collections.unmodifiableList(
            matchedBlockResults.stream().map(Pair::getLeft).collect(Collectors.toList()));
    }

    /**
     * @param block the block for which we should get the match result
     * @return the match result, or null if no result exists
     */
    public BlockMatcher getMatchResult(Block block)
    {
        Optional<BlockMatcher> match =
            matchedBlockResults.stream().filter(e -> e.getLeft().equals(block)).map(Pair::getRight).findFirst();
        return match.orElse(null);
    }

    /**
     * @return the matched block results
     */
    public List<Pair<Block, BlockMatcher>> getMatchedBlockResults()
    {
        return Collections.unmodifiableList(matchedBlockResults);
    }

    private void computeMatch()
    {
        // First match against the first matcher block
        matches = checkMatch(initialBlock, pattern.getPrimaryBlockPattern(), InsertionStrategy.FIRST);

        if (matches || !stopOnNoMatch) {
            handleLeftHandMatch();
            handleRightHandMatch();
        }
    }

    private void handleLeftHandMatch()
    {
        // Go back to the start of the expression
        Block currentBlock = initialBlock.getPreviousSibling();
        for (int i = pattern.getPrimaryBlockPatternPosition() - 1; i >= 0 && (matches || !stopOnNoMatch); i--) {
            if (currentBlock != null) {
                matches &= checkMatch(currentBlock, pattern.getBlockPatterns().get(i), InsertionStrategy.FIRST);
                currentBlock = currentBlock.getPreviousSibling();
            } else {
                matches = false;
            }
        }
    }

    private void handleRightHandMatch()
    {
        // Now, go to the end of the expression
        Block currentBlock = initialBlock.getNextSibling();
        for (int i = pattern.getPrimaryBlockPatternPosition() + 1;
            i < pattern.getBlockPatterns().size() && (matches || !stopOnNoMatch); i++) {
            if (currentBlock != null) {
                matches &= checkMatch(currentBlock, pattern.getBlockPatterns().get(i), InsertionStrategy.LAST);
                currentBlock = currentBlock.getNextSibling();
            } else {
                matches = false;
            }
        }
    }

    private boolean checkMatch(Block block, BlockPattern blockPattern, InsertionStrategy insertionStrategy)
    {
        BlockMatcher result = blockPattern.matches(block);

        Pair<Block, BlockMatcher> pair = new ImmutablePair<>(block, result);

        if (insertionStrategy.equals(InsertionStrategy.FIRST)) {
            matchedBlockResults.addFirst(pair);
        } else {
            matchedBlockResults.addLast(pair);
        }

        return result.matches();
    }
}
