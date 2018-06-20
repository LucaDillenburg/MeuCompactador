import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import compactador.Compactador;
import compactadorArquivo.CompactadorArquivo;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class FrmCompac extends JFrame {

	protected JPanel contentPane;
	protected JPanel pnlGeneral;
	
	protected JLabel lblDillensCompactador;
	protected JButton btnCompactDifDirectory;
	protected JButton btnChooseFile;
	protected JButton btnCompactThere;
	protected JLabel lbFileName;
	
	protected File dialogDefaultDirectory = new File(System.getProperty("user.home"));
	
	protected File selectedFile = null;
	protected boolean compacted = false;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmCompac frame = new FrmCompac();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void changeBtnNames()
	{
	    if(compacted)
	    {
		    btnCompactThere.setText("Extract");
		    btnCompactDifDirectory.setText("Extract in different directory");
	    }else
	    {
	    	 btnCompactThere.setText("Compact");
	    	 btnCompactDifDirectory.setText("Compact in different directory");
	    }
	    
	    btnCompactDifDirectory.setEnabled(true);
	    btnCompactThere.setEnabled(true);
	}
	
	protected void proceduresCompactFile(String saveAs)
	{
		try
		{
			if(compacted)
			{
				String newFileName;
				if(saveAs == null || saveAs == "")
					newFileName = Compactador.descompactar(selectedFile);
				else
					newFileName = Compactador.descompactar(selectedFile, saveAs);
				JOptionPane.showMessageDialog(btnCompactThere, "Successfully compacted in " + newFileName + "!");
			}
			else
			{
				String newFileName;
				if(saveAs == null || saveAs == "")
					newFileName = Compactador.compactar(selectedFile);
				else
					newFileName = Compactador.compactar(selectedFile, saveAs);
				JOptionPane.showMessageDialog(btnCompactThere, "Successfully extracted in " + newFileName + "!");
			}
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(btnCompactThere, "Error: " + e.getMessage());
		}
	}

	/**
	 * Create the frame.
	 */
	public FrmCompac() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		lblDillensCompactador = new JLabel("Dillen's Compactador");
		lblDillensCompactador.setFont(new Font("Vijaya", Font.PLAIN, 25));
		lblDillensCompactador.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblDillensCompactador, BorderLayout.NORTH);
		
		pnlGeneral = new JPanel();
		contentPane.add(pnlGeneral, BorderLayout.CENTER);
		pnlGeneral.setLayout(null);
		
		lbFileName = new JLabel("Select file or folder");
		lbFileName.setHorizontalAlignment(SwingConstants.CENTER);
		lbFileName.setBounds(10, 50, 404, 14);
		pnlGeneral.add(lbFileName);
				
		btnChooseFile = new JButton("Select");
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(dialogDefaultDirectory);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int result = fileChooser.showOpenDialog(btnChooseFile);
				
				if (result == JFileChooser.APPROVE_OPTION)
				{
				    selectedFile = fileChooser.getSelectedFile();
				    lbFileName.setText(selectedFile.getAbsolutePath());
				    
				    dialogDefaultDirectory = selectedFile.getParentFile();
				    
				    compacted = Compactador.estahCompactado(selectedFile);
				    changeBtnNames();
				}
			}
		});
		btnChooseFile.setBounds(161, 11, 102, 28);
		pnlGeneral.add(btnChooseFile);
		
		btnCompactThere = new JButton("Compact");
		btnCompactThere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				proceduresCompactFile(null);
			}
		});
		btnCompactThere.setEnabled(false);
		btnCompactThere.setBounds(147, 108, 116, 31);
		pnlGeneral.add(btnCompactThere);
		
		btnCompactDifDirectory = new JButton("Compact in different directory");
		btnCompactDifDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(dialogDefaultDirectory);
				fileChooser.setDialogTitle("Select a file");
				int result = fileChooser.showSaveDialog(btnChooseFile);
				
				if (result == JFileChooser.APPROVE_OPTION)
				{
					String fileName = fileChooser.getSelectedFile().getPath();
					
					dialogDefaultDirectory = selectedFile.getParentFile();
					
					String dir;
					int indexOf = fileName.lastIndexOf(".");
					if(indexOf < 0)
						dir = fileName;
					else
						dir = fileName.substring(0, indexOf);
					
				    proceduresCompactFile(dir);
				}
			}
		});
		btnCompactDifDirectory.setEnabled(false);
		btnCompactDifDirectory.setBounds(115, 153, 187, 38);
		pnlGeneral.add(btnCompactDifDirectory);
	}
}
