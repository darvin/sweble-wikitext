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
package org.sweble.wikitext.engine.astwom;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;

import lombok.Getter;

import org.sweble.wikitext.parser.nodes.WtBold;
import org.sweble.wikitext.parser.nodes.WtHorizontalRule;
import org.sweble.wikitext.parser.nodes.WtInternalLink;
import org.sweble.wikitext.parser.nodes.WtItalics;
import org.sweble.wikitext.parser.nodes.WtNewline;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtNodeList;
import org.sweble.wikitext.parser.nodes.WtRedirect;
import org.sweble.wikitext.parser.nodes.WtTagExtension;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.nodes.WtXmlAttribute;
import org.sweble.wikitext.parser.nodes.WtXmlCharRef;
import org.sweble.wikitext.parser.nodes.WtXmlComment;
import org.sweble.wikitext.parser.nodes.WtXmlElement;
import org.sweble.wikitext.parser.nodes.WtXmlEntityRef;

import de.fau.cs.osr.ptk.common.ast.RtData;
import de.fau.cs.osr.utils.XmlGrammar;

public class Toolbox
{
	public static void replaceAstNode(
			WtNodeList container,
			WtNode oldAstNode,
			WtNode newAstNode) throws AssertionError
	{
		ListIterator<WtNode> i = container.listIterator();
		while (i.hasNext())
		{
			WtNode node = i.next();
			if (node == oldAstNode)
			{
				i.set(newAstNode);
				i = null;
				break;
			}
		}
		if (i != null)
			throw new AssertionError();
	}
	
	public static void removeAstNode(WtNodeList container, WtNode astNode)
			throws AssertionError
	{
		Iterator<WtNode> i = container.iterator();
		while (i.hasNext())
		{
			WtNode node = i.next();
			if (node == astNode)
			{
				i.remove();
				i = null;
				break;
			}
		}
		if (i != null)
			throw new AssertionError();
	}
	
	public static void insertAstNode(
			WtNodeList container,
			WtNode astNode,
			WtNode beforeAstNode) throws AssertionError
	{
		ListIterator<WtNode> i = container.listIterator();
		while (i.hasNext())
		{
			WtNode n = i.next();
			if (n == beforeAstNode)
			{
				i.previous();
				i.add(astNode);
				i = null;
				break;
			}
		}
		
		if (i != null)
			throw new AssertionError();
	}
	
	public static void insertAstNodeAfter(
			WtNodeList container,
			WtNode astNode,
			WtNode afterAstNode) throws AssertionError
	{
		ListIterator<WtNode> i = container.listIterator();
		while (i.hasNext())
		{
			WtNode n = i.next();
			if (n == afterAstNode)
			{
				i.add(astNode);
				i = null;
				break;
			}
		}
		
		if (i != null)
			throw new AssertionError();
	}
	
	public static void prependAstNode(WtNodeList container, WtNode astNode)
	{
		container.add(0, astNode);
	}
	
	public static void appendAstNode(WtNodeList container, WtNode astNode)
	{
		container.add(astNode);
	}
	
	public static ListIterator<WtNode> advanceAfter(
			WtNodeList container,
			WtNode node)
	{
		ListIterator<WtNode> i = container.listIterator();
		while (i.hasNext())
		{
			if (i.next() == node)
				return i;
		}
		return null;
	}
	
	public static ListIterator<WtNode> advanceBefore(
			WtNodeList container,
			WtNode node)
	{
		ListIterator<WtNode> i = container.listIterator();
		while (i.hasNext())
		{
			if (i.next() == node)
			{
				i.previous();
				return i;
			}
		}
		return null;
	}
	
	public static void removeAstNode(
			ListIterator<WtNode> i,
			WtNode astNode)
	{
		while (i.hasNext())
		{
			if (i.next() == astNode)
			{
				i.remove();
				return;
			}
		}
		throw new InternalError();
	}
	
	// =========================================================================
	
