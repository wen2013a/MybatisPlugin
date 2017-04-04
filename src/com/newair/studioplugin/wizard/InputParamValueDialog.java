package com.newair.studioplugin.wizard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.newair.studioplugin.db.DbUtil;

public class InputParamValueDialog extends Dialog {

	protected Object[] results;
	protected Shell shell;
	private String[] sourceparams;
	private String[] newparams;
	private List<Object> edits = new ArrayList<Object>();
	private Composite composite;
	private ScrolledComposite scrolledComposite;

	public InputParamValueDialog(Shell parent, int style, String[] params) {
		super(parent, style);
		this.sourceparams = params;
		List<String> paramlist = new ArrayList<String>();
		for (int i = 0; i < sourceparams.length; i++) {
			String param = sourceparams[i];
			int k = param.indexOf(",");
			if (k < 0)
				k = param.length() - 1;
			String paramname = StringUtils.trim(param.substring(2, k));
			boolean flag = false;
			for (int j = 0; j < paramlist.size(); j++) {
				String newparam = paramlist.get(j);
				k = newparam.indexOf(",");
				if (k < 0)
					k = newparam.length() - 2;
				String newparamname = StringUtils.trim(newparam.substring(2, k));
				if (paramname.equals(newparamname)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				paramlist.add(param);
			}
		}
		//排重复
		newparams = paramlist.toArray(new String[] {});
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return results;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
		shell.setSize(391, 272);
		shell.setText("输入参数值");
		shell.setLayout(new FormLayout());

		Button butOk = new Button(shell, SWT.NONE);
		FormData fd_butOk = new FormData();
		fd_butOk.right = new FormAttachment(0, 178);
		fd_butOk.left = new FormAttachment(0, 113);
		fd_butOk.top = new FormAttachment(100, -35);
		fd_butOk.bottom = new FormAttachment(100, -5);
		butOk.setLayoutData(fd_butOk);
		butOk.addMouseListener(new ButOkMouseAdapter());
		butOk.setText("  确 定  ");

		Button butCancel = new Button(shell, SWT.NONE);
		FormData fd_butCancel = new FormData();
		fd_butCancel.right = new FormAttachment(0, 265);
		fd_butCancel.left = new FormAttachment(0, 200);
		fd_butCancel.top = new FormAttachment(100, -35);
		fd_butCancel.bottom = new FormAttachment(100, -5);
		butCancel.setLayoutData(fd_butCancel);
		butCancel.addMouseListener(new ButCancelMouseAdapter());
		butCancel.setText("  取 消  ");

		scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.left = new FormAttachment(0, 5);
		fd_scrolledComposite.right = new FormAttachment(100, -5);
		fd_scrolledComposite.top = new FormAttachment(0, 10);
		fd_scrolledComposite.bottom = new FormAttachment(butOk, -6);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		composite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		bindData();
	}

	private void bindData() {
		for (int i = 0; i < newparams.length; i++) {
			String s = newparams[i].trim();
			s = s.substring(2);
			s = s.substring(0, s.length() - 1);
			String[] values = new String[] { "", "", "" };
			String[] ss = s.split(",");
			if (ss.length >= 1) {
				values[0] = ss[0];
			}
			if (ss.length >= 2) {
				String[] sss = ss[1].split("=");
				values[1] = sss[1];
			}
			String type = DbUtil.getJavaClass(DbUtil.getJdbcTypeByName(values[1]));

			Label label = new Label(composite, SWT.NONE);
			label.setBounds(10, 10 + i * 28, 100, 17);
			label.setText(values[0]);

			if (java.lang.Boolean.class.getName().equals(type)) {
				Combo combo = new Combo(composite, SWT.NONE);
				combo.setItems(new String[] {"true", "false"});
				combo.setBounds(120, 7 + i * 28, 200, 23);
				edits.add(combo);
			} else if (java.util.Date.class.getName().equals(type) 
					|| java.sql.Date.class.getName().equals(type)
					|| java.sql.Time.class.getName().equals(type)
					|| java.sql.Timestamp.class.getName().equals(type)) {
				CDateTime dateTime = new CDateTime(composite, CDT.DATE_LONG | CDT.TIME_MEDIUM | CDT.DROP_DOWN);
				FormData fd_dateTime = new FormData();
				dateTime.setLayoutData(fd_dateTime);
				dateTime.setBounds(120, 7 + i * 28, 200, 23);
				dateTime.setPattern("yyyy-MM-dd HH:mm:ss");
				dateTime.setSelection(null);
				edits.add(dateTime);
			} else {
				Text text = new Text(composite, SWT.BORDER);
				text.setBounds(120, 7 + i * 28, 200, 23);
				edits.add(text);
			}
		}
		if (composite != null) {
			scrolledComposite.setContent(composite);
			scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
	}

	private class ButOkMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			results = new Object[sourceparams.length];
			for (int i = 0; i < newparams.length; i++) {
				String s = newparams[i].trim();
				s = s.substring(2);
				s = s.substring(0, s.length() - 1);
				String[] values = new String[] { "", "", "" };
				String[] ss = s.split(",");
				if (ss.length >= 1) {
					values[0] = ss[0];
				}
				if (ss.length >= 2) {
					String[] sss = ss[1].split("=");
					values[1] = sss[1];
				}
				String type = DbUtil.getJavaClass(DbUtil.getJdbcTypeByName(values[1]));
				
				Object edit = edits.get(i);
				Object rs = null;
				if (edit instanceof Text) {
					rs = ((Text)edit).getText();
				} else if (edit instanceof Combo) {
					if (((Combo)edit).getSelectionIndex() == 0) {
						rs = true;
					} else if (((Combo)edit).getSelectionIndex() == 1) {
						rs = false;
					}
				} else if (edit instanceof CDateTime) {
					Date date = ((CDateTime)edit).getSelection();
					if (date != null) {
						if (java.sql.Date.class.getName().equals(type)) {
							java.sql.Date d = new java.sql.Date(date.getTime());
							rs = d;
						} else if (java.sql.Time.class.getName().equals(type)) {
							java.sql.Time d = new java.sql.Time(date.getTime());
							rs = d;
						} else if (java.sql.Timestamp.class.getName().equals(type)) {
							java.sql.Timestamp d = new java.sql.Timestamp(date.getTime());
							rs = d;
						} else {
							java.sql.Timestamp d = new Timestamp(date.getTime());
							rs = d;
						}
					}
				}
				for (int j = 0; j < sourceparams.length; j++) {
					if (newparams[i].equals(sourceparams[j])) {
						results[j] = rs;
					}
				}
			}
			shell.close();
		}
	}

	private class ButCancelMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			results = null;
			shell.close();
		}
	}
}
