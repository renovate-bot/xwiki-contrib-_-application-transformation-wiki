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

import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.SpecialSymbolBlock;

/**
 * Pattern block for a {@link SpecialSymbolBlock}.
 *
 * @version $Id$
 * @since 1.0
 */
public class SpecialSymbolBlockPattern implements BlockPattern<SpecialSymbolBlock>
{
    private char symbol;

    /**
     * Create a new {@link SpecialSymbolBlockPattern} bound to no particular symbol.
     */
    public SpecialSymbolBlockPattern()
    {

    }

    /**
     * Create a new {@link SpecialSymbolBlockPattern}, binding it to a specific symbol.
     *
     * @param symbol the symbol to match
     */
    public SpecialSymbolBlockPattern(char symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @return the symbol that will be matched or '\u0000' for any symbol
     */
    public char getSymbol()
    {
        return symbol;
    }

    @Override
    public Class<SpecialSymbolBlock> getBlockClass()
    {
        return SpecialSymbolBlock.class;
    }

    @Override
    public BlockMatcher matches(Block block)
    {
        return new BlockMatcher(block instanceof SpecialSymbolBlock
            && (symbol == '\u0000' || symbol == ((SpecialSymbolBlock) block).getSymbol()));
    }
}
