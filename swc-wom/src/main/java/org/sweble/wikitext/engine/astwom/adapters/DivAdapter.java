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

import static org.sweble.wikitext.engine.astwom.adapters.FullElementContentType.MIXED_FLOW;
import lombok.AccessLevel;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

import org.sweble.wikitext.engine.astwom.AstToWomNodeFactory;
import org.sweble.wikitext.engine.astwom.AttributeDescriptor;
import org.sweble.wikitext.engine.astwom.AttributeManagerBase;
import org.sweble.wikitext.engine.astwom.AttributeVerifiers;
import org.sweble.wikitext.engine.astwom.CoreAttributes;
import org.sweble.wikitext.engine.astwom.GenericAttributeDescriptor;
import org.sweble.wikitext.parser.nodes.WtXmlElement;
import org.sweble.wom.WomDiv;
import org.sweble.wom.WomHorizAlign;
import org.sweble.wom.WomNode;
import org.sweble.wom.WomUniversalAttributes;

import de.fau.cs.osr.utils.Utils;

public class DivAdapter
		extends
			XmlElementWithChildren
		implements
			WomDiv
{
	private static final long serialVersionUID = 1L;
	
	private static final String TAG_AND_NODE_NAME = "div";
	
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	@Delegate(types = { WomUniversalAttributes.class, AttribAccessors.class })
	private AttributeManagerBase attribManager = AttributeManagerBase.emptyManager();
	
	// =========================================================================
	
	public DivAdapter()
	{
		super(TAG_AND_NODE_NAME);
	}
	
	public DivAdapter(AstToWomNodeFactory womNodeFactory, WtXmlElement astNode)
	{
		super(MIXED_FLOW, TAG_AND_NODE_NAME, womNodeFactory, astNode);
	}
	
	// =========================================================================
	
	@Override
	public String getNodeName()
	{
		return TAG_AND_NODE_NAME;
	}
	
	// =========================================================================
	
	@Override
	protected AttributeDescriptor getAttributeDescriptor(String name)
	{
		AttributeDescriptor d = Utils.fromString(Attributes.class, name);
		if (d != null)
			return d;
		d = Utils.fromString(CoreAttributes.class, name);
		if (d != null)
			return d;
		return GenericAttributeDescriptor.get();
	}
	
	private static enum Attributes implements AttributeDescriptor
	{
		align
		{
			@Override
			public String verify(WomNode parent, String value) throws IllegalArgumentException
			{
				return AttributeVerifiers.LCRJ_ALIGN.verify(parent, value);
			}
		};
		
		// =====================================================================
		
		@Override
		public boolean isRemovable()
		{
			return true;
		}
		
		@Override
		public boolean syncToAst()
		{
			return true;
		}
		
		@Override
		public Normalization getNormalizationMode()
		{
			return Normalization.NON_CDATA;
		}
		
		@Override
		public void customAction(
				WomNode parent,
				NativeOrXmlAttributeAdapter oldAttr,
				NativeOrXmlAttributeAdapter newAttr)
		{
		}
	}
	
	private interface AttribAccessors
	{
		WomHorizAlign getAlign();
		
		WomHorizAlign setAlign(WomHorizAlign align);
	}
}
