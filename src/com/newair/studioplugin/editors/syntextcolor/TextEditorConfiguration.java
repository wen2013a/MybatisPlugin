package com.newair.studioplugin.editors.syntextcolor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


/**
 * 编辑着色和代码辅助类
 */
public class TextEditorConfiguration extends SourceViewerConfiguration {
	public TextEditorConfiguration() {
	}

	// 覆盖父类中的方法，主要提供代码着色功能
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new SQLCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		return reconciler;
	}

	// 覆盖父类中的方法，主要提供内容辅助功能
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant contentAssistant = new ContentAssistant(); // 创建内容助手对象
		// 设置提示的内容
		contentAssistant.setContentAssistProcessor(new ObjectContentAssistant(), IDocument.DEFAULT_CONTENT_TYPE);
		contentAssistant.enableAutoActivation(true); // 设置自动激活提示
		contentAssistant.setAutoActivationDelay(500); // 设置自动激活提示的时间为500毫秒
		return contentAssistant;
	}
}