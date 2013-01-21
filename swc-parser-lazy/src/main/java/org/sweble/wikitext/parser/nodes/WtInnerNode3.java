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

package org.sweble.wikitext.parser.nodes;

import org.sweble.wikitext.parser.WtRtData;

import xtc.tree.Location;
import de.fau.cs.osr.ptk.common.ast.AstAbstractInnerNode.AstInnerNode3;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.ptk.common.ast.Uninitialized;

public abstract class WtInnerNode3
		extends
			AstInnerNode3<WtNode>
		implements
			WtNode
{
	private static final long serialVersionUID = 3962368845249207297L;
	
	private WtRtData rtd = null;
	
	// =========================================================================
	
	public WtInnerNode3(Uninitialized u)
	{
		super(u);
	}
	
	public WtInnerNode3(WtNode n0, WtNode n1, WtNode n2)
	{
		super(n0, n1, n2);
	}
	
	public WtInnerNode3(
			Location arg0,
			WtNode n0,
			WtNode n1,
			WtNode n2)
	{
		super(arg0, n0, n1, n2);
	}
	
	public WtInnerNode3(Location arg0)
	{
		super(arg0);
	}
	
	// =========================================================================
	
	@Override
	public WtRtData setRtd(WtRtData rtd)
	{
		if (rtd != null && rtd.size() != this.size() + 1)
			throw new IllegalArgumentException();
		WtRtData old = this.rtd;
		this.rtd = rtd;
		return old;
	}
	
	@Override
	public WtRtData setRtd(Object... glue)
	{
		rtd = new WtRtData(this, glue);
		return rtd;
	}
	
	@Override
	public WtRtData setRtd(String... glue)
	{
		rtd = new WtRtData(this, glue);
		return rtd;
	}
	
	@Override
	public WtRtData getRtd()
	{
		return rtd;
	}
	
	@Override
	public void clearRtd()
	{
		rtd = null;
	}
	
	// =========================================================================
	
	@Override
	public int getPropertyCount()
	{
		return 1;
	}
	
	@Override
	public AstNodePropertyIterator propertyIterator()
	{
		return new WtInnerNode3PropertyIterator();
	}
	
	protected class WtInnerNode3PropertyIterator
			extends
				AstNodePropertyIterator
	{
		@Override
		protected int getPropertyCount()
		{
			return WtInnerNode3.this.getPropertyCount();
		}
		
		@Override
		protected String getName(int index)
		{
			switch (index)
			{
				case 0:
					return "rtd";
					
				default:
					throw new IndexOutOfBoundsException();
			}
		}
		
		@Override
		protected Object getValue(int index)
		{
			switch (index)
			{
				case 0:
					return WtInnerNode3.this.getRtd();
					
				default:
					throw new IndexOutOfBoundsException();
			}
		}
		
		@Override
		protected Object setValue(int index, Object value)
		{
			switch (index)
			{
				case 0:
					return WtInnerNode3.this.setRtd((WtRtData) value);
					
				default:
					throw new IndexOutOfBoundsException();
			}
		}
	}
}