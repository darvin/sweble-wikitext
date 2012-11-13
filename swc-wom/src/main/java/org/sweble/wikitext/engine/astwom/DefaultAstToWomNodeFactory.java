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

import java.util.LinkedList;

import org.sweble.wikitext.engine.astwom.adapters.BoldAdapter;
import org.sweble.wikitext.engine.astwom.adapters.CommentAdapter;
import org.sweble.wikitext.engine.astwom.adapters.HorizontalRuleAdapter;
import org.sweble.wikitext.engine.astwom.adapters.PageAdapter;
import org.sweble.wikitext.engine.astwom.adapters.ParagraphAdapter;
import org.sweble.wikitext.engine.astwom.adapters.TextAdapter;
import org.sweble.wikitext.engine.config.Namespace;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.parser.nodes.WtBold;
import org.sweble.wikitext.parser.nodes.WtHorizontalRule;
import org.sweble.wikitext.parser.nodes.WtInternalLink;
import org.sweble.wikitext.parser.nodes.WtLinkTarget.LinkTargetType;
import org.sweble.wikitext.parser.nodes.WtNewline;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtNodeList;
import org.sweble.wikitext.parser.nodes.WtParagraph;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.nodes.WtXmlCharRef;
import org.sweble.wikitext.parser.nodes.WtXmlComment;
import org.sweble.wikitext.parser.nodes.WtXmlElement;
import org.sweble.wikitext.parser.nodes.WtXmlEntityRef;
import org.sweble.wikitext.parser.parser.LinkTargetException;
import org.sweble.wikitext.parser.parser.LinkTargetParser;
import org.sweble.wom.WomNode;

import de.fau.cs.osr.ptk.common.AstVisitor;

