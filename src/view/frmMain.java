/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.Define;
import control.ImageCluster2;
import control.ImageClusterFCM;
import control.LuatMo2;
import control.MatrixPixel;
import control.Pixel;
import control.Star;
import control.Transformations;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.CustomTreeCellRender;
import model.CustomTreeNode;
import model.ImageImplement;
import model.ImageModel;
import model.NodeItem;
import model.PDFModel;
import model.TimeSpan;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author
 */
public final class frmMain extends javax.swing.JFrame {

	/**
	 * Creates new form frmMain
	 */
	public frmMain() {
		initComponents();
		treeListFile.setModel(null);
		setLocationRelativeTo(null);
	}

	static int algorithm = 0;	
	String directory;
	String timeForecast = "";
	Object currentWindow = this;
	ArrayList<String> paths;
	static int numberImage;
	Thread myThread;
	boolean isInProcess = false;
	ArrayList<ImageModel> imagesAfterCluster;
	ArrayList<ImageModel> imagesForecast;
	ImageImplement panelImage;
	BufferedImage image;
	JLabel labImage;
	int currentImage = 0;
	boolean isPlay = false;
	boolean isPlause = false;
	CustomTreeNode root;
	CustomTreeNode parents;
	CustomTreeNode newParents;
	CustomTreeNode forcastParents;
	Timer timer;
	int typeShow = -1;
	Timer timerProcess; // thoi gian xu ly du bao
	TimeSpan totalTimeProc; // tong thoi gian du bao

	private void initTimerProc() {
		totalTimeProc = new TimeSpan(0);
		timerProcess = new Timer(1000, actListnerProc);
	}

	ActionListener actListnerProc = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			totalTimeProc = totalTimeProc.Add(new TimeSpan(10000000));

