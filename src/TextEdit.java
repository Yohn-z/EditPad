import java.io.*;
import java.nio.file.WatchEvent.Kind;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("unused")
public class TextEdit{
	public static void main(String args[]){
       Win TArea = new Win();//测试主体，生成窗口
       //监听窗口焦点事件
       TArea.addWindowFocusListener(new WindowFocusListener() {   
           public void windowGainedFocus(WindowEvent e) {   
               
           }   
 
           public void windowLostFocus(WindowEvent e) { 
        	   TArea.menuR.setVisible(false);
           }   
       });   
       //监听窗口状态事件
       TArea.addWindowStateListener(new WindowStateListener() {   
           public void windowStateChanged(WindowEvent e) {  
        	   TArea.menuR.setVisible(false);
           }   
       });   
       TArea.setVisible(true);//设置可见性
    }
}

/*定义一个文档个数异常*/
@SuppressWarnings("serial")
class FileNumberException extends Exception{
	String s;
	FileNumberException(){
		s = "文档数超出限制";
	}
	public String toString(){
		return s;
	}
	
}

@SuppressWarnings("serial")
class Win extends JFrame implements ActionListener,CaretListener,DocumentListener,MouseListener/*按钮、光标、文档事件监听*/{
	int i=0;//新建文档个数
	int m=0;//显示文档数
	int index=1;//选项卡位置
	int flag[] = {0,0,0,0,0,0,0,0,0,0};//文档的flag初始值，0表示未被改变
	String path[] = new String[10]; //文档上一级的绝对路径
	int dot=0,mark=0;//dot光标的起始位置，mark光标的末尾位置
	String str1="0",str3="0";//行数，字数初始值0
	JTabbedPane tabbedPane;//选项卡面板
	JPanel p2[]=new JPanel[10];//选项卡上的按钮，用于添加label和按钮"X"
	JLabel lab[] = new JLabel[10];//用于显示文档名
	JTextArea text[]= new JTextArea[10];//文本域
	JScrollPane jsp[] = new JScrollPane[10];//下拉条
	JButton btn[] = new JButton[10];//选项卡上的"X"按钮
	
	JFileChooser fileDialog;//可获取文件对话框
	
	JMenuBar menubar;//菜单栏
    JMenu menu,edit,formation,run,look,help;//文件，编辑,格式，编译，查看，帮助
	JMenuItem itemBulid,itemSave,itemOpen,itemExit;//文件新建、文件保存，文件打开、退出
	JMenuItem itemAll,itemCut,itemCopy,itemPaste,RAll,RCut,RCopy,RPaste,itemSearch,itemChange;//剪切、复制、黏贴
	JMenuItem itemFont,itemColor;//自动换行，字体
	JMenuItem itemJavac,itemJava;//编译和运行java程序
	JRadioButton lineWrapSelect,itemlookSelect;//自动换行、状态栏
	JMenuItem itemHelp,itemAbout;//帮助，有关

	JPopupMenu menuR;//右键菜单栏
	
	JPanel line;//底部状态栏
	JPanel lineeast;//底部状态栏东部面板
	JLabel linenumber,textnumber;//显示底部状态栏的的行数和字数
	
	FileReader fileReader;//文件流
    FileWriter fileWrite;
    BufferedReader in;//缓冲流
    BufferedWriter out;
    
    FontChange fontChange = new FontChange();//颜色窗体
    ReplaceFrame replace = new ReplaceFrame();//替换窗口
  
    /*获取屏幕分辨率*/
	Toolkit kit = Toolkit.getDefaultToolkit(); 
    Dimension screenSize = kit.getScreenSize();//获取屏幕分辨率  
   

