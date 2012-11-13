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

package org.sweble.wikitext.engine.wom.tools;

import org.sweble.wikitext.engine.astwom.adapters.BodyAdapter;
import org.sweble.wikitext.engine.astwom.adapters.BoldAdapter;
import org.sweble.wikitext.engine.astwom.adapters.CommentAdapter;
import org.sweble.wikitext.engine.astwom.adapters.HorizontalRuleAdapter;
import org.sweble.wikitext.engine.astwom.adapters.PageAdapter;
import org.sweble.wikitext.engine.astwom.adapters.TextAdapter;
import org.sweble.wom.WomBody;
import org.sweble.wom.WomBold;
import org.sweble.wom.WomComment;
import org.sweble.wom.WomHorizontalRule;
import org.sweble.wom.WomNode;
import org.sweble.wom.WomPage;

/**
 * Builder for a WOM tree using fluent interfaces.
 */
public class AstWomBuilder
{
	public static WomPageBuilder womPage()
	{
		return new WomPageBuilder();
	}
	
	public static WomBodyBuilder womBody()
	{
		return new WomBodyBuilder();
	}
	
	public static WomCommentBuilder womComment()
	{
		return new WomCommentBuilder();
	}
	
	public static WomHorizontalRuleBuilder womHr()
	{
		return new WomHorizontalRuleBuilder();
	}
	
	public static WomBoldBuilder womBold()
	{
		return new WomBoldBuilder();
	}
	
	public static WomTextBuilder womText()
	{
		return new WomTextBuilder();
	}
	
	// =========================================================================
	
	public static final class WomCommentBuilder
	{
		private String text = " Default Comment WtText ";
		
		public WomCommentBuilder withText(String text)
		{
			this.text = text;
			return this;
		}
		
		public WomComment build()
		{
			return new CommentAdapter(text);
		}
	}
	
	public static final class WomBodyBuilder
	{
		private WomBody body = new BodyAdapter();
		
		public WomBodyBuilder withContent(WomNode... contents)
		{
			for (WomNode n : contents)
				this.body.appendChild(n);
			return this;
		}
		
		public WomBody build()
		{
			return this.body;
		}
	}
	
	public static final class WomPageBuilder
	{
		private String namespace = null;
		
		private String path = null;
		
		private String title = "Default Page";
		
		private WomBody body = null;
		
		public WomPageBuilder withBody(WomNode... contents)
		{
			this.body = new BodyAdapter();
			for (WomNode n : contents)
				this.body.appendChild(n);
			return this;
		}
		
		public WomPageBuilder withNamespace(String ns)
		{
			this.namespace = ns;
			return this;
		}
		
		public WomPageBuilder withPath(String path)
		{
			this.path = path;
			return this;
		}
		
		public WomPageBuilder withTitle(String title)
		{
			this.title = title;
			return this;
		}
		
		public WomPage build()
		{
			PageAdapter page = new PageAdapter(this.namespace, this.path, this.title);
			if (this.body != null)
				page.setBody(this.body);
			return page;
		}
	}
	
	public static final class WomHorizontalRuleBuilder
	{
		private HorizontalRuleAdapter hr = new HorizontalRuleAdapter();
		
		public WomHorizontalRule build()
		{
			return hr;
		}
	}
	
	public static final class WomBoldBuilder
	{
		public WomBold build()
		{
			return new BoldAdapter();
		}
	}
	
	public static final class WomTextBuilder
	{
		private String text;
		
		public WomTextBuilder withText(String text)
		{
			this.text = text;
			return this;
		}
		
		public WomNode build()
		{
			return new TextAdapter(this.text);
		}
	}
}
