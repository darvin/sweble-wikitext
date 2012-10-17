package org.sweble.wikitext.engine.output;

import java.io.Writer;
import java.util.ArrayList;

import org.sweble.wikitext.parser.nodes.WtNode;

import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.utils.PrinterBase;
import de.fau.cs.osr.utils.StringUtils;

public class HtmlRendererBase
		extends
			AstVisitor<WtNode>
{
	protected final PrinterBase p;
	
	// =========================================================================
	
	protected HtmlRendererBase(Writer writer)
	{
		this.p = new PrinterBase(writer);
		this.p.setMemoize(false);
	}
	
	// =========================================================================
	
	protected static String esc(String content)
	{
		return StringUtils.escHtml(content);
	}
	
	protected static String esc(String content, boolean forAttribute)
	{
		return StringUtils.escHtml(content, forAttribute);
	}
	
	protected static String capitalize(String text)
	{
		return org.apache.commons.lang.StringUtils.capitalize(text);
	}
	
	// =========================================================================
	
	/**
	 * Print Formatted
	 */
	protected void pf(String format, Object... args)
	{
		p.print(String.format(format, args));
	}
	
	/**
	 * Print Tree
	 */
	protected void pt(String format, Object... args)
	{
		ArrayList<Object> a = new ArrayList<Object>(args.length);
		
		int arg = 0;
		int last = 0;
		for (int i = 0; i < format.length(); ++i)
		{
			char ch = format.charAt(i);
			if (ch == '%' && format.length() > i + 1)
			{
				char ch2 = format.charAt(i + 1);
				switch (ch2)
				{
					case '!':
					{
						printPart(format, a, last, i);
						last = i + 2;
						
						WtNode n = (WtNode) args[arg++];
						if (n.isList())
						{
							iterate(n);
						}
						else
						{
							dispatch(n);
						}
						break;
					}
					
					case '=': // Escape string for HTML
					case '~': // Escape string for HTML attribute value
					{
						printPart(format, a, last, i);
						last = i + 2;
						
						String s = (String) args[arg++];
						p.print(esc(s, ch2 == '~'));
						
						break;
					}
					
					case '%':
						break;
					
					default:
						a.add(args[arg++]);
						break;
				}
				
				++i;
			}
		}
		
		printPart(format, a, last, format.length());
	}
	
	private void printPart(String format, ArrayList<Object> a, int last, int i)
	{
		if (i - last > 0)
		{
			String f = format.substring(last, i);
			if (a.isEmpty())
			{
				p.print(f);
			}
			else
			{
				p.print(String.format(f, a.toArray()));
				a.clear();
			}
		}
	}
}