    Win(){
    	
    	super("EditPad-v1.0");
        setSize(screenSize.width/2,screenSize.height/2);//大小  
        setLocation(screenSize.width/4,screenSize.height/4);//位置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置可关闭
        
        /*初始化选项卡面板*/
        tabbedPane = new JTabbedPane();tabbedPane.addMouseListener(this);
        /*初始化右键菜单*/
        menuR = new JPopupMenu();
        /*添加图标*/
        java.net.URL imgURL = Frame.class.getResource("/img/frameicon.png");
        ImageIcon imgIcon = new ImageIcon(imgURL);
        Image img = imgIcon.getImage();
        this.setIconImage(img);
        
    	/*菜单栏*/
    	menubar = new JMenuBar();menubar.addMouseListener(this);
    	/*一级菜单栏*/
    	menu = new JMenu("文件(F)");menu.addMouseListener(this);
    	edit = new JMenu("编辑(E)");edit.addMouseListener(this);
    	formation = new JMenu("格式(O)");formation.addMouseListener(this);
    	run = new JMenu("编译(C)");run.addMouseListener(this);
    	look = new JMenu("查看(V)");look.addMouseListener(this);
    	help = new JMenu("帮助(H)");help.addMouseListener(this);
    	
    	/*二级菜单栏*/
    	itemBulid= new JMenuItem(" 新建 (N)  ");itemBulid.addActionListener(this);
    	itemOpen = new JMenuItem(" 打开(O)   ");itemOpen.addActionListener(this);
    	itemSave = new JMenuItem(" 保存(S)   ");itemSave.addActionListener(this);
        itemExit = new JMenuItem(" 退出(X)   ");itemExit.addActionListener(this);
        itemAll  = new JMenuItem(" 全选(A)   ");itemAll.addActionListener(this);
        itemCut  = new JMenuItem(" 剪切(T)   ");itemCut.addActionListener(this);
        itemCopy  = new JMenuItem(" 复制(C)   ");itemCopy.addActionListener(this);
        itemPaste  = new JMenuItem(" 黏贴(P)   ");itemPaste.addActionListener(this);
        RAll  = new JMenuItem(" 全选(A)   ");RAll.addActionListener(this);
        RCut  = new JMenuItem(" 剪切(T)   ");RCut.addActionListener(this);
        RCopy  = new JMenuItem(" 复制(C)   ");RCopy.addActionListener(this);
        RPaste  = new JMenuItem(" 黏贴(P)   ");RPaste.addActionListener(this);
        itemSearch  = new JMenuItem(" 查找(F)   ");itemSearch.addActionListener(this);
        itemChange  = new JMenuItem(" 替换(R)   ");itemChange.addActionListener(this);
        itemJava  = new JMenuItem(" 运行java    ");itemJava.addActionListener(this);
        itemJavac  = new JMenuItem(" 编译java    ");itemJavac.addActionListener(this);
        lineWrapSelect = new JRadioButton("自动换行(W)");lineWrapSelect.addActionListener(this);
        itemFont = new JMenuItem("     字体(F) ");itemFont.addActionListener(this);
        itemColor = new JMenuItem("     颜色(C) ");itemColor.addActionListener(this);
        itemlookSelect = new JRadioButton("状态栏(s)");itemlookSelect.addActionListener(this);
        itemHelp = new JMenuItem(" 查看帮助(H)   ");itemHelp.addActionListener(this);
        itemAbout = new JMenuItem(" 有关记事本(A)   ");itemAbout.addActionListener(this);
        
    	/*二级子项添加*/
        menu.add(itemBulid);
        menu.addSeparator();
    	menu.add(itemSave);
    	menu.add(itemOpen);
    	menu.addSeparator();
    	menu.add(itemExit);
    	edit.add(itemAll);
    	edit.addSeparator();
        edit.add(itemCut);
        edit.add(itemCopy);
        edit.add(itemPaste);
        edit.addSeparator();
        edit.add(itemSearch);
        edit.add(itemChange);
        run.add(itemJavac);
        run.add(itemJava);
        formation.add(lineWrapSelect);
        formation.addSeparator();
        formation.add(itemFont);
        formation.add(itemColor);
        look.add(itemlookSelect);
        help.add(itemHelp);
        help.add(itemAbout);
        
    	/*一级子项添加*/
    	menubar.add(menu);//添加文件
    	menubar.add(edit);//添加编辑
    	menubar.add(formation);//格式
    	menubar.add(run);
    	menubar.add(look);
    	menubar.add(help);//帮助
    	
    	/*右键菜单添加*/
    	menuR.add(RAll);
        menuR.add(RCopy); 
        menuR.add(RCut);  
        menuR.add(RPaste);
        
    	
    	
    	setJMenuBar(menubar);//添加菜单栏
    	add(menuR);menuR.setVisible(false);//添加一个影藏的右键菜单
    	add(tabbedPane);//添加选项卡
    	
    	/*状态栏*/
		line = new JPanel();line.addMouseListener(this);
		linenumber = new JLabel("行数:"+str1);
		textnumber = new JLabel("字数:"+str3);
		line.setLayout(new BorderLayout());
		lineeast = new JPanel();
		lineeast.add(linenumber);
		lineeast.add(textnumber);
		lineeast.setBorder(BorderFactory.createLineBorder(Color.lightGray));

		line.add(lineeast,BorderLayout.EAST);
		add(line,BorderLayout.SOUTH);line.setVisible(false);
		
		/*字体改变窗口*/
    	menubar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
    	fileDialog = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("txt文件","txt");//设置文件类型可选txt
    	FileNameExtensionFilter filter1 = new FileNameExtensionFilter("java文件","java");//设置文件类型可选java
    	fileDialog.setFileFilter(filter);//filter添加到文件对话框
    	fileDialog.setFileFilter(filter1);//filter1添加到文件对话框
        
    	try {
    		newFile();//程序开始时，创建一个文件
    	}catch(FileNumberException fex){
    		JOptionPane.showMessageDialog(null,fex.toString());
    	}
    	
    	buttonSelcet();//第一次判断文件菜单栏按钮可用性
    	
    	
    	
    }
    
   
    /*新建文件*/	
    void newFile() throws FileNumberException {
    	if(i>8){
    		throw (new FileNumberException());
    	}
 
        /*添加面板，新建选项卡，生成文本域对象*/
    	btn[i] = new JButton("X");
        btn[i].setForeground(Color.DARK_GRAY);
        btn[i].setBackground(new Color(200,221,245));
        btn[i].setBorderPainted(false);
        btn[i].setContentAreaFilled(false);
        btn[i].addActionListener(this);
        lab[i] = new JLabel(" 文档"+(m+1));
        lab[i].setPreferredSize(new Dimension(88, 18));
        p2[i]= new JPanel();
        p2[i].setLayout(new BorderLayout());
        p2[i].add(lab[i],BorderLayout.CENTER);
        p2[i].add(btn[i],BorderLayout.EAST);
        p2[i].setBackground(new Color(200,221,245));
        p2[i].setOpaque(false);
        p2[i].setPreferredSize(new Dimension(80, 23));
    	//p[i]=new JPanel();
       	text[i]=new JTextArea(screenSize.width/46,screenSize.height/18);
    	text[i].setFont(new Font("楷体_gb2321",Font.PLAIN,16));
    	text[i].addCaretListener(this);
    	text[i].getDocument().addDocumentListener(this);
    	
    	/*匿名函数*/
        text[i].addMouseListener(new MouseAdapter() {  
            public void mouseReleased(MouseEvent e) {  
	            /*鼠标点击事件*/	
	            if (e.getButton()==MouseEvent.BUTTON3) {  
	            	/*鼠标位置*/
	                PointerInfo pinfo = MouseInfo.getPointerInfo();
	            	Point p = pinfo.getLocation();
	            	int mx = (int) p.getX();
	            	int my = (int) p.getY();
	               //弹出右键菜单  
	            	menuR.show(text[i], mx, my);
	                menuR.setVisible(true);
	               //menuR.show(text[i], e.getX()+screenSize.width/4+100, e.getY()+screenSize.height/4+100);
	            }else {
	            	menuR.setVisible(false);  
	            }
	           /* if(e.getButton()==MouseEvent.BUTTON1) {  
	                 //关闭右键菜单  
	                 menuR.setVisible(false);  
	            } */ 
            }     
        });
    	
       	jsp[i] = new JScrollPane(text[i]);
    	tabbedPane.addTab("文档",null,jsp[i],"i");
    	buttonSelcet(); //判断按钮可用性的方法
    	tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(jsp[i]),p2[i]);//将label和X添加到选项卡上方
    	i++;//文档数加1
    	m++;//创建过的文档数加1
    	
    }

    /*保存文件*/
    void saveFile(){
    	itemSave.setEnabled(true);//setEnabeled()方法设置按钮可编辑性
		int state = fileDialog.showSaveDialog(this);//显示保存文件对话框
		index = tabbedPane.getSelectedIndex();//返回当前选择的此选项卡窗格的索引。如果当前没有选择选项卡，则返回 -1。
		if(index!=-1){
			//点击了确定按钮
			if(state==JFileChooser.APPROVE_OPTION){
				try {
					File dir=fileDialog.getCurrentDirectory();//生成当前的上一级文件对象
					String name =fileDialog.getSelectedFile().getName();//获取输入框上的文件名
					File file=new File(dir,name);
					fileWrite = new FileWriter(file);//打开文件流
					out = new BufferedWriter(fileWrite);//打开输出流
					out.write(text[index].getText());//输出流写入当前文件文本
					out.close();//关闭
					fileWrite.close();
					lab[index].setText(fileDialog.getSelectedFile().getName());//设置选项卡上的文件名
					path[index] = dir.getAbsolutePath();//获取文件对象dir的绝对路径
					
				   }catch(IOException exp) {}
			}
	    }
		 
    	
    }
    
    /*打开文件*/
    void openFile()
    {
    	int State = fileDialog.showOpenDialog(this);//显示打开文件对话框
    	index = tabbedPane.getSelectedIndex();//返回当前选择的此选项卡窗格的索引。如果当前没有选择选项卡，则返回 -1。
    	//程序中有程序的情况
    	if(index!=-1){
    		//如果当前文件没有被编辑过
    		if(flag[index]==0){
    			//确定
				if(State==JFileChooser.APPROVE_OPTION) {
				     text[index].setText(null);
				     try {
				    	 File dir = fileDialog.getCurrentDirectory();
				    	 String name = fileDialog.getSelectedFile().getName();
				    	 File file = new File(dir,name);
				    	 fileReader=new FileReader(file);
				    	 in = new BufferedReader(fileReader);
				    	 String s=null;
				    	 while ((s=in.readLine())!=null) {
				    		 text[index].append(s+"\n");
				    		 
				    	 }
				    	 in.close();
				    	 fileReader.close();
				    	 path[index]=dir.getAbsolutePath();
				     }catch(IOException exp) {}
				}
				lab[index].setText(fileDialog.getSelectedFile().getName());
				flag[index]=1;//设置当前文件flag值1
    		}
    		//如果当前文件被编辑过,导入第i-1个文件
	    	else{
	    	    try {
					newFile();
				} catch (FileNumberException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		if(State==JFileChooser.APPROVE_OPTION) {
		   		     text[i-1].setText(null);
		   		     try {
		   		    	 File dir = fileDialog.getCurrentDirectory();
		   		    	 String name = fileDialog.getSelectedFile().getName();
		   		    	 File file = new File(dir,name);
		   		    	 fileReader=new FileReader(file);
		   		    	 in = new BufferedReader(fileReader);
		   		    	 String s=null;
		   		    	 while ((s=in.readLine())!=null) {
		   		    		 text[i-1].append(s+"\n");
		   		    		 
		   		    	 }
		   		    	path[i-1] = dir.getAbsolutePath();
		   		    	 in.close();
		   		    	 fileReader.close();
		   		     }catch(IOException exp) {}
		   		     lab[i-1].setText(fileDialog.getSelectedFile().getName());
		   		  
		   		     flag[i-1]=1;
	   		    }
	    	}
    	}
    	//文件中没有文件，导入第一个文件就是i=0的情况
    	else {
    		try {
				newFile();
			} catch (FileNumberException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(State==JFileChooser.APPROVE_OPTION) {
   		     text[0].setText(null);
   		     try {
   		    	 File dir = fileDialog.getCurrentDirectory();
   		    	 String name = fileDialog.getSelectedFile().getName();
   		    	 File file = new File(dir,name);
   		    	 fileReader=new FileReader(file);
   		    	 in = new BufferedReader(fileReader);
   		    	 String s=null;
   		    	 while ((s=in.readLine())!=null) {
   		    		 text[0].append(s+"\n");
   		    		 
   		    	 }
   		    	path[0] =dir.getAbsolutePath();
   		    	 in.close();
   		    	 fileReader.close();
   		     }catch(IOException exp) {}
   		     lab[0].setText(fileDialog.getSelectedFile().getName());
   		     flag[0]=1;
    		
    		}
    	}
    	
    }
    /*删除文件*/
    void fileDelet(int j){
    	tabbedPane.removeTabAt(j);
    	
    	for(int a=j;a<=i-1;a++)
    	{
    		/*删除的选项卡后的选项卡所有对象前移一位*/
    		p2[a]=p2[a+1];
    		lab[a]=lab[a+1];
    		text[a]=text[a+1];
    	    jsp[a] = jsp[a+1];
    		btn[a]=btn[a+1];
    		flag[a]=flag[a+1];
            path[a]=path[a+1];
    		
    	}
    	i--;//文档数减1
    }
    
    /*退出软件*/
    void exit(){
    	index = tabbedPane.getSelectedIndex();
    	if(index!=-1){
	    	if(text[index].getText().equals("")){
	    		System.exit(0);
	    	}
	    	else{
	   		   int m = JOptionPane.showConfirmDialog(this,"是否保存该文档");  
	           if(m==0){  
	               saveFile();  
	           }  
	           if(m==1){   
	               System.exit(0);  
	           }  
	    	}
    	}
    	else System.exit(0);
    		
    }
    /*分解文件名，获取文件名，去除后缀*/
    String  getSplitname(String filename){
		String filenameafter = filename.substring(0,filename.lastIndexOf("."));
    	return filenameafter;
    	
    }
    
     /*按钮判断*/
    void buttonSelcet(){
    	//判断文件菜单栏中按钮的可用性
    	menuR.setVisible(false);
    	index = tabbedPane.getSelectedIndex();
    	if(index==-1){
        	
        	itemOpen.setEnabled(true);
        	itemSave.setEnabled(false);
        	itemCut.setEnabled(false);
            itemCopy.setEnabled(false);
            itemPaste.setEnabled(false);
            RCut.setEnabled(false);
            RCopy.setEnabled(false);
            RPaste.setEnabled(false);
            itemSearch.setEnabled(false);
            lineWrapSelect.setEnabled(false);
            itemFont.setEnabled(false);
            itemJava.setEnabled(false);
            itemJavac.setEnabled(false);
            itemlookSelect.setEnabled(false);
            itemChange.setEnabled(false);
            itemColor.setEnabled(false);

    	}
    	else{
        	itemOpen.setEnabled(true);
        	itemSave.setEnabled(true);
            itemJava.setEnabled(true);
            itemJavac.setEnabled(true);
        	careEditButtonSelect();
            itemSearch.setEnabled(true);
            lineWrapSelect.setEnabled(true);
            itemFont.setEnabled(true);
            itemlookSelect.setEnabled(true);
            itemChange.setEnabled(true);
            itemColor.setEnabled(true);
    	}
    	
    }
    /*X按钮选择*/
    void btnSelect(ActionEvent select){
    	for(int j=0;j<10;j++){
        	if(select.getSource() == btn[j]){
       		   int m = JOptionPane.showConfirmDialog(this,"是否保存该文件");  
               if(m==0){  
                   saveFile();  //是则保存文件
               }  
               if(m==1){   
            	   fileDelet(j); //否则删除文件
               }  
               buttonSelcet();//判断按钮
        			
        	}
    		
    	}    	
    }
    
    /*判断编辑处和菜单处按钮是否可用*/
    void careEditButtonSelect(){
       	if(text[index].getText().equals("")){
    		RAll.setEnabled(false);
    		itemAll.setEnabled(false);
    	}
       	else{
       		RAll.setEnabled(true);
       		itemAll.setEnabled(true);
       	}
        //如果选中文本光标处的（初始位置-末尾位置=0）键入末尾
    	if(dot-mark==0){
            itemCut.setEnabled(false);
            itemCopy.setEnabled(false);
            itemPaste.setEnabled(true);
            RCut.setEnabled(false);
            RCopy.setEnabled(false);
            RPaste.setEnabled(true);
    		
    	}
    	else{
            itemCut.setEnabled(true);
            itemCopy.setEnabled(true);
            itemPaste.setEnabled(true);
            RCut.setEnabled(true);
            RCopy.setEnabled(true);
            RPaste.setEnabled(true);
    		
    	}
    }
    /*获取当前文件的行数，字数*/
    void getInformation(){
    	int index = tabbedPane.getSelectedIndex();
    	str1 = "行数:"+String.valueOf(text[index].getLineCount())+",";
    	linenumber.setText(str1);
    	str3 = "字数:"+String.valueOf(getFontNumber(text[index])+"                                           ");
    	textnumber.setText(str3);
    }
    /*获取指定文本的字数*/
    int getFontNumber(JTextArea text){
    	int number=0;
    	int line = text.getLineCount();
    	String s = text.getText();
    	number = s.length();
        number = number - line +1;
    	return number;
    }
    /*设置颜色*/
    void setcolor(){  
           Color fontcolor=JColorChooser.showDialog(this,"字体颜色选择",text[index].getForeground());  
           text[index].setForeground(fontcolor);  
    } 

    public void actionPerformed(ActionEvent e){
    	
    	buttonSelcet();//判断按钮可用性
    	btnSelect(e);//获取监听对象，关闭要关闭的选项卡
    	
    	if(e.getSource()==itemBulid){
    		try {
    			newFile();
    		}catch(FileNumberException fex){
    			JOptionPane.showMessageDialog(null,fex.toString());
    		}
    	}
    	if(e.getSource()==itemSave){	
    		saveFile();
    	}
    	if(e.getSource()==itemOpen){
    		openFile();
    	}
    	if(e.getSource()==itemExit){
    	    exit();
    	}
    	if(e.getSource()==itemAll||e.getSource()==RAll){
    		
    		text[index].selectAll();
    	}
    	if(e.getSource()==itemCut||e.getSource()==RCut){
    	    text[index].cut();	
        }
    	if(e.getSource()==itemCopy||e.getSource()==RCopy){
    		text[index].copy();	
        }
    	if(e.getSource()==itemPaste||e.getSource()==RPaste){
    		text[index].paste();	
        }
    	if(e.getSource()==itemSearch){
    		@SuppressWarnings("unused")
			SearchFrame search = new SearchFrame(text[index]);
    	}
    	if(e.getSource()==itemChange){
    		replace.set(text[index]);
    		replace.setVisible(true);
    	}
    	if(e.getSource()==itemJava){
    		runWindows jdk1 = new runWindows();
    		String s = getSplitname(lab[index].getText());
    		//System.out.println(s);
    		//System.out.println(path[index]);
		    jdk1.rrun(path[index],s);
		    jdk1.setVisible(true);
				
    	}
    	if(e.getSource()==itemJavac){
    		CompileWindow jdk = new CompileWindow();	
    		jdk.compile(path[index]+"\\"+lab[index].getText());
    		jdk.setVisible(true);
    	}
    	if(e.getSource()==itemAbout){
    		new myversion();
    		
    	}
    	if(e.getSource()==lineWrapSelect){
    		if(lineWrapSelect.isSelected()) text[index].setLineWrap(true);
    		else text[index].setLineWrap(false);;	
        }
    	if(e.getSource()==itemlookSelect){
    		getInformation();
    		if(itemlookSelect.isSelected())
    		{
               line.setVisible(true);
    		}
    		else line.setVisible(false);
        }
    	if(e.getSource()==itemFont){
    		fontChange.set(text[index]);
    		fontChange.setVisible(true);
    	}
    	if(e.getSource()==itemColor){
    		setcolor();
    	}
    	menuR.setVisible(false);
    	
    }
    /*选中事件*/
    public void caretUpdate(CaretEvent e) {
    	
        dot = e.getDot(); // 取得光标开始位置
        mark = e.getMark();// 取得光标结束位置
        careEditButtonSelect();
    }
    /*文档事件的三个接口*/
    public void changedUpdate(DocumentEvent e) {
    	getInformation();
        flag[index]=1;
    }  
      
    public void insertUpdate(DocumentEvent e) { 
    	getInformation();
    	flag[index]=1;
    }  
    public void removeUpdate(DocumentEvent e) {  
    	getInformation();
    	flag[index]=1;
    }


	@Override
	public void mouseClicked(MouseEvent e) {
		menuR.setVisible(false);
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		menuR.setVisible(false);
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		menuR.setVisible(false);
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}  

	


}


/*版本介绍窗口*/
@SuppressWarnings("serial")
class myversion extends JFrame {
	
	
	    Label l1 = new Label("Author:  Yuhong ");
        Label l2 = new Label("Version 1.0");
	    Label l3 = new Label("2018.1.9");
	    Label l4 = new Label("Tel:18361489220");

	    @SuppressWarnings("static-access")
		myversion() {
	    	super("关于EditPad");
	        this.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));

	        this.add(l1);
	        this.add(l2);
	        this.add(l3);
	        this.add(l4);

	        this.setBounds(200, 180, 300, 200);
	        this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
	        this.setVisible(true);
	   }


}