			labTime.setText("Run time: " + totalTimeProc.toString());
		}
	};

	private void pauseTimer(Timer timer) throws InterruptedException {
		timer.wait();
	}

	private void stopTimer(Timer timer) {
		timer.stop();
	}

	private void startTimer(Timer timer) {
		// timer = new Timer(1000, actListnerProc);
		timer.start();
	}

	private void restartTimer(Timer timer) {
		timer.restart();
	}

	public void configure() throws IOException, SAXException,
			ParserConfigurationException, ParseException {
		try {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showDialog(this, null);

			File fileRoot = fc.getSelectedFile();
			paths = new ArrayList<>();

			if (fileRoot == null) {
				return;
			}
			directory = fileRoot.getPath();
			Define.BASEDPATH = directory + Define.BASEDPATH;
			Define.path = Define.BASEDPATH.replace("\\", "/");
			NodeItem folder1 = new NodeItem("Data", Define.BASEDPATH, "", true);
			parents = new CustomTreeNode(folder1);
			parents.setId(0);
			root = new CustomTreeNode(new NodeItem("Root", "", "", true));
			root.add(parents);
			boolean f = new File(Define.BASEDPATH).mkdirs();

			NodeItem folder = new NodeItem(fileRoot.getName(),
					fileRoot.getPath(), "", true);
			parents = new CustomTreeNode(new ImageIcon(ImageIO.read(getClass()
					.getResource("/Image/folder.png"))), folder);
			parents.setId(1);
			root = new CustomTreeNode(new ImageIcon(ImageIO.read(getClass()
					.getResource("/Image/tree.png"))), new NodeItem("Root", "",
					"", true));
			root.add(parents);
			DefaultTreeModel model = new DefaultTreeModel(root);

			File[] subItems = fileRoot.listFiles();
			int index = 0;
			File data = null;
			for (File file : subItems) {
				String ext = frmMain.getExtension(file);
				if (null != ext) {
					switch (ext) {
					case "jpg":
						NodeItem image = new NodeItem(file.getName(),
								file.getPath(), "", false);
						CustomTreeNode node = new CustomTreeNode(new ImageIcon(
								ImageIO.read(getClass().getResource(
										"/Image/image.png"))), image);
						node.setId(index);
						parents.add(node);
						paths.add(file.getPath());
						index++;
						break;
					case "xml":
						data = file;
						break;
					}
				}
			}
			if (data.isFile()) {
				loadDataImages(data);
			} else {
				JOptionPane.showMessageDialog(this, "Can't load file data.xml");
			}
			treeListFile.setModel(model);
			treeListFile.setCellRenderer(new CustomTreeCellRender());
			treeListFile.setRowHeight(25);
			initProcess();
			timer = new Timer(2000, null);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Have error when load folder! Please check again!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadDataImages(File file) throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuider = fac.newDocumentBuilder();
			Document doc = (Document) docBuider.parse(file);
			NodeList root = doc.getElementsByTagName("image");
			numberImage = root.getLength();
			for (int i = 0; i < root.getLength(); i++) {
				Element e = (Element) root.item(i);
				NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
						.getNodeItem();
				item.setTime(e.getElementsByTagName("time").item(0)
						.getTextContent());
				item.setId(Integer.parseInt(((Element) root.item(i))
						.getAttribute("id")));
			}

			NodeList forecastNode = doc.getElementsByTagName("forecast");
			timeForecast = ((Element) forecastNode.item(0))
					.getElementsByTagName("time").item(0).getTextContent();
		} catch (DOMException e) {
			System.out.printf("Have error");
		}
	}

	private void setTimeNodeItem(String startTime, String stepTime)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar calStart = Calendar.getInstance();
		Calendar calStep = Calendar.getInstance();
		Date start = dateFormat.parse(startTime);
		Date step = dateFormat.parse(stepTime);
		calStep.setTime(step);
		for (int i = 0; i < parents.getChildCount(); i++) {
			calStart.setTime(start);
			calStart.add(Calendar.SECOND, calStep.get(Calendar.SECOND) * i);
			calStart.add(Calendar.MINUTE, calStep.get(Calendar.MINUTE) * i);
			calStart.add(Calendar.HOUR, calStep.get(Calendar.HOUR) * i);
			NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
					.getNodeItem();
			item.setTime(dateFormat.format(calStart.getTime()));
		}
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panRight = new javax.swing.JPanel();
        panLeft = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeListFile = new javax.swing.JTree();
        btnCluster = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnForecast = new javax.swing.JButton();
        btnSetting = new javax.swing.JButton();
        panBottom = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        probarThread = new javax.swing.JProgressBar();
        labTime = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        panScrollCluster = new javax.swing.JScrollPane();
        panCluster = new javax.swing.JPanel();
        txtTimeImage = new javax.swing.JLabel();
        txtShowMore = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        mainMenu = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        miSelected = new javax.swing.JMenuItem();
        miCluster = new javax.swing.JMenuItem();
        miForecast = new javax.swing.JMenuItem();
        miSetting = new javax.swing.JMenuItem();
        meExport = new javax.swing.JMenu();
        miImage = new javax.swing.JMenuItem();
        miPDF = new javax.swing.JMenuItem();
        miPrint = new javax.swing.JMenuItem();
        miClose = new javax.swing.JMenuItem();
        mHelp = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();
        miHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Forecast the weather");
        setMinimumSize(new java.awt.Dimension(900, 500));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        panRight.setBackground(new java.awt.Color(255, 255, 255));
        panRight.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panRight.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panRightComponentResized(evt);
            }
        });

        javax.swing.GroupLayout panRightLayout = new javax.swing.GroupLayout(panRight);
        panRight.setLayout(panRightLayout);
        panRightLayout.setHorizontalGroup(
            panRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panRightLayout.setVerticalGroup(
            panRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panLeft.setBorder(javax.swing.BorderFactory.createTitledBorder("List of Files"));
        panLeft.setName(""); // NOI18N

        treeListFile.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeListFileValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(treeListFile);

        javax.swing.GroupLayout panLeftLayout = new javax.swing.GroupLayout(panLeft);
        panLeft.setLayout(panLeftLayout);
        panLeftLayout.setHorizontalGroup(
            panLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
        );
        panLeftLayout.setVerticalGroup(
            panLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        btnCluster.setBackground(new java.awt.Color(255, 255, 255));
        btnCluster.setForeground(new java.awt.Color(240, 240, 240));
        btnCluster.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/cluster.png"))); // NOI18N
        btnCluster.setToolTipText("Cluster");
        btnCluster.setBorder(null);
        btnCluster.setContentAreaFilled(false);
        btnCluster.setEnabled(false);
        btnCluster.setFocusTraversalPolicyProvider(true);
        btnCluster.setPreferredSize(new java.awt.Dimension(45, 45));
        btnCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClusterActionPerformed(evt);
            }
        });

        btnOpen.setForeground(new java.awt.Color(240, 240, 240));
        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/open.png"))); // NOI18N
        btnOpen.setToolTipText("Open");
        btnOpen.setBorder(null);
        btnOpen.setContentAreaFilled(false);
        btnOpen.setFocusTraversalPolicyProvider(true);
        btnOpen.setPreferredSize(new java.awt.Dimension(45, 45));
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        btnForecast.setBackground(new java.awt.Color(255, 255, 255));
        btnForecast.setForeground(new java.awt.Color(240, 240, 240));
        btnForecast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/weather.png"))); // NOI18N
        btnForecast.setToolTipText("Forecast");
        btnForecast.setBorder(null);
        btnForecast.setContentAreaFilled(false);
        btnForecast.setEnabled(false);
        btnForecast.setFocusTraversalPolicyProvider(true);
        btnForecast.setPreferredSize(new java.awt.Dimension(45, 45));
        btnForecast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForecastActionPerformed(evt);
            }
        });

        btnSetting.setBackground(new java.awt.Color(255, 255, 255));
        btnSetting.setForeground(new java.awt.Color(240, 240, 240));
        btnSetting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/settings.png"))); // NOI18N
        btnSetting.setToolTipText("Setting");
        btnSetting.setBorder(null);
        btnSetting.setContentAreaFilled(false);
        btnSetting.setFocusTraversalPolicyProvider(true);
        btnSetting.setPreferredSize(new java.awt.Dimension(45, 45));
        btnSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingActionPerformed(evt);
            }
        });

        panBottom.setBackground(new java.awt.Color(255, 255, 255));
        panBottom.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panBottom.setAlignmentX(0.0F);
        panBottom.setAlignmentY(0.0F);
        panBottom.setPreferredSize(new java.awt.Dimension(669, 42));

        jLabel1.setText("Process");

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/back.png"))); // NOI18N
        btnBack.setToolTipText("Back");
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusTraversalPolicyProvider(true);
        btnBack.setPreferredSize(new java.awt.Dimension(24, 24));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/play.png"))); // NOI18N
        btnPlay.setToolTipText("Play");
        btnPlay.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnPlay.setContentAreaFilled(false);
        btnPlay.setEnabled(false);
        btnPlay.setFocusTraversalPolicyProvider(true);
        btnPlay.setPreferredSize(new java.awt.Dimension(24, 24));
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/pause.png"))); // NOI18N
        btnPause.setToolTipText("Pause");
        btnPause.setContentAreaFilled(false);
        btnPause.setEnabled(false);
        btnPause.setFocusTraversalPolicyProvider(true);
        btnPause.setPreferredSize(new java.awt.Dimension(24, 24));
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/next.png"))); // NOI18N
        btnNext.setToolTipText("Next");
        btnNext.setContentAreaFilled(false);
        btnNext.setEnabled(false);
        btnNext.setFocusTraversalPolicyProvider(true);
        btnNext.setPreferredSize(new java.awt.Dimension(24, 24));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        probarThread.setAlignmentX(0.0F);
        probarThread.setAlignmentY(0.0F);
        probarThread.setBorderPainted(false);
        probarThread.setMaximumSize(new java.awt.Dimension(32767, 10));
        probarThread.setMinimumSize(new java.awt.Dimension(10, 10));
        probarThread.setPreferredSize(new java.awt.Dimension(146, 20));
        probarThread.setString("(0%)");
        probarThread.setStringPainted(true);

        labTime.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labTime.setText("Run Time: 00:00:00");

        javax.swing.GroupLayout panBottomLayout = new javax.swing.GroupLayout(panBottom);
        panBottom.setLayout(panBottomLayout);
        panBottomLayout.setHorizontalGroup(
            panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(probarThread, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnPlay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(btnPause, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labTime)
                .addContainerGap())
        );
        panBottomLayout.setVerticalGroup(
            panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labTime)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnPlay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPause, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNext, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(probarThread, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        panScrollCluster.setBorder(null);

        panCluster.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panCluster.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout panClusterLayout = new javax.swing.GroupLayout(panCluster);
        panCluster.setLayout(panClusterLayout);
        panClusterLayout.setHorizontalGroup(
            panClusterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 75, Short.MAX_VALUE)
        );
        panClusterLayout.setVerticalGroup(
            panClusterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 297, Short.MAX_VALUE)
        );

        panScrollCluster.setViewportView(panCluster);

        txtTimeImage.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
