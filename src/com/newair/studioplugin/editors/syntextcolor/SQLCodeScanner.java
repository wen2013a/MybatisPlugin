package com.newair.studioplugin.editors.syntextcolor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.*;
import org.eclipse.swt.graphics.Color;


public class SQLCodeScanner extends RuleBasedScanner {

	public SQLCodeScanner() {
		List<IRule> rules = new ArrayList<IRule>();
		// 字符串的规则
		rules.add(new SingleLineRule("\"", "\"", new Token(TextConst.TEXT_ATTR_STRING), '\\'));
		rules.add(new SingleLineRule("'", "'", new Token(TextConst.TEXT_ATTR_STRING), '\\'));
		// 注释的规则
		rules.add(new SingleLineRule("/*", "*/", new Token(TextConst.TEXT_ATTR_COMMENT), '\\'));
		rules.add(new EndOfLineRule("--", new Token(TextConst.TEXT_ATTR_COMMENT), '\\'));
		rules.add(new EndOfLineRule("//", new Token(TextConst.TEXT_ATTR_COMMENT), '\\'));
		//mybatis在脚本中参数规则
		rules.add(new SingleLineRule("#{", "}", new Token(TextConst.TEXT_ATTR_MYBATIS_PARAM), '\\'));
		rules.add(new SingleLineRule("${", "}", new Token(TextConst.TEXT_ATTR_MYBATIS_PARAM), '\\'));
		// 空格的规则
		rules.add(new WhitespaceRule(new IWhitespaceDetector() {
			public boolean isWhitespace(char c) {
				return Character.isWhitespace(c);
			}
		}));
		//关键字规则
		WordRule wordrule = new WordRule(new KeywordDetector(), new Token(new TextAttribute(new Color(null, 0, 0, 0))), true);
		for (int i = 0, n = TextConst.SQL_KEY_WORD.length; i < n; i++) {
			wordrule.addWord(TextConst.SQL_KEY_WORD[i], new Token(TextConst.TEXT_ATTR_KEYWORD));
		}
		rules.add(wordrule);
		// 调用父类中的方法，设置规则
		setRules(rules.toArray(new IRule[0])); 
	}
}