@SuppressWarnings("serial")
/*字体颜色界面*/
class FontChange extends JFrame implements ItemListener,ActionListener{  
  
    /*获取屏幕分辨率*/
	Toolkit kit = Toolkit.getDefaultToolkit(); 
    Dimension screenSize = kit.getScreenSize();
    
    JPanel panel1 = new JPanel();  
    JPanel panel2 = new JPanel();  
    JPanel panel3 = new JPanel();  
    @SuppressWarnings("rawtypes")
	JComboBox comboBox1 = new JComboBox();  
    @SuppressWarnings("rawtypes")
	JComboBox comboBox2 = new JComboBox();  
    @SuppressWarnings("rawtypes")
	JComboBox comboBox3 = new JComboBox();  
    JLabel lab1=new JLabel("字体：");  
    JLabel lab2=new JLabel("字形：");  
    JLabel lab3=new JLabel("字号：");  
    String name=new String("宋体");  
    Font f1=new Font("隶书",Font.PLAIN,15);  
    int style=1;  
    int size=12;  
    String []array2=new String[]{"正常","倾斜","加粗"};  
    String []array3=new String[]{"14","15","15","16","17","18"};  
    GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();  
    String [] fontName=ge.getAvailableFontFamilyNames();
    JButton b1=new JButton("确定");  
    JButton b2=new JButton("取消");  
    JTextArea a1 = new JTextArea();
    void set(JTextArea n){  
        a1=n;  
    } 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public FontChange(){  
        setTitle("字体");  
        setSize(400,150);  
        setLayout(new BorderLayout());  
        this.setLocation(screenSize.width/4,screenSize.height/4);//位置 
        comboBox1.setModel(new DefaultComboBoxModel(fontName));  
        comboBox1.setFont(f1);  
        comboBox2.setModel(new DefaultComboBoxModel(array2));  
        comboBox2.setFont(f1);  
        comboBox3.setModel(new DefaultComboBoxModel(array3));  
        comboBox3.setFont(f1);     
        panel1.add(lab1);  
        panel1.add(comboBox1);  
        panel2.add(lab2);  
        panel2.add(comboBox2);  
        panel2.add(lab3);  
        panel2.add(comboBox3);  
        panel3.add(b1);  
        panel3.add(b2);  
        b2.addActionListener(this); //设置监听 
        comboBox1.addItemListener(this);  
        comboBox2.addItemListener(this);  
        comboBox3.addItemListener(this);  
        b1.addActionListener(this);  //设置监听 
        add(panel2,BorderLayout.NORTH);  
        add(panel1,BorderLayout.CENTER);  
        add(panel3,BorderLayout.SOUTH); 
        setVisible(false);
    }  
    public void itemStateChanged(ItemEvent e) {  
        if(e.getSource()==comboBox1){  
            name=comboBox1.getSelectedItem().toString();  
        }  
        if(e.getSource()==comboBox2){  
          String s1=comboBox2.getSelectedItem().toString();  
          if(s1.equals("加粗")){  
          style=Font.BOLD;  
          }  
          if(s1.equals("倾斜")){  
              style=Font.ITALIC;  
          }  
          if(s1.equals("正常")){  
              style=Font.PLAIN;  
          }  
        }  
        if(e.getSource()==comboBox3){  
             String s2=comboBox3.getSelectedItem().toString();  
             size=Integer.parseInt(s2);  
        }  
          
    }  
    public void actionPerformed(ActionEvent e2) { 
    	if(e2.getActionCommand()=="确定")
        {
            Font f=new Font(name,style,size);  
            a1.setFont(f); 
            setVisible(true);
        }  
        if(e2.getActionCommand()=="取消")
        {
        	setVisible(false);
        	
        }
 
    }  
   


}  
/*替换窗口*/
@SuppressWarnings("serial")
class ReplaceFrame extends JFrame implements ActionListener{

