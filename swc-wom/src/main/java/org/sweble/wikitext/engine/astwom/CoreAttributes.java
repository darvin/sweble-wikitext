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

import org.sweble.wikitext.engine.astwom.adapters.NativeOrXmlAttributeAdapter;
import org.sweble.wom.WomNode;

public enum CoreAttributes implements AttributeDescriptor
{
	ID
	{
		@Override
		public String verify(WomNode parent, String value) throws IllegalArgumentException
		{
			// xs:ID
			// TODO Auto-generated method stub
			return value;
		}
	},
	
	CLASS
	{
		@Override
		public String verify(WomNode parent, String value) throws IllegalArgumentException
		{
			// xs:NMTOKENS
			// TODO Auto-generated method stub
			return value;
		}
	},
	
	STYLE
	{
		@Override
		public String verify(WomNode parent, String value) throws IllegalArgumentException
		{
			// StyleSheet
			// TODO Auto-generated method stub
			return value;
		}
	},
	
	TITLE
	{
		@Override
		public String verify(WomNode parent, String value) throws IllegalArgumentException
		{
			// WtText
			// TODO Auto-generated method stub
			return value;
		}
	};
	
	// =========================================================================
	
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