//        txtTimeImage.setText("Time:");

        txtShowMore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/showmore.png"))); // NOI18N
        txtShowMore.setToolTipText("Show More");
        txtShowMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtShowMoreActionPerformed(evt);
            }
        });

        jLabel2.setText("RMSE 1 =");

        jLabel3.setText("RMSE 2 =");

        jLabel4.setText("RMSE 3 = ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panScrollCluster, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtShowMore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtTimeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtShowMore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panScrollCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(17, 17, 17)
                .addComponent(txtTimeImage)
                .addContainerGap())
        );

        mFile.setText("File");

        miSelected.setText("Selected Input");
        miSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        mFile.add(miSelected);

        miCluster.setText("Cluster");
        miCluster.setEnabled(false);
        miCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClusterActionPerformed(evt);
            }
        });
        mFile.add(miCluster);

        miForecast.setText("Forecast");
        miForecast.setEnabled(false);
        miForecast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForecastActionPerformed(evt);
            }
        });
        mFile.add(miForecast);

        miSetting.setText("Setting");
        miSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingActionPerformed(evt);
            }
        });
        mFile.add(miSetting);

        meExport.setText("Export");
        meExport.setEnabled(false);

        miImage.setText("Export Image");
        miImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miImageActionPerformed(evt);
            }
        });
        meExport.add(miImage);

        miPDF.setText("Export PDF");
        miPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miPDFActionPerformed(evt);
            }
        });
        meExport.add(miPDF);

        miPrint.setText("Export Print");
        miPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miPrintActionPerformed(evt);
            }
        });
        meExport.add(miPrint);

        mFile.add(meExport);

        miClose.setText("Close");
        miClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCloseActionPerformed(evt);
            }
        });
        mFile.add(miClose);

        mainMenu.add(mFile);

        mHelp.setText("Help");

        miAbout.setText("About");
        miAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAboutActionPerformed(evt);
            }
        });
        
        miHelp.setText("Help");
        miHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miHelpActionPerformed(evt);
            }
        });
        mHelp.add(miHelp);
        mHelp.add(miAbout);

        mainMenu.add(mHelp);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCluster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnForecast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCluster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnForecast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	protected void ShowImage(MouseEvent e) {

	}

	private void btnClusterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnClusterActionPerformed
		btnCluster.setEnabled(false);
		miCluster.setEnabled(false);
		btnOpen.setEnabled(false);
		miSelected.setEnabled(false);
		btnBack.setEnabled(true);
		typeShow = -1;
		initTimerProc();
		addComponents();
		if (timer.isRunning()) {
			timer.stop();
		}
		myThread = new Thread() {
			@Override
			public void run() {
				try {
					isInProcess = true;
					imagesAfterCluster = new ArrayList<>();
					btnBack.setEnabled(false);
					btnNext.setEnabled(false);
					btnPlay.setEnabled(false);
					btnPause.setEnabled(true);
					btnSetting.setEnabled(false);
					// runtime FC
					startTimer(timerProcess);
					for (int i = 0; i < paths.size(); i++) {
						// System.out.println("size = "+ paths.size());
						try {
							waitThread();
							imagesAfterCluster.add(functionFC_PFS(i));
							// System.out.println("i = "+ i);
							currentImage = imagesAfterCluster.size() - 1;
						} catch (NumberFormatException | IOException
								| InterruptedException ex) {
							isInProcess = false;
							JOptionPane.showMessageDialog(null,
									"Have error when cluster 1", "Error",
									JOptionPane.ERROR_MESSAGE);
							Logger.getLogger(frmMain.class.getName()).log(
									Level.SEVERE, null, ex);
						}
					}
					// runtime FC
					stopTimer(timerProcess);
					JOptionPane.showMessageDialog(
							null,
							"Complete cluster! Time run: "
									+ totalTimeProc.toString(), "Information",
							JOptionPane.INFORMATION_MESSAGE);
					resetValue();
					writeImages();
					if (imagesAfterCluster.size() > 0) {
						meExport.setEnabled(true);
					}
					btnForecast.setEnabled(true);
				} catch (Exception e) {
					resetValue();
					stopTimer(timerProcess);
					JOptionPane.showMessageDialog(null,
							"Have error when cluster 2", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		// System.out.println("link = " + Define.BASEDPATH);

		myThread.start();

	}// GEN-LAST:event_btnClusterActionPerformed

	private void resetValue() {
		isInProcess = false;
		isPlause = false;
		btnPlay.setEnabled(false);
		btnPause.setEnabled(false);
		btnOpen.setEnabled(true);
		miSelected.setEnabled(true);
		miCluster.setEnabled(true);
		btnCluster.setEnabled(true);
		btnSetting.setEnabled(true);
	}

	private void waitThread() throws InterruptedException {
		synchronized (myThread) {
			if (!isInProcess && !isPlause) {
				myThread.wait();
				isInProcess = false;
				isPlause = true;
			}
		}
	}

	private void addComponents() {
		panCluster.removeAll();
		panCluster.setLayout(new BoxLayout(panCluster, BoxLayout.PAGE_AXIS));
		for (int i = 0; i < Transformations.getC(); i++) {
			final Button b = new Button("Cluster " + (i + 1));
			panCluster.add(b);
			b.setFont(new Font("Tahoma", Font.PLAIN, 12));
			b.setVisible(false);
			show = false;
			b.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						String s = (String) JOptionPane.showInputDialog(
								panCluster, "Name: " + b.getLabel(),
								"Set name " + b.getLabel(),
								JOptionPane.PLAIN_MESSAGE);
						if (s != null && s.length() > 0) {
							b.setLabel(s);
						}
					}
				}
			});
		}
		panCluster.revalidate();
		panCluster.repaint();
	}

	private void setColorButton(Pixel[] p) {
		if (p == null) {
			for (int i = 0; i < panCluster.getComponentCount(); i++) {
				final Button b = (Button) panCluster.getComponent(i);
				Color c = new Color(240, 240, 240);
				b.setBackground(c);
			}
		} else {
			for (int i = 0; i < panCluster.getComponentCount(); i++) {
				final Button b = (Button) panCluster.getComponent(i);
				Color c = p[i].toColor();
				b.setBackground(c);
			}
		}
		panCluster.revalidate();
		panCluster.repaint();
	}

	private ImageModel functionFC_PFS(int index) throws NumberFormatException,
			IOException {
		File f = new File(paths.get(index));
		image = ImageIO.read(f);
		NodeItem item = ((CustomTreeNode) parents.getChildAt(index))
				.getNodeItem();
		BufferedImage imageCluster = Transformations.flipVertical(image, index);
		Pixel[] p;
		if (Transformations.getjCombochooser() == 0) {
			ImageCluster2 ic = new ImageCluster2();
			ic.setK(Transformations.getC());
			p = ic.readCenter(Define.BASEDPATH + "/center_id" + index + ".txt");
		} else {
			ImageClusterFCM ic = new ImageClusterFCM();
			ic.setK(Transformations.getC());
			p = ic.readCenter(Define.BASEDPATH + "/center_id" + index + ".txt");
		}
		ImageModel imageModel = new ImageModel(imageCluster, item.getName(),
				item.getTime(), p);
		showImageResult(imageCluster);
		setColorButton(p);
		setValueProcessBar(index);
//		txtTimeImage.setText("Time: " + item.getTime());
//		txtTimeImage.setForeground(Color.black);
		return imageModel;
	}

	private void setValueProcessBar(int i) {
		int percent = (i + 1) * 100 / (paths.size());
		probarThread.setValue(percent);
		probarThread.setString("(" + percent + "%)");
		probarThread.repaint();
	}

	synchronized void writeImages() {
		try {
			String newDirectory = directory + "/Phan Cum";

			NodeItem folder = new NodeItem("Phan Cum", newDirectory, "", true);
			newParents = new CustomTreeNode(new ImageIcon(
					ImageIO.read(getClass().getResource("/Image/folder.png"))),
					folder);
			newParents.setId(2);
			boolean f = new File(newDirectory).mkdirs();
			for (int i = 0; i < imagesAfterCluster.size(); i++) {
				try {
					ImageModel bi = imagesAfterCluster.get(i);
					String path = newDirectory + "/image" + i + ".png";
					File outputfile = new File(path);
					ImageIO.write(bi.getImage(), "png", outputfile);
					NodeItem image = new NodeItem("image" + i + ".png",
							newDirectory + "/image" + i + ".png", bi.getTime(),
							false);
					CustomTreeNode node = new CustomTreeNode(new ImageIcon(
							ImageIO.read(getClass().getResource(
									"/Image/image.png"))), image);
					node.setId(paths.size() + i);
					newParents.add(node);
					imagesAfterCluster.get(i).setPath(path);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null,
							"Have error! Write file fail", "Error",
							JOptionPane.ERROR_MESSAGE);
					Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			}
			root = new CustomTreeNode(new ImageIcon(ImageIO.read(getClass()
					.getResource("/Image/tree.png"))), new NodeItem("Root", "",
					"", true));
			root.add(parents);
			root.add(newParents);
			DefaultTreeModel model = new DefaultTreeModel(root);
			treeListFile.setModel(model);
			treeListFile.setCellRenderer(new CustomTreeCellRender());
			treeListFile.setRowHeight(25);
			treeListFile.repaint();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Have error! Write file fail",
					"Error", JOptionPane.ERROR_MESSAGE);
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	private void btnForecastActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnForecastActionPerformed
		btnCluster.setEnabled(false);
		btnForecast.setEnabled(false);
		btnOpen.setEnabled(false);
		if(timerProcess == null){
			initTimerProc();
		}
		myThread = new Thread() {
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				try {
					imagesForecast = new ArrayList<>();
					btnBack.setEnabled(false);
					btnNext.setEnabled(false);
					btnPlay.setEnabled(false);
					btnPause.setEnabled(true);
					btnSetting.setEnabled(false);

					 startTimer(timerProcess);

					 if(Transformations.getjCombochooser() == 0){
					  BufferedImage forecastMedian = Star.predictedImage1(0);
					  //showImageResult(forecastMedian);
					  String str = "#.###";
						DecimalFormat decimalFormat = new DecimalFormat(str);
					  jLabel2.setText("RMSE = " +
								 decimalFormat.format(Star.getRmse()));
					  
					  BufferedImage tg = forecastMedian;
					  BufferedImage[] forecast = new BufferedImage[3];
					  forecast[0] = tg;
					  double [] rmse2 = new double[3];
					  for (int i = 1; i < 3; i++) {
						  BufferedImage ClusterImgForecast = convertTojpg(tg);
						  BufferedImage ImgForecast = Transformations.flipVertical2(ClusterImgForecast, 3 + i);
						  //BufferedImage ClusterImgForecast = convertTojpg(tg);
						 // forecast[i] = ClusterImgForecast;
						  forecast[i] = Star.predictedImage1(i); 
						  rmse2[i] = Star.getRmse();
					 
					 }
					  showImageResult(forecast[0]);
					  //showImageResult(ClusterImgForecast);
					 

					for (int i = 0; i < 3; i++) {
						NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
								.getNodeItem();
						ImageModel imageModel = new ImageModel(forecast[i],
								item.getName());
						imagesForecast.add(imageModel);
					}
				
					jLabel3.setText("RMSE 2 = "
							+ decimalFormat.format(rmse2[1]));
					jLabel4.setText("RMSE 3 = "
							+ decimalFormat.format(rmse2[2]));

//					txtTimeImage.setText("Time: " + timeForecast);
//					txtTimeImage.setForeground(Color.red);
				}else if(Transformations.getjCombochooser() == 1){
					  BufferedImage forecastMedian = Star.predictedImage1(0);
					  //showImageResult(forecastMedian);
					  String str = "#.###";
						DecimalFormat decimalFormat = new DecimalFormat(str);
					  jLabel2.setText("RMSE = " +
								 decimalFormat.format(Star.getRmse()));
					  
					  BufferedImage tg = forecastMedian;
					  BufferedImage[] forecast = new BufferedImage[3];
					  forecast[0] = tg;
					  double [] rmse2 = new double[3];
					  for (int i = 1; i < 3; i++) {
						  BufferedImage ClusterImgForecast = convertTojpg(tg);
						  BufferedImage ImgForecast = Transformations.flipVertical21(ClusterImgForecast, 3 + i);
						  //BufferedImage ClusterImgForecast = convertTojpg(tg);
						 // forecast[i] = ClusterImgForecast;
						  forecast[i] = Star.predictedImage1(i); 
						  rmse2[i] = Star.getRmse();
					 
					 }
					  showImageResult(forecast[0]);
					  //showImageResult(ClusterImgForecast);
					 

					for (int i = 0; i < 3; i++) {
						NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
								.getNodeItem();
						ImageModel imageModel = new ImageModel(forecast[i],
								item.getName());
						imagesForecast.add(imageModel);
					}
				
					jLabel3.setText("RMSE 2 = "
							+ decimalFormat.format(rmse2[1]));
					jLabel4.setText("RMSE 3 = "
							+ decimalFormat.format(rmse2[2]));

//					txtTimeImage.setText("Time: " + timeForecast);
//					txtTimeImage.setForeground(Color.red);
					
				}else if(Transformations.getjCombochooser() == 2){
					
	 				for (int i = 0; i < paths.size(); i++) {
						File f = new File(paths.get(i));
						BufferedImage ima = ImageIO.read(f);
						MatrixPixel mp = new MatrixPixel(ima);
						Pixel[][] px = mp.getData(i);
					}
					
					BufferedImage[] fore = new BufferedImage[3];
					fore = Transformations.forcast();
					showImageResult(fore[2]);
					String str = "#.###";
					DecimalFormat decimalFormat = new DecimalFormat(str);
					jLabel2.setText("RMSE 1 = "
							+ decimalFormat.format(LuatMo2.getRmse()));
					jLabel3.setText("RMSE 2 = "
							+ decimalFormat.format(Transformations.getRmse2()));
					jLabel4.setText("RMSE 3 = "
							+ decimalFormat.format(Transformations.rmse3));
//					txtTimeImage.setText("Time: " + timeForecast);
//					txtTimeImage.setForeground(Color.red);

					for (int i = 0; i < 3; i++) {
						NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
								.getNodeItem();
						ImageModel imageModel = new ImageModel(fore[i],
								item.getName());
						imagesForecast.add(imageModel);

						// writeImagesForecast(fore[i],"forecast"+ i + ".png");
						// showImageResult(fore[i]);

					}
				}else{
					BufferedImage[] fore = new BufferedImage[3];
					fore = Transformations.forcastAFC();
					showImageResult(fore[2]);
					String str = "#.###";
					DecimalFormat decimalFormat = new DecimalFormat(str);
					jLabel2.setText("RMSE 1 = "
							+ decimalFormat.format(LuatMo2.getRmse()));
					jLabel3.setText("RMSE 2 = "
							+ decimalFormat.format(Transformations.getRmse2()));
					jLabel4.setText("RMSE 3 = "
							+ decimalFormat.format(Transformations.rmse3));
//					txtTimeImage.setText("Time: " + timeForecast);
//					txtTimeImage.setForeground(Color.red);

					for (int i = 0; i < 3; i++) {
						NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
								.getNodeItem();
						ImageModel imageModel = new ImageModel(fore[i],
								item.getName());
						imagesForecast.add(imageModel);
					}
				}
					/*for (int i = 0; i < 3; i++) {
						NodeItem item = ((CustomTreeNode) parents.getChildAt(i))
								.getNodeItem();
						ImageModel imageModel = new ImageModel(fore[i],
								item.getName());
						imagesForecast.add(imageModel);

						// writeImagesForecast(fore[i],"forecast"+ i + ".png");
						// showImageResult(fore[i]);

					}*/
					writeImagesForecast();
					// writeImagesForecast(fore2,"forecast_test.png");

					resetValue();
					if (timerProcess.isRunning()) {
						stopTimer(timerProcess);
						JOptionPane.showMessageDialog(null,
								"Complete forecast! Time run total to forecast: "
										+ totalTimeProc.toString(),
								"Information", JOptionPane.INFORMATION_MESSAGE);
					}
                                        
                                        
                                        
                                        
                                        
                                        
				} catch (IOException ex) {
					resetValue();
					JOptionPane.showMessageDialog(null,
							"Have error! Write file fail", "Error",
							JOptionPane.ERROR_MESSAGE);
					Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			}
		};
		myThread.start();
	}// GEN-LAST:event_btnForecastActionPerformed

	private String nextName(String beforeName) {
		return "Next";
	}

	private String nextTime(String beforeTime) {
		return "Next time";
	}

	synchronized void writeImagesForecast(BufferedImage imgForecast,
			String filename) throws IOException {
		try {
			String newDirectory = directory + "/Du Bao";
			NodeItem folder = new NodeItem("Du Bao", newDirectory, "", true);
			forcastParents = new CustomTreeNode(new ImageIcon(
					ImageIO.read(getClass().getResource("/Image/folder.png"))),
					folder);
			forcastParents.setId(2);
			boolean f = new File(newDirectory).mkdirs();
			String path = newDirectory + "/" + filename;
			File outputfile = new File(path);
			ImageIO.write(imgForecast, "png", outputfile);
			NodeItem image = new NodeItem(filename, newDirectory + "/"
					+ filename, timeForecast, false);
			CustomTreeNode node = new CustomTreeNode(new ImageIcon(
					ImageIO.read(getClass().getResource("/Image/image.png"))),
					image);
			node.setId(-1);
			forcastParents.add(node);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Have error! Write file fail",
					"Error", JOptionPane.ERROR_MESSAGE);
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		root = new CustomTreeNode(new ImageIcon(ImageIO.read(getClass()
				.getResource("/Image/tree.png"))), new NodeItem("Root", "", "",
				true));
		root.add(parents);
		// root.add(newParents); neu bo qua buoc phan cum

		root.add(forcastParents);
		DefaultTreeModel model = new DefaultTreeModel(root);
		treeListFile.setModel(model);
		treeListFile.setCellRenderer(new CustomTreeCellRender());
		treeListFile.setRowHeight(25);
		treeListFile.repaint();

		//

	}

	synchronized void writeImagesForecast() throws IOException {
		try {
			String newDirectory = directory + "/Du Bao";
			NodeItem folder = new NodeItem("Du Bao", newDirectory, "", true);
			forcastParents = new CustomTreeNode(new ImageIcon(
					ImageIO.read(getClass().getResource("/Image/folder.png"))),
					folder);
			forcastParents.setId(3);
			boolean f = new File(newDirectory).mkdirs();

			for (int i = 0; i < imagesForecast.size(); i++) {
				try {
					ImageModel bi = imagesForecast.get(i);
					String path = newDirectory + "/imageForecast" + i + ".png";
					File outputfile = new File(path);
					ImageIO.write(bi.getImage(), "png", outputfile);
					NodeItem image = new NodeItem("imageForecast" + i + ".png",
							newDirectory + "/imageForecast" + i + ".png",
							bi.getTime(), false);
					CustomTreeNode node = new CustomTreeNode(new ImageIcon(
							ImageIO.read(getClass().getResource(
									"/Image/image.png"))), image);
					node.setId(paths.size() * 2 + i); // id
					forcastParents.add(node);
					imagesForecast.get(i).setPath(path);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null,
							"Have error! Write file fail", "Error",
							JOptionPane.ERROR_MESSAGE);
					Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			}

		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Have error! Write file fail",
					"Error", JOptionPane.ERROR_MESSAGE);
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		root = new CustomTreeNode(new ImageIcon(ImageIO.read(getClass()
				.getResource("/Image/tree.png"))), new NodeItem("Root", "", "",
				true));
		
		root.add(parents);
		if(newParents!= null && newParents.getChildCount() > 0 )
			root.add(newParents); // cmt dong nay lai no k hien thu muc phan cum
								// nua.neu de thi no bao loi vì bo qua bc phan
								// cum ma
		root.add(forcastParents);
		DefaultTreeModel model = new DefaultTreeModel(root);
		treeListFile.setModel(model);
		treeListFile.setCellRenderer(new CustomTreeCellRender());
		treeListFile.setRowHeight(25);
		treeListFile.repaint();
	}

	synchronized void writeImagesForecast(ImageModel nextImage)
			throws IOException {
		try {
			String newDirectory = directory + "/Du Bao";
			NodeItem folder = new NodeItem("Du Bao", newDirectory, "", true);
			forcastParents = new CustomTreeNode(new ImageIcon(
					ImageIO.read(getClass().getResource("/Image/folder.png"))),
					folder);
			forcastParents.setId(2);
			boolean f = new File(newDirectory).mkdirs();

			String path = newDirectory + "/" + nextImage.getName();
			File outputfile = new File(path);
			ImageIO.write(nextImage.getImage(), "png", outputfile);
			NodeItem image = new NodeItem(nextImage.getName(), path,
					nextImage.getTime(), false);
			CustomTreeNode node = new CustomTreeNode(new ImageIcon(
					ImageIO.read(getClass().getResource("/Image/image.png"))),
					image);
			node.setId(-1);
			forcastParents.add(node);
			// node.setId(parents.);
			parents.add(node);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Have error! Write file fail",
					"Error", JOptionPane.ERROR_MESSAGE);
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		root = new CustomTreeNode(new ImageIcon(ImageIO.read(getClass()
				.getResource("/Image/tree.png"))), new NodeItem("Root", "", "",
				true));
		root.add(parents);
		root.add(newParents);
		root.add(forcastParents);
		DefaultTreeModel model = new DefaultTreeModel(root);
		treeListFile.setModel(model);
		treeListFile.setCellRenderer(new CustomTreeCellRender());
		treeListFile.setRowHeight(25);
		treeListFile.repaint();
	}

	private BufferedImage convertTojpg(BufferedImage bf) throws IOException {
		BufferedImage newBufferedImage = new BufferedImage(bf.getWidth(),
				bf.getHeight(), BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics()
				.drawImage(bf, 0, 0, Color.WHITE, null);

		ImageIO.write(newBufferedImage, "jpg", new File(directory + "/forecast.jpg"));
		BufferedImage forecast_jpg = ImageIO.read(new File(directory + "/forecast.jpg"));
		return forecast_jpg;
	}

	private void btnSettingActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSettingActionPerformed
		// TODO add your handling code here:
		final frmSetting frm = new frmSetting(algorithm);
		frm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				((Component) currentWindow).setEnabled(true);
				Transformations.setC(frm.getC());
				frm.dispose();
			}

		});
		frm.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				((Component) currentWindow).setEnabled(true);
				algorithm = frm.getAlgorithm();
				Transformations.setC(frm.getC());
				frm.dispose();
			}
		});

		((Component) currentWindow).setEnabled(false);
		frm.setResizable(false);
		setLocationRelativeTo(getRootPane());
		frm.setVisible(true);
		
		
	}// GEN-LAST:event_btnSettingActionPerformed

	private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnOpenActionPerformed
		try {
			configure();

		} catch (IOException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		} catch (SAXException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		} catch (ParseException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}// GEN-LAST:event_btnOpenActionPerformed

	private void panRightComponentResized(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_panRightComponentResized
		// TODO add your handling code here:
		try {
			setPositionImage();
		} catch (Exception e) {

		}
	}// GEN-LAST:event_panRightComponentResized

	private void miCloseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miCloseActionPerformed
		// TODO add your handling code here:
		try {
			myThread.interrupt();
		} catch (Exception e) {
		}
		System.exit(0);
	}// GEN-LAST:event_miCloseActionPerformed

	private void treeListFileValueChanged(
			javax.swing.event.TreeSelectionEvent evt) {// GEN-FIRST:event_treeListFileValueChanged
		// TODO add your handling code here:
		initProcess();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeListFile
				.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		NodeItem nodeInfo = (NodeItem) node.getUserObject();
		if (!nodeInfo.isIsRoot()) {
			if (timer.isRunning()) {
				timer.stop();
			}
			if (((CustomTreeNode) node).getId() == -1) { // á id = null
				typeShow = 0; // is error
				currentImage = 0;
			} else {
				if (((CustomTreeNode) node).getId() < paths.size()) { // is
																		// image
																		// before
																		// cluster
					currentImage = ((CustomTreeNode) node).getId();
					typeShow = 1;
				} else if (((CustomTreeNode) node).getId() >= paths.size()
						&& ((CustomTreeNode) node).getId() < paths.size() * 2) {// is
																				// image
																				// after
																				// cluster
					typeShow = 2;
					currentImage = ((CustomTreeNode) node).getId()
							- paths.size();
				} else {
					typeShow = 3;
					currentImage = ((CustomTreeNode) node).getId()
							- paths.size() * 2;
				}
			}
			showImageOption();
		} else {
			if (((CustomTreeNode) node).getId() == 0) {
				typeShow = 0; // is folder root
			} else if (((CustomTreeNode) node).getId() == 1) {// is image before
																// cluster
				typeShow = 1;
			} else {
				if (((CustomTreeNode) node).getId() == 2) {// is image after cluster
					typeShow = 2;
				}else if (((CustomTreeNode) node).getId() == 3){// is image after forecast
					typeShow = 3;
				}
			}
			setTimeShowImage();
		}
		checkEnable();
	}// GEN-LAST:event_treeListFileValueChanged

	private void setTimeShowImage() {
		if (!isInProcess) {
			currentImage = 0;
			ActionListener actListner;
			actListner = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					showImageOption();
					if (typeShow != 0) {
						if (typeShow != 3) {
							currentImage = (currentImage + 1) % paths.size();
						} else {
							currentImage = (currentImage + 1) % imagesForecast.size();
						}
					}
					checkEnable();
				}
			};
			timer.setDelay(1000);
			timer.stop();
			timer = new Timer(1000, actListner);
			timer.start();
			btnPlay.setEnabled(false);
			btnPause.setEnabled(true);
		}
	}

	private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnNextActionPerformed
		if (currentImage < paths.size() - 1) {
			if (typeShow != 3) {
				currentImage = (currentImage + 1) % paths.size();
			} else {
				currentImage = (currentImage + 1) % imagesForecast.size();
			}
			showImageOption();
		}
		checkEnable();
	}// GEN-LAST:event_btnNextActionPerformed

	private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPauseActionPerformed
		if (typeShow == -1) {
			isInProcess = false;
			stopTimer(timerProcess);
		} else {
			timer.stop();
		}
		btnPlay.setEnabled(true);
		btnPause.setEnabled(false);
	}// GEN-LAST:event_btnPauseActionPerformed

	private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPlayActionPerformed
		if (typeShow == -1) {
			synchronized (myThread) {
				if (isPlause = true) {
					myThread.notify();
					isPlause = false;
					isInProcess = true;
					startTimer(timerProcess);
				}
			}
		} else {
			timer.start();
		}
		btnPlay.setEnabled(false);
		btnPause.setEnabled(true);
	}// GEN-LAST:event_btnPlayActionPerformed

	private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBackActionPerformed

		if (currentImage > 0) {
			if (typeShow != 3) {
				currentImage = (currentImage - 1) % paths.size();
			} else {
				currentImage = (currentImage - 1) % imagesForecast.size();
			}
			showImageOption();
		}
		checkEnable();
	}// GEN-LAST:event_btnBackActionPerformed

	private void miPDFActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miPDFActionPerformed
		if (PDFModel.createPdf(imagesAfterCluster, directory + "\\output.pdf",
				true)) {
			JOptionPane.showMessageDialog(this,
					"Export file PDF success! Path: " + directory
							+ "\\output.pdf", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Export file PDF fail",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}// GEN-LAST:event_miPDFActionPerformed

	private void miPrintActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miPrintActionPerformed
		PrinterJob pj = PrinterJob.getPrinterJob();
		if (pj.printDialog()) {
			try {
				pj.print();
			} catch (PrinterException exc) {
				// System.out.println(exc);
			}
		}
	}// GEN-LAST:event_miPrintActionPerformed

	private void miImageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miImageActionPerformed
		writeImages();
		JOptionPane.showMessageDialog(this, "Export images success!Path: "
				+ directory + "\\Phan Cum", "Information",
				JOptionPane.INFORMATION_MESSAGE);
	}// GEN-LAST:event_miImageActionPerformed

	private void formComponentResized(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_formComponentResized
		try {
			setPositionImage();
		} catch (Exception e) {

		}
	}// GEN-LAST:event_formComponentResized

	boolean show = false;

	private void txtShowMoreActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtShowMoreActionPerformed
		show = !show;
		for (int i = 0; i < panCluster.getComponentCount(); i++) {
			Component c = panCluster.getComponent(i);
			c.setVisible(show);
		}
		panCluster.revalidate();
		panCluster.repaint();
	}// GEN-LAST:event_txtShowMoreActionPerformed

	private void miAboutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAboutActionPerformed
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(ImageIO.read(getClass().getResource(
					"/Image/about1.png")));

		} catch (IOException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		JOptionPane.showMessageDialog(this,
				"Application of fuzzy clustering scenario in weather forecasting\n"
						+ "Develop by: CHPC\n"
						+ "Address: VNU University of Science, 334 Nguyen Trai, Thanh Xuan, Ha Noi", "About",
				JOptionPane.INFORMATION_MESSAGE, icon);
	}// GEN-LAST:event_miAboutActionPerformed

	private void miHelpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miAboutActionPerformed
		ImageIcon icon = null;
		JOptionPane.showMessageDialog(this,
				"Help to run this software!\n"
						+ "Firstly, Click Open button to choose a folder of input satellite images.\n"
						+ "Secondly, Click Setting button to choose which algorithm to be used.\n"
						+ "If you choose FCM_STAR or FC_PFS_STAR, you must click clustering button before clicking forecast button.\n"
						+ "If you choose FC_PFS_RULE or AutoCluster_RULE, you do not click clustering button, click forecast botton only.\n"
						+ "Thank you!", "Help",
				JOptionPane.INFORMATION_MESSAGE, icon);
	}// GEN-LAST:event_miAboutActionPerformed
	
	private void checkEnable() {
		if (currentImage < paths.size() - 1) {
			btnNext.setEnabled(true);
		}
		if (currentImage > 0) {
			btnBack.setEnabled(true);
		}
		if (currentImage == 0) {
			btnBack.setEnabled(false);
		} else {
			btnBack.setEnabled(true);
		}
		if (currentImage == paths.size() - 1) {
			btnNext.setEnabled(false);
		} else {
			btnNext.setEnabled(true);
		}

	}

	private void initProcess() {
		if (!isInProcess) {
			btnCluster.setEnabled(treeListFile.getModel() != null
					&& paths.size() > 0);

			File f = new File(Define.BASEDPATH);
			// btnForecast.setEnabled(f.list().length > 0);
			btnForecast.setEnabled(true);
			miCluster.setEnabled(treeListFile.getModel() != null
					&& paths.size() > 0);
			probarThread.setString("(0%)");
			probarThread.setValue(0);
			probarThread.repaint();
		}
	}

	private void showImageOption() {
		try {
			NodeItem node = null;
			switch (typeShow) {
			case 1:// is show image before cluster
				node = ((CustomTreeNode) parents.getChildAt(currentImage))
						.getNodeItem();
				setColorButton(null);
//				txtTimeImage.setForeground(Color.black);
				break;
			case 2: // is show image after cluster
				node = ((CustomTreeNode) newParents.getChildAt(currentImage))
						.getNodeItem();
				if (imagesAfterCluster.size() > 0) {
					setColorButton(imagesAfterCluster.get(currentImage)
							.getListCenter());
				}
//				txtTimeImage.setForeground(Color.black);
				break;
			case 3:// is show image after forecast
				node = ((CustomTreeNode) forcastParents
						.getChildAt(currentImage)).getNodeItem();
				if (imagesForecast.size() > 0) {
					setColorButton(imagesForecast.get(currentImage)
							.getListCenter());
				}
//				txtTimeImage.setForeground(Color.red);
				break;
			default:
				// Todo something
				break;
			}
			panRight.removeAll();
			image = ImageIO.read(new File(node.getPath()));
			setImage();
//			txtTimeImage.setText("Time: " + node.getTime());
			repaint();

		} catch (IOException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	private void showImage(String path) {
		try {
			panRight.removeAll();
			File f = new File(path);
			image = ImageIO.read(f);
			setImage();

		} catch (IOException ex) {
			Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	private void setImage() throws IOException {
		image = getScaledImage(image, this.panRight.getSize().width,
				this.panRight.getSize().height);
		labImage = new JLabel(new ImageIcon(image));
		panelImage = new ImageImplement(image);
		panRight.add(panelImage, BorderLayout.CENTER);
		setPositionImage();
		repaint();
	}

	synchronized public static BufferedImage getScaledImage(
			BufferedImage image, int width, int height) throws IOException {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) Math.min(width, height) / imageWidth;
		double scaleY = (double) Math.min(width, height) / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(
				scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(
				scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		int type = image.getType();
		return bilinearScaleOp.filter(
				image,
				new BufferedImage(Math.min(width, height), Math.min(width,
						height), type));
	}

	private void showImageResult(BufferedImage img) throws IOException {
		panRight.removeAll();
		img = getScaledImage(img, this.panRight.getSize().width,
				this.panRight.getSize().height);
		labImage = new JLabel(new ImageIcon(img));
		panelImage = new ImageImplement(img);
		panRight.add(panelImage, BorderLayout.CENTER);
		setPositionImage();
		repaint();
	}

	private void setPositionImage() {
		int centerX = (panRight.getWidth() - panelImage.getWidth()) / 2;
		int centerY = (panRight.getHeight() - panelImage.getHeight()) / 2;
		panelImage.setLocation(centerX, centerY);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;

				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(frmMain.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(frmMain.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(frmMain.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(frmMain.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new frmMain().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCluster;
    private javax.swing.JButton btnForecast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnSetting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labTime;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenu meExport;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miHelp;
    private javax.swing.JMenuItem miClose;
    private javax.swing.JMenuItem miCluster;
    private javax.swing.JMenuItem miForecast;
    private javax.swing.JMenuItem miImage;
    private javax.swing.JMenuItem miPDF;
    private javax.swing.JMenuItem miPrint;
    private javax.swing.JMenuItem miSelected;
    private javax.swing.JMenuItem miSetting;
    private javax.swing.JPanel panBottom;
    private javax.swing.JPanel panCluster;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JScrollPane panScrollCluster;
    private javax.swing.JProgressBar probarThread;
    private javax.swing.JTree treeListFile;
    private javax.swing.JButton txtShowMore;
    private javax.swing.JLabel txtTimeImage;
    // End of variables declaration//GEN-END:variables
}
