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

import lombok.AccessLevel;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

import org.sweble.wikitext.engine.astwom.AstToWomNodeFactory;
import org.sweble.wikitext.engine.astwom.AttributeDescriptor;
import org.sweble.wikitext.engine.astwom.AttributeManagerBase;
import org.sweble.wikitext.engine.astwom.ChildManagerBase;
import org.sweble.wikitext.engine.astwom.GenericAttributeDescriptor;
import org.sweble.wikitext.engine.astwom.NativeOrXmlElement;
import org.sweble.wikitext.engine.astwom.Toolbox;
import org.sweble.wikitext.engine.astwom.UniversalAttributes;
import org.sweble.wikitext.parser.nodes.WtContentNode;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtNodeList;
import org.sweble.wikitext.parser.nodes.WtXmlElement;
import org.sweble.wom.WomUniversalAttributes;

import de.fau.cs.osr.utils.Utils;

public abstract class NativeOrXmlElementWithUniversalAttributes
		extends
			NativeOrXmlElement
{
	private static final long serialVersionUID = 1L;
	
	// =========================================================================
	
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	@Delegate(types = { WomUniversalAttributes.class })
	private AttributeManagerBase attribManager = AttributeManagerBase.emptyManager();
	
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private ChildManagerBase childManager = ChildManagerBase.emptyManager();
	
	// =========================================================================
	
	protected NativeOrXmlElementWithUniversalAttributes(WtNode astNode)
	{
		super(astNode);
	}
	
	protected NativeOrXmlElementWithUniversalAttributes(
			FullElementContentType contentType,
			AstToWomNodeFactory factory,
			WtContentNode astNode)
	{
		super(astNode);
		addContent(contentType, factory, astNode.getContent());
	}
	
	protected NativeOrXmlElementWithUniversalAttributes(
			FullElementContentType contentType,
			String tagName,
			AstToWomNodeFactory factory,
			WtXmlElement astNode)
	{
		super(tagName, astNode);
		addAttributes(astNode.getXmlAttributes());
		addContent(contentType, factory, astNode.getBody());
	}
	
	// =========================================================================
	
	@Override
	public WtNodeList getAstChildContainer()
	{
		return isXml() ? xml().getBody() : ((WtContentNode) getAstNode()).getContent();
	}
	
	@Override
	protected AttributeDescriptor getAttributeDescriptor(String name)
	{
		UniversalAttributes d = Utils.fromString(UniversalAttributes.class, name);
		if (d != null)
			return d;
		return GenericAttributeDescriptor.get();
	}
	
	@Override
	protected WtXmlElement convertToXmlElement()
	{
		return Toolbox.addRtData(new WtXmlElement(
				getNodeName(),
				false,
				new WtNodeList(),
				getAstChildContainer()));
	}
}