	@SuppressWarnings("unchecked")
	public static <T> T expectType(Class<T> type, Object obj)
	{
		if (obj != null && !type.isInstance(obj))
			throw new IllegalArgumentException(
					"Expected object of type " + type.getName() + "!");
		return (T) obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T expectType(Class<T> type, Object obj, String argName)
	{
		if (obj != null && !type.isInstance(obj))
			throw new IllegalArgumentException(
					"Expected argument `" + argName + "' to be of type " + type.getName() + "!");
		return (T) obj;
	}
	
	// =========================================================================
	
	public static WtXmlElement addRtData(WtXmlElement n)
	{
		if (n.getEmpty())
		{
			n.setRtd('<', n.getName(), RtData.SEP, " />", RtData.SEP);
		}
		else
		{
			n.setRtd('<', n.getName(), RtData.SEP, '>', RtData.SEP, "</", n.getName(), '>');
		}
		
		for (WtNode attr : n.getXmlAttributes())
			addRtData((WtXmlAttribute) attr);
		
		return n;
	}
	
	public static WtXmlAttribute addRtData(WtXmlAttribute n)
	{
		if (n.getHasValue())
		{
			n.setRtd(' ', n.getName(), "=\"", RtData.SEP, '"');
		}
		else
		{
			n.setRtd(' ', n.getName(), RtData.SEP);
		}
		
		return n;
	}
	
	public static WtBold addRtData(WtBold n)
	{
		n.setRtd("'''", RtData.SEP, "'''");
		return n;
	}
	
	public static WtNode addRtData(WtItalics n)
	{
		n.setRtd("''", RtData.SEP, "''");
		return n;
	}
	
	public static WtInternalLink addRtData(WtInternalLink n)
	{
		n.setRtd("[[", n.getTarget(), RtData.SEP, "]]");
		return n;
	}
	
	public static WtXmlComment addRtData(WtXmlComment n)
	{
		n.setRtd("<!--", n.getContent(), "-->");
		return n;
	}
	
	public static WtHorizontalRule addRtData(WtHorizontalRule n)
	{
		n.setRtd("----");
		return n;
	}
	
	public static WtRedirect addRtData(WtRedirect n)
	{
		n.setRtd("#REDIRECT[[", n.getTarget(), "]]");
		return n;
	}
	
	public static WtTagExtension addRtData(WtTagExtension n)
	{
		for (WtNode attr : n.getXmlAttributes())
		{
			if (attr.getNodeType() != WtNode.NT_XML_ATTRIBUTE)
				continue;
			addRtData((WtXmlAttribute) attr);
		}
		
		n.setRtd("<", n.getName(), RtData.SEP, ">", n.getBody(), "</", n.getName(), ">");
		return n;
	}
	
	// =========================================================================
	
	private static final String validTargetRxStr =
			"(?:[^\\u0000-\\u001F\\u007F\\uFFFD<>{}|\\[\\]/]+)";
	
	private static final String validTitleRxStr =
			"(?:[^\\u0000-\\u001F\\u007F\\uFFFD<>{}|\\[\\]:/]+)";
	
	@Getter(lazy = true)
	private static final Pattern validTargetRx = Pattern.compile(validTargetRxStr);
	
	@Getter(lazy = true)
	private static final Pattern validTitleRx = Pattern.compile(validTitleRxStr);
	
	private static final String validPathRxStr =
			"(?:(?:" + validTitleRxStr + "/)*" + validTitleRxStr + "?)";
	
	@Getter(lazy = true)
	private static final Pattern validPathRx = Pattern.compile(validPathRxStr);
	
	// =========================================================================
	
	public static void checkValidTitle(String title)
			throws UnsupportedOperationException,
			IllegalArgumentException
	{
		if (title == null)
			throw new UnsupportedOperationException("Cannot remove attribute `title'");
		
		if (!getValidTitleRx().matcher(title).matches())
			throw new IllegalArgumentException("Invalid title");
	}
	
	public static void checkValidCategory(String category)
			throws UnsupportedOperationException,
			IllegalArgumentException
	{
		if (category == null)
			throw new UnsupportedOperationException("Cannot remove attribute `category'");
		
		if (!getValidTitleRx().matcher(category).matches())
			throw new IllegalArgumentException("Invalid category");
	}
	
	public static String checkValidNamespace(String namespace)
			throws IllegalArgumentException
	{
		if (namespace == null || namespace.isEmpty())
			return null;
		
		if (!getValidTitleRx().matcher(namespace).matches())
			throw new IllegalArgumentException("Invalid namespace");
		
		return namespace;
	}
	
	/**
	 * Checks for a valid path expression and removes a trailing slash if
	 * present.
	 * 
	 * @param path
	 *            The path to check.
	 * @return The path stripped of a trailing slash if present.
	 * @throws IllegalArgumentException
	 */
	public static String checkValidPath(String path)
			throws IllegalArgumentException
	{
		if (path == null || path.isEmpty())
			return null;
		
		if (path == null || !getValidPathRx().matcher(path).matches())
			throw new IllegalArgumentException("Invalid path");
		
		int l = path.length() - 1;
		return (path.charAt(l) == '/') ? path.substring(0, l) : path;
	}
	
	public static void checkValidTarget(String target)
	{
		if (target == null)
			throw new UnsupportedOperationException("Cannot remove target attribute");
		
		if (!getValidTargetRx().matcher(target).matches())
			throw new IllegalArgumentException("Invalid target");
	}
	
	public static void checkValidXmlName(String name)
	{
		if (name == null)
			throw new NullPointerException("Name cannot be null");
		
		if (!XmlGrammar.xmlName().matcher(name).matches())
			throw new IllegalArgumentException("Not a valid XML Name");
	}
	
	public static void checkValidCommentText(String text)
	{
		if (text == null)
			throw new NullPointerException("WtText cannot be null");
		
		if (!XmlGrammar.xmlCommentText().matcher(text).matches())
			throw new IllegalArgumentException("Not a valid XML Comment text");
	}
	
	// =========================================================================
	
	public static String toText(WtNode n)
	{
		switch (n.getNodeType())
		{
			case WtNode.NT_TEXT:
				return ((WtText) n).getContent();
				
			case WtNode.NT_NEWLINE:
				return ((WtNewline) n).getContent();
				
			case WtNode.NT_XML_COMMENT:
			case WtNode.NT_IGNORED:
				return "";
				
			case WtNode.NT_XML_CHAR_REF:
				return new String(Character.toChars(((WtXmlCharRef) n).getCodePoint()));
				
			case WtNode.NT_XML_ENTITY_REF:
				return ((WtXmlEntityRef) n).getResolved();
				
			default:
				throw new IllegalArgumentException();
		}
	}
}