	String str1,str2,str3;
	String str4="";
	JButton button1,button2,button3;
	JTextField texts,textd;
	JTextArea textarea;
	void set(JTextArea t){
		textarea=t;
	}
	//实现查找替换函数功能
    ReplaceFrame(){
    	  super("替换");
    	  button1=new JButton("替换下一个");
    	  button2=new JButton("全部替换");
    	  button3=new JButton("取消");
    	  button1.addActionListener(this);
    	  button2.addActionListener(this);
    	  button3.addActionListener(this);
    	  texts=new JTextField(10);
    	  textd=new JTextField(10);
    	  JPanel pan1=new JPanel();
    	  JPanel pan2=new JPanel();
    	  JPanel pan3=new JPanel();
    	  pan1.add(new JLabel("查    找:"));
    	  pan1.add(texts);
    	  pan2.add(new JLabel("替换为:"));
    	  pan2.add(textd);
    	  //pan2.add(button3);
    	  pan3.add(button1);
     	  pan3.add(button2);
    	  add(pan1,"North");
    	  add(pan2,"Center");
    	  add(pan3,"South");
    	  setSize(300,150);
    	  setLocation(300,300);
    	  setVisible(false);
    	  setResizable(false);
  
     }

   void change(){
	   str1=textarea.getText();
	   str2=texts.getText();//被替换的字符串
	   str3=textd.getText();//目的字符串
	   int i=str1.indexOf(str2);//获取str1在文本中的位置
	   if(i!=-1&&str1!=""&&str2!=""&&str3!="")
	   {
	       str4=str1.substring(0,i)+str3+str1.substring(i+str2.length());
	       textarea.setText(str4);
	       textarea.setCaretPosition(i);
	   }
   }
   void allChange(){
	   str1=textarea.getText();
	   str2=texts.getText();//被替换的字符串
	   str3=textd.getText();//目的字符串
	   str1 = str1.replaceAll(str2, str3);//正则匹配替换
	   textarea.setText(str1);
	   
   }
   public void actionPerformed(ActionEvent e){
	   if(e.getSource()==button1)
	   {
		   change();
	   }
	   if(e.getSource()==button2)
	   {
		   allChange();
	   }
	   if(e.getSource()==button3)
	   {
		   setVisible(false);
	   }
   }

}

