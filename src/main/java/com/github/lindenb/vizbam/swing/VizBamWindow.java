package com.github.lindenb.vizbam.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class VizBamWindow extends JFrame
	{
	private JTextArea textArea=null;
	private JScrollBar hScroll;
	public VizBamWindow()
		{
		super("VizBam");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowOpened(WindowEvent e)
				{
				Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
				VizBamWindow.this.setBounds(50, 50, screen.width-100, screen.height-100);
				}
			@Override
			public void windowClosing(WindowEvent e) {
				doMenuQuit();
				}
			});
		JPanel mainPane=new JPanel(new BorderLayout());
		mainPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		textArea=new JTextArea();
		textArea.setEditable(false);
		mainPane.add(new JScrollPane(this.textArea),BorderLayout.CENTER);
		mainPane.add(this.hScroll=new JScrollBar(JScrollBar.HORIZONTAL),BorderLayout.SOUTH);
		setContentPane(mainPane);
		JMenuBar bar=new JMenuBar();
		setJMenuBar(bar);
		JMenu menu=new JMenu("File");
		bar.add(menu);
		AbstractAction action=new AbstractAction("Close")
			{
			@Override
			public void actionPerformed(ActionEvent ae)
				{
				doMenuQuit();
				}
			};
		menu.add(new JMenuItem(action));
		
		}
	
	public void doMenuQuit()
		{
		this.setVisible(false);
		this.dispose();
		}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
		{
		final VizBamWindow f=new VizBamWindow();
		SwingUtilities.invokeAndWait(new Runnable()
			{
			@Override
			public void run()	
				{
				f.setVisible(true);
				}
			});
		}

}
