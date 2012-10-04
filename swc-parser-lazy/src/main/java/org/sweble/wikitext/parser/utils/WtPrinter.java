/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sweble.wikitext.parser.utils;

import java.io.StringWriter;
import java.io.Writer;

import org.sweble.wikitext.parser.nodes.WtContentNodeMarkTwo;
import org.sweble.wikitext.parser.nodes.WtNode;

import de.fau.cs.osr.ptk.common.AstPrinter;
import de.fau.cs.osr.utils.PrinterBase.Memoize;
import de.fau.cs.osr.utils.PrinterBase.OutputBuffer;

public class WtPrinter
		extends
			AstPrinter<WtNode>
{
	public void visit(WtContentNodeMarkTwo n)
	{
		Memoize m = p.memoizeStart(n);
		if (m != null)
		{
			if (hasVisibleProperties(n))
			{
				// FIXME: Temporary!
				p.indent(n.getNodeName());
				p.println('(');
				
				p.incIndent();
				if (hasVisibleProperties(n))
				{
					printProperties(n);
					if (!n.isEmpty())
						p.needNewlines(2);
				}
				
				boolean singleLine = false;
				if (isCompact() && n.size() <= 1)
				{
					OutputBuffer b = p.outputBufferStart();
					printListOfNodes(n);
					b.stop();
					
					String output = b.getBuffer().trim();
					if (isSingleLine(output))
					{
						p.indent("[ ");
						p.print(output);
						p.println(" ]");
						singleLine = true;
					}
				}
				
				if (!singleLine)
				{
					p.indentln('[');
					
					p.incIndent();
					printListOfNodes(n);
					p.decIndent();
					
					p.indentln(']');
				}
				p.decIndent();
				
				p.indentln(')');
			}
			else if (n.isEmpty())
			{
				p.indent(n.getClass().getSimpleName());
				p.println("[]");
			}
			else
			{
				boolean singleLine = false;
				if (isCompact() && n.size() <= 1)
				{
					OutputBuffer b = p.outputBufferStart();
					printListOfNodes(n);
					b.stop();
					
					String output = b.getBuffer().trim();
					if (isSingleLine(output))
					{
						p.indent(n.getClass().getSimpleName());
						p.print("([ ");
						p.print(output);
						p.println(" ])");
						singleLine = true;
					}
				}
				
				if (!singleLine)
				{
					p.indent(n.getClass().getSimpleName());
					p.println("([");
					
					p.incIndent();
					printListOfNodes(n);
					p.decIndent();
					
					p.indentln("])");
				}
			}
			p.memoizeStop(m);
		}
	}
	
	// =========================================================================
	
	public static String print(WtNode node)
	{
		return WtPrinter.print(new StringWriter(), node).toString();
	}
	
	public static Writer print(Writer writer, WtNode node)
	{
		new WtPrinter(writer).go(node);
		return writer;
	}
	
	// =========================================================================
	
	public WtPrinter(Writer writer)
	{
		super(writer);
	}
}