@SuppressWarnings("serial")
class SearchFrame extends JFrame implements ActionListener{
	  private JTextArea jta = new JTextArea();
	  JTextField jtf = new JTextField(10);
	  JFrame jf1 = new JFrame();   
	  ButtonGroup bg = new ButtonGroup();
	  String ss = new String();//向下查询用
	  int next = 0;//从何处开始查找
	  int start = 0;//查到的第一个字符的起始位置
	  boolean  b = true;//判断是向上还是向下查询
	  //有参构造
	  public SearchFrame(JTextArea jta){
		  this.setTitle("查找");
		  this.jta = jta;
		  
		  jf1.setLayout(new GridLayout(3,1));   
		   
		  JPanel jp1 = new JPanel();
		  JLabel jl1 = new JLabel("查找内容");
		       
		  jp1.add(jl1);
		  jp1.add(jtf);
		  jf1.add(jp1);
		   
		  JPanel jp2 = new JPanel();
		   
		  JRadioButton jr1 = new JRadioButton("向上(U)");
		  jr1.setMnemonic(KeyEvent.VK_B);
		  jr1.setActionCommand("向上(U)");
		  jr1.setSelected(true);
		  jr1.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e) {
		          b = true;           
		      }      
		  });     
		   
		  JRadioButton jr2 = new JRadioButton("向下(D)");
		  jr2.setMnemonic(KeyEvent.VK_B);
		  jr2.setActionCommand("向上(D)");
		  jr2.addActionListener(new ActionListener(){
		   
		      public void actionPerformed(ActionEvent e) {
		          b = false;           
		      }      
		  });     
		  JLabel jl2 = new JLabel("方向");
		  jp2.add(jl2);
		  bg.add(jr1);
		  bg.add(jr2);
		  jp2.add(jr1);
		  jp2.add(jr2);
		  jf1.add(jp2);
		
		  JPanel jp3 = new JPanel();
		  JButton jb1 = new JButton("查找下一个");
		  jb1.addActionListener(this);
		  JButton jb2 = new JButton("取消");
		  jb2.addActionListener(this);
		  jp3.add(jb1);
		  jp3.add(jb2);
		  jf1.add(jp3);
		   
		  jf1.setTitle("查找");
		  jf1.setSize(250,150);
		  jf1.setLocation(400,300);//设置窗口位置
		  jf1.setVisible(true);//尽可能放后
		  jf1.setResizable(false);//窗口大小固定
	  }
	
	  public void actionPerformed(ActionEvent e) {
	      String comm = e.getActionCommand();
	      String text = jta.getText();
	      String jtff = jtf.getText();
	      int length = jtff.length();
	      if("查找下一个".equals(comm)){   
		  if(jtff.equals("")){
		      JOptionPane.showMessageDialog(null, "请输入内容！", "", JOptionPane.ERROR_MESSAGE);
		  }else{
			  if(b){
			      //向上查询       
			      if(text.indexOf(jtff,next)!=-1){//返回指定字符在此字符串中第一次出现处的索引
	                 start = text.indexOf(jtff,next);
	                 jta.setSelectionStart(start);
	                 jta.setSelectionEnd(start+length);
	                 next = start+length;                          
		          }else{
		           JOptionPane.showMessageDialog(null, "very sorry!we cann't find it!", "", JOptionPane.ERROR_MESSAGE);   
		          }               
			 }else{//向下查询
			     if(text.lastIndexOf(jtff,start-1)!=-1){
			         int n = text.lastIndexOf(jtff,start-1);
			         jta.setSelectionStart(n);
			         jta.setSelectionEnd(n+length);
			         start = n;
			         next = n + length;
			     }else{ 
			         JOptionPane.showMessageDialog(null, "very sorry!we cann't find it!", "", JOptionPane.ERROR_MESSAGE);     
			     }
			 }
		  }
	    }else if("取消".equals(comm)){
	        jf1.setVisible(false);
	    }
	 }   
}

