
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.util.CloseableUtils;

/**
 * A tokenizer separating input characters on whitespace, but capable of extracting more
 * complex tokens, such as URLs, e-mail addresses and sentence delimiters. Provides
 * {@link TermAttribute}s and {@link PayloadAttribute}s implementing {@link ITokenType}.
 */
public final class ExtendedWhitespaceTokenizer extends Tokenizer
{
    /**
     * Character stream source.
     */
    private Reader reader;

    /**
     * JFlex parser used to split the input into tokens.
     */
    private final ExtendedWhitespaceTokenizerImpl parser;

    /**
     * Reusable object for returning token type.
     */
    private final TokenTypePayload tokenPayload = new TokenTypePayload();

    private TermAttribute term;
    private PayloadAttribute payload;
    
    /**
     * 
     */
    public ExtendedWhitespaceTokenizer(Reader input)
    {
        this.parser = new ExtendedWhitespaceTokenizerImpl(input);
        term = (TermAttribute) addAttribute(TermAttribute.class);
        payload = (PayloadAttribute) addAttribute(PayloadAttribute.class);
        reset(input);
    }
    
    

    @Override
    public boolean incrementToken() throws IOException
    {
        final int tokenType = parser.getNextToken();

        // EOF?
        if (tokenType == ExtendedWhitespaceTokenizerImpl.YYEOF)
        {
            return false;
        }

        tokenPayload.setRawFlags(tokenType);
        payload.setPayload(tokenPayload);
        term.setTermBuffer(parser.yybuffer(), parser.yystart(), parser.yylength());
        term.setTermLength(parser.yylength());
        return true;
    }

    /**
     * Not implemented in this tokenizer. Use {@link #reset()} or {@link #close()}.
     */
    public void reset() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Reset this tokenizer to start parsing another stream.
     */
    public void reset(Reader input)
    {
        if (this.reader != null)
        {
            try
            {
                close();
            }
            catch (IOException e)
            {
                // Fall through, nothing to be done here.
            }
        }

        this.reader = input;
        this.parser.yyreset(input);
    }

    /**
     * 
     */
    public void close() throws IOException
    {
        if (reader != null)
        {
            CloseableUtils.close(reader);
            this.reader = null;
        }
    }
}
