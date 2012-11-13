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
package org.sweble.wikitext.engine.astwom.adapters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.sweble.wikitext.engine.astwom.AttributeDescriptor;
import org.sweble.wikitext.engine.astwom.AttributeManagerBase;
import org.sweble.wikitext.engine.astwom.AttributeSupportingElement;
import org.sweble.wikitext.engine.astwom.Toolbox;
import org.sweble.wikitext.engine.astwom.WomBackbone;
import org.sweble.wikitext.parser.nodes.WtInternalLink;
import org.sweble.wikitext.parser.nodes.WtLinkTitle;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtNodeList;
import org.sweble.wom.WomCategory;
import org.sweble.wom.WomNode;
import org.sweble.wom.WomNodeType;
import org.sweble.wom.WomTitle;

import de.fau.cs.osr.utils.Utils;

public class CategoryAdapter
		extends
			AttributeSupportingElement
		implements
			WomCategory
{
	private static final String CATEGORY_ADAPTER_ATTRIBUTE_NAME = CategoryAdapter.class.getName();
	
	private static final String CATEGORY_NAMESPACE = "Category:";
	
	private static final long serialVersionUID = 1L;
	
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private AttributeManagerBase attribManager = AttributeManagerBase.emptyManager();
	
	private static final class AstAnalog
	{
		public final WtNodeList container;
		
		public final WtInternalLink category;
		
		public AstAnalog(WtNodeList container, WtInternalLink category)
		{
			super();
			this.container = container;
			this.category = category;
		}
	}
	
	private LinkedList<AstAnalog> astNodes = new LinkedList<AstAnalog>();
	
	// =========================================================================
	
	/**
	 * Only called by the PageAdapter when a new category is added to the page.
	 * 
	 * @param container
	 *            The AST node containing the category.
	 * @param name
	 *            The name of the category.
	 */
	protected CategoryAdapter(WtNodeList container, String name)
	{
		// Just a dummy node which will get corrected later:
		super(new WtInternalLink("", null, new WtLinkTitle(), ""));
		
		if (container == null)
			throw new NullPointerException();
		
		addAstAnalog(container, getAstNode());
		
		setName(name);
	}
	
	/**
	 * Called when creating a category node from an existing AST node.
	 * 
	 * @param container
	 *            The container in which the AST node of the category was found.
	 * @param category
	 *            The category node in the AST.
	 */
	public CategoryAdapter(WtNodeList container, WtInternalLink category)
	{
		super(category);
		
		if (container == null || category == null)
			throw new NullPointerException();
		
		addAstAnalog(container, category);
		
		setNameUnchecked(getNameFromAst(category));
	}
	
	// =========================================================================
	
	@Override
	public String getNodeName()
	{
		return "category";
	}
	
	@Override
	public WtInternalLink getAstNode()
	{
		return (WtInternalLink) super.getAstNode();
	}
	
	@Override
	public WomNodeType getNodeType()
	{
		return WomNodeType.ELEMENT;
	}
	
	// =========================================================================
	
	@Override
	public String getName()
	{
		return getAttribute("name");
	}
	
	@Override
	public String setName(String name)
	{
		NativeOrXmlAttributeAdapter old = setAttribute(
				Attributes.name,
				"name",
				name);
		
		return (old == null) ? null : old.getValue();
	}
	
	/**
	 * Used whenever the name is set from an AST attribute. In this case, the
	 * value must not be verified and no custom action must be taken since all
	 * involved parties know of the change anyway.
	 */
	protected void setNameUnchecked(String name)
	{
		setAttribute(
				Attributes.NAME_UNCHECKED,
				"name",
				name);
	}
	
	/**
	 * @deprecated
	 */
	@Override
	public String getCategory()
	{
		return getName();
	}
	
	/**
	 * @deprecated
	 */
	@Override
	public String setCategory(String category) throws NullPointerException, IllegalArgumentException
	{
		return setName(category);
	}
	
	@Override
	public WomTitle getLinkTitle()
	{
		return EmptyTitleAdapter.get();
	}
	
	@Override
	public String getLinkTarget()
	{
		return getAstNode().getTarget();
	}
	
	// =========================================================================
	
	/**
	 * Called internally and also called by PageAdapter for each additional AST
	 * category link that points to the an already existing (this) category.
	 * 
	 * After a call to this method, the caller has to set the name if the name's
	 * case of the added category link should differ from the current name.
	 */
	protected void addAstAnalog(WtNodeList container, WtInternalLink link)
	{
		astNodes.add(new AstAnalog(container, link));
		setAstNode(link);
		
		link.setAttribute(CATEGORY_ADAPTER_ATTRIBUTE_NAME, this);
	}
	
	/**
	 * Called by PageAdapter when a category is removed. We are not using
	 * WomBackbone.removeFromAst() since that method is expected to be called on
	 * the parent.
	 */
	protected void removeCategoryAstNodes()
	{
		for (AstAnalog analog : astNodes)
		{
			if (!analog.container.remove(analog.category))
				throw new InternalError();
		}
		
		setAstNode(null);
		astNodes.clear();
	}
	
	protected void rename(
			NativeOrXmlAttributeAdapter oldAttr,
			NativeOrXmlAttributeAdapter newAttr)
	{
		// target can be null when this method is called indirectly by the constructor.
		if (getAstNode().getTarget() != null && getNameFromAst(getAstNode()).equals(newAttr.getValue()))
			return;
		
		renameInAst(newAttr.getValue());
		
		removeRedundantAnalogs();
		
		renameInPage(oldAttr, newAttr);
	}
	
	private void renameInAst(String value)
	{
		WtInternalLink astNode = getAstNode();
		astNode.setTarget(CATEGORY_NAMESPACE + value);
		Toolbox.addRtData(astNode);
	}
	
	private void removeRedundantAnalogs()
	{
		if (astNodes.size() > 1)
		{
			WtInternalLink remaining = getAstNode();
			
			ListIterator<AstAnalog> i = astNodes.listIterator();
			while (i.hasNext())
			{
				AstAnalog analog = i.next();
				if (analog.category != remaining)
				{
					if (!analog.container.remove(analog.category))
						throw new InternalError();
					
					i.remove();
				}
			}
		}
	}
	
	private void renameInPage(
			NativeOrXmlAttributeAdapter oldAttr,
			NativeOrXmlAttributeAdapter newAttr)
	{
		PageAdapter page = getPageAdapter();
		if (page != null)
			page.renameCategory(this, oldAttr.getValue(), newAttr.getValue());
	}
	
	private PageAdapter getPageAdapter()
	{
		WomBackbone root = getParent();
		if (root == null)
			return null;
		
		if (root instanceof PageAdapter)
			return (PageAdapter) root;
		
		throw new InternalError();
	}
	
	// =========================================================================
	
	public static String getNameFromAst(WtInternalLink astNode)
	{
		String name = astNode.getTarget();
		int i = name.lastIndexOf(':');
		return (i >= 0) ? name.substring(i + 1) : name;
	}
	
	public static void reAttachCategoryNodesToPage(WtNode subtree)
	{
		ArrayList<CategoryAdapter> toFix = new ArrayList<CategoryAdapter>();
		scanForCategories(subtree, toFix);
		
		if (!toFix.isEmpty())
		{
			// The page must be the same for every category
			PageAdapter page = toFix.get(0).getPageAdapter();
			for (CategoryAdapter fix : toFix)
				fix.reAttachToPage(page);
		}
	}
	
	private void reAttachToPage(PageAdapter page)
	{
		removeCategoryAstNodes();
		
		WtInternalLink astNode = new WtInternalLink("", null, new WtLinkTitle(), "");
		
		setAstNode(astNode);
		renameInAst(getName());
		
		WtNodeList container = page.reAttachCategory(astNode);
		addAstAnalog(container, astNode);
	}
	
	private static void scanForCategories(
			WtNode subtree,
			ArrayList<CategoryAdapter> toFix)
	{
		for (WtNode n : subtree)
		{
			CategoryAdapter a =
					(CategoryAdapter) n.getAttribute(CATEGORY_ADAPTER_ATTRIBUTE_NAME);
			
			if (a != null)
			{
				toFix.add(a);
			}
			else
			{
				scanForCategories(n, toFix);
			}
		}
	}
	
	// =========================================================================
	
	@Override
	public void acceptsParent(WomBackbone parent)
	{
		throw new UnsupportedOperationException(
				"Cannot add category node! " +
						"Use category manipulation methods on page object instead.");
	}
	
	@Override
	public void childAllowsRemoval(WomBackbone parent)
	{
		throw new UnsupportedOperationException(
				"Cannot remove category node! " +
						"Use category manipulation methods on page object instead.");
	}
	
	// =========================================================================
	
	@Override
	protected AttributeDescriptor getAttributeDescriptor(String name)
	{
		Attributes d = Utils.fromString(Attributes.class, name);
		if (d != null && d != Attributes.NAME_UNCHECKED)
			return d;
		// No other attributes are allowed.
		return null;
	}
	
	private static enum Attributes implements AttributeDescriptor
	{
		name
		{
			@Override
			public String verify(WomNode parent, String value) throws IllegalArgumentException
			{
				Toolbox.checkValidCategory(value);
				
				CategoryAdapter categoryAdapter = (CategoryAdapter) parent;
				if (!value.equalsIgnoreCase(categoryAdapter.getName()))
				{
					WomBackbone pageNode = categoryAdapter.getParent();
					if (pageNode != null)
					{
						if (!(pageNode instanceof PageAdapter))
							throw new InternalError();
						
						PageAdapter page = (PageAdapter) pageNode;
						if (page.hasCategory(value))
							throw new IllegalArgumentException(
									"The page is already assigned to a category called `" + value + "'");
					}
				}
				
				return value;
			}
			
			@Override
			public void customAction(
					WomNode parent,
					NativeOrXmlAttributeAdapter oldAttr,
					NativeOrXmlAttributeAdapter newAttr)
			{
				((CategoryAdapter) parent).rename(oldAttr, newAttr);
			}
		},
		
		/**
		 * This descriptor also describes the "name" attribute, however, it
		 * doesn't verify the attribute's value and doesn't perform any custom
		 * actions (as opposed to setAttributeUnchecked() which only skips the
		 * verification).
		 */
		NAME_UNCHECKED
		{
			@Override
			public String verify(WomNode parent, String value) throws IllegalArgumentException
			{
				return value;
			}
			
			@Override
			public void customAction(
					WomNode parent,
					NativeOrXmlAttributeAdapter oldAttr,
					NativeOrXmlAttributeAdapter newAttr)
			{
			}
		};
		
		@Override
		public boolean syncToAst()
		{
			return false;
		}
		
		@Override
		public Normalization getNormalizationMode()
		{
			return Normalization.NON_CDATA;
		}
		
		@Override
		public boolean isRemovable()
		{
			return false;
		}
	}
}