/*编译对话框*/
@SuppressWarnings("serial")
class CompileWindow extends JDialog{
	JTextArea showError;
	
	@SuppressWarnings("static-access")
	CompileWindow(){
		setTitle("编译运行对话框");
		showError = new JTextArea();
		add(new JScrollPane(showError),BorderLayout.CENTER);
		//this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		setBounds(10,10,500,500);
	}
	public void compile(String name){

		try {
			Runtime ce = Runtime.getRuntime();
			Process proccess =ce.exec("javac "+name);
			InputStream in = proccess.getErrorStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			int n;
			boolean bn = true;
			byte error[]=new byte[100];
			while((n=bin.read(error,0,100))!=-1){
				String s=null;
				s = new String(error,0,n);
				showError.append(s);
				if(s!=null) bn=false;
			}
			if(bn) showError.append("编译成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}

/*运行对话框*/
@SuppressWarnings("serial")
class runWindows extends JDialog{
	JTextArea showError;
	
	@SuppressWarnings("static-access")
	runWindows(){
		setTitle("编译运行对话框");
		showError = new JTextArea();
		add(new JScrollPane(showError),BorderLayout.CENTER);
		//this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		setBounds(10,10,500,500);
	}
	@SuppressWarnings("unused")
	public void rrun(String filepath,String filename){
		//showError.setText("");
		try {
			Runtime ce = Runtime.getRuntime();
			Process proccess =ce.exec("java "+filename,null,new File(filepath));
			

			
			InputStream in1 = proccess.getErrorStream();
			BufferedInputStream bin1 = new BufferedInputStream(in1);
			int n1;
			boolean bn1 = true;
			byte error1[]=new byte[100];
			while((n1=bin1.read(error1,0,100))!=-1){
				String s=null;
				s = new String(error1,0,n1);
				showError.append(s);
				if(s!=null) bn1=false;
			}
			
			
			InputStream in = proccess.getInputStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			int n;
			boolean bn = true;
			byte error[]=new byte[100];
			while((n=bin.read(error,0,100))!=-1){
				String s=null;
				s = new String(error,0,n);
				showError.append(s);
				if(s!=null) bn=false;
				if(bn) showError.append("未使用out流");
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	
}