public class DefaultAstToWomNodeFactory
		extends
			AstVisitor<WtNode>
		implements
			AstToWomNodeFactory
{
	public static final class AstCategory
	{
		private WtNodeList container;
		
		private WtInternalLink link;
		
		public AstCategory(WtNodeList container, WtInternalLink link)
		{
			this.container = container;
			this.link = link;
		}
	}
	
	private final WikiConfig config;
	
	private final String title;
	
	private final Namespace CATEGORY_NAMESPACE;
	
	private final LinkedList<AstCategory> categories = new LinkedList<AstCategory>();
	
	private WtNodeList container;
	
	// =========================================================================
	
	public DefaultAstToWomNodeFactory(
			WikiConfig config,
			String title)
	{
		this.config = config;
		this.title = title;
		
		CATEGORY_NAMESPACE = config.getNamespace("Category");
	}
	
	// =========================================================================
	
	@Override
	public WomNode create(WtNodeList container, WtNode node)
	{
		this.container = container;
		return (WomNode) go(node);
	}
	
	// =========================================================================
	
	public WomNode visit(Page page)
	{
		PageAdapter p = new PageAdapter(this, page, null, null, title);
		
		for (AstCategory c : categories)
			p.setCategory(c.container, c.link);
		
		return p;
	}
	
	public WomNode visit(WtText text)
	{
		return new TextAdapter(text);
	}
	
	public WomNode visit(WtNewline newline)
	{
		return new TextAdapter(newline);
	}
	
	public WomNode visit(WtXmlComment comment)
	{
		return new CommentAdapter(comment);
	}
	
	public WomNode visit(WtParagraph para)
	{
		return new ParagraphAdapter(this, para);
	}
	
	public WomNode visit(WtBold wtBold)
	{
		return new BoldAdapter(this, wtBold);
	}
	
	public WomNode visit(WtHorizontalRule hr)
	{
		return new HorizontalRuleAdapter(hr);
	}
	
	public WomNode visit(WtInternalLink link) throws LinkTargetException
	{
		LinkTargetParser ltp = new LinkTargetParser();
		
		// can throw ... but shouldn't ...
		ltp.parse(config.getParserConfig(), link.getTarget());
		
		LinkTargetType type = config.getParserConfig().classifyTarget(link.getTarget());
		if (type == LinkTargetType.IMAGE)
		{
			// TODO: Generate WtImageLink
			return null;
		}
		else
		{
			if (type != LinkTargetType.PAGE)
				throw new InternalError();
			
			Namespace namespace = null;
			String ns = ltp.getNamespace();
			if (ns != null)
				namespace = config.getNamespace(ns);
			
			if (namespace != null && namespace.equals(CATEGORY_NAMESPACE))
			{
				if (this.container == null)
					throw new RuntimeException("Cannot associate AST category node with its container");
				
				categories.add(new AstCategory(this.container, link));
				return null;
			}
			else
			{
				// TODO: Generate PageLink
				return null;
			}
		}
	}
	
	public WomNode visit(WtXmlElement elem)
	{
		WomNode result = createFromXmlElement(elem);
		if (result == null)
			throw new UnsupportedXmlElement(elem);
		return result;
	}
	
	public WomNode visit(WtXmlEntityRef ref)
	{
		return new TextAdapter(ref);
	}
	
	public WomNode visit(WtXmlCharRef ref)
	{
		return new TextAdapter(ref);
	}
	
	// =========================================================================
	
	private WomNode createFromXmlElement(WtXmlElement e)
	{
		XmlElementFactoryEnum resolved =
				XmlElementFactoryEnum.valueOf(e.getName().toUpperCase());
		if (resolved == null)
			return null;
		
		return resolved.create(this, e);
	}
	
	public enum XmlElementFactoryEnum
	{
		/*
		BLOCKQUOTE
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementBlockquoteAdapter(e);
			}
		},
		
		DIV
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementDivAdapter(e);
			}
		},
		
		PRE
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new PREAdapter(e);
			}
		},
		
		CENTER
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new CENTERAdapter(e);
			}
		},
		*/
		
		P
		{
			@Override
			public WomNode create(
					AstToWomNodeFactory womNodeFactory,
					WtXmlElement e)
			{
				return new ParagraphAdapter(womNodeFactory, e);
			}
		},
		
		HR
		{
			@Override
			public WomNode create(
					AstToWomNodeFactory womNodeFactory,
					WtXmlElement e)
			{
				return new HorizontalRuleAdapter(womNodeFactory, e);
			}
		},
		
		// =====================================================================
		
		/*
		OL
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementOrderedListAdapter(e);
			}
		},
		
		UL
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementUnorderedListAdapter(e);
			}
		},
		
		LI
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementListItemAdapter(e);
			}
		},
		*/
		
		// =====================================================================
		
		/*
		DL
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new DLAdapter(e);
			}
		},

		DD
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new DDAdapter(e);
			}
		},
		
		DT
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new DTAdapter(e);
			}
		},
		*/
		
		// =====================================================================
		
		/*
		TABLE
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new TABLEAdapter(e);
			}
		},
		
		TD
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new TDAdapter(e);
			}
		},
		
		TR
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new TRAdapter(e);
			}
		},
		*/
		
		// =====================================================================
		
		/*
		ABBR
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementAbbrAdapter(e);
			}
		},
		
		BIG
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementBigAdapter(e);
			}
		},
		*/
		
		B
		{
			@Override
			public WomNode create(
					AstToWomNodeFactory womNodeFactory,
					WtXmlElement e)
			{
				return new BoldAdapter(womNodeFactory, e);
			}
		},
		
		/*
		BR
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementBrAdapter(e);
			}
		},
		
		CITE
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementCiteAdapter(e);
			}
		},
		
		CODE
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementCodeAdapter(e);
			}
		},
		
		DFN
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementDfnAdapter(e);
			}
		},
		
		EM
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementEmAdapter(e);
			}
		},
		
		FONT
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new FONTAdapter(e);
			}
		},
		
		I
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementItalicsAdapter(e);
			}
		},
		
		KBD
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementKbdAdapter(e);
			}
		},
		
		S
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new SAdapter(e);
			}
		},
		
		SAMP
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementSampAdapter(e);
			}
		},
		
		SMALL
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementSmallAdapter(e);
			}
		},
		
		SPAN
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementSpanAdapter(e);
			}
		},
		
		STRIKE
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new STRIKEAdapter(e);
			}
		},
		
		STRONG
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementStrongAdapter(e);
			}
		},
		
		SUB
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementSubAdapter(e);
			}
		},
		
		SUP
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementSupAdapter(e);
			}
		},
		
		TT
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementTeletypeAdapter(e);
			}
		},
		
		U
		{
			@Override
			public DomNode create(WtXmlElement e)
			{
				return new UAdapter(e);
			}
		},
		
		VAR
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementVarAdapter(e);
			}
		},
		*/
		
		// =====================================================================
		
		/*
		DEL
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementDelAdapter(e);
			}
		},
		
		INS
		{
			@Override
			public WomNode create(WtXmlElement e)
			{
				return new XmlElementInsAdapter(e);
			}
		}*/;
		
		// =====================================================================
		
		public abstract WomNode create(
				AstToWomNodeFactory womNodeFactory,
				WtXmlElement e);
	}
}
