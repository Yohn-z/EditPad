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
       Win TArea = new Win();//�������壬���ɴ���
       //�������ڽ����¼�
       TArea.addWindowFocusListener(new WindowFocusListener() {   
           public void windowGainedFocus(WindowEvent e) {   
               
           }   
 
           public void windowLostFocus(WindowEvent e) { 
        	   TArea.menuR.setVisible(false);
           }   
       });   
       //��������״̬�¼�
       TArea.addWindowStateListener(new WindowStateListener() {   
           public void windowStateChanged(WindowEvent e) {  
        	   TArea.menuR.setVisible(false);
           }   
       });   
       TArea.setVisible(true);//���ÿɼ���
    }
}

/*����һ���ĵ������쳣*/
@SuppressWarnings("serial")
class FileNumberException extends Exception{
	String s;
	FileNumberException(){
		s = "�ĵ�����������";
	}
	public String toString(){
		return s;
	}
	
}

@SuppressWarnings("serial")
class Win extends JFrame implements ActionListener,CaretListener,DocumentListener,MouseListener/*��ť����ꡢ�ĵ��¼�����*/{
	int i=0;//�½��ĵ�����
	int m=0;//��ʾ�ĵ���
	int index=1;//ѡ�λ��
	int flag[] = {0,0,0,0,0,0,0,0,0,0};//�ĵ���flag��ʼֵ��0��ʾδ���ı�
	String path[] = new String[10]; //�ĵ���һ���ľ���·��
	int dot=0,mark=0;//dot������ʼλ�ã�mark����ĩβλ��
	String str1="0",str3="0";//������������ʼֵ0
	JTabbedPane tabbedPane;//ѡ����
	JPanel p2[]=new JPanel[10];//ѡ��ϵİ�ť���������label�Ͱ�ť"X"
	JLabel lab[] = new JLabel[10];//������ʾ�ĵ���
	JTextArea text[]= new JTextArea[10];//�ı���
	JScrollPane jsp[] = new JScrollPane[10];//������
	JButton btn[] = new JButton[10];//ѡ��ϵ�"X"��ť
	
	JFileChooser fileDialog;//�ɻ�ȡ�ļ��Ի���
	
	JMenuBar menubar;//�˵���
    JMenu menu,edit,formation,run,look,help;//�ļ����༭,��ʽ�����룬�鿴������
	JMenuItem itemBulid,itemSave,itemOpen,itemExit;//�ļ��½����ļ����棬�ļ��򿪡��˳�
	JMenuItem itemAll,itemCut,itemCopy,itemPaste,RAll,RCut,RCopy,RPaste,itemSearch,itemChange;//���С����ơ����
	JMenuItem itemFont,itemColor;//�Զ����У�����
	JMenuItem itemJavac,itemJava;//���������java����
	JRadioButton lineWrapSelect,itemlookSelect;//�Զ����С�״̬��
	JMenuItem itemHelp,itemAbout;//�������й�

	JPopupMenu menuR;//�Ҽ��˵���
	
	JPanel line;//�ײ�״̬��
	JPanel lineeast;//�ײ�״̬���������
	JLabel linenumber,textnumber;//��ʾ�ײ�״̬���ĵ�����������
	
	FileReader fileReader;//�ļ���
    FileWriter fileWrite;
    BufferedReader in;//������
    BufferedWriter out;
    
    FontChange fontChange = new FontChange();//��ɫ����
    ReplaceFrame replace = new ReplaceFrame();//�滻����
  
    /*��ȡ��Ļ�ֱ���*/
	Toolkit kit = Toolkit.getDefaultToolkit(); 
    Dimension screenSize = kit.getScreenSize();//��ȡ��Ļ�ֱ���  
   

    Win(){
    	
    	super("EditPad-v1.0");
        setSize(screenSize.width/2,screenSize.height/2);//��С  
        setLocation(screenSize.width/4,screenSize.height/4);//λ��
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//���ÿɹر�
        
        /*��ʼ��ѡ����*/
        tabbedPane = new JTabbedPane();tabbedPane.addMouseListener(this);
        /*��ʼ���Ҽ��˵�*/
        menuR = new JPopupMenu();
        /*���ͼ��*/
        java.net.URL imgURL = Frame.class.getResource("/img/frameicon.png");
        ImageIcon imgIcon = new ImageIcon(imgURL);
        Image img = imgIcon.getImage();
        this.setIconImage(img);
        
    	/*�˵���*/
    	menubar = new JMenuBar();menubar.addMouseListener(this);
    	/*һ���˵���*/
    	menu = new JMenu("�ļ�(F)");menu.addMouseListener(this);
    	edit = new JMenu("�༭(E)");edit.addMouseListener(this);
    	formation = new JMenu("��ʽ(O)");formation.addMouseListener(this);
    	run = new JMenu("����(C)");run.addMouseListener(this);
    	look = new JMenu("�鿴(V)");look.addMouseListener(this);
    	help = new JMenu("����(H)");help.addMouseListener(this);
    	
    	/*�����˵���*/
    	itemBulid= new JMenuItem(" �½� (N)  ");itemBulid.addActionListener(this);
    	itemOpen = new JMenuItem(" ��(O)   ");itemOpen.addActionListener(this);
    	itemSave = new JMenuItem(" ����(S)   ");itemSave.addActionListener(this);
        itemExit = new JMenuItem(" �˳�(X)   ");itemExit.addActionListener(this);
        itemAll  = new JMenuItem(" ȫѡ(A)   ");itemAll.addActionListener(this);
        itemCut  = new JMenuItem(" ����(T)   ");itemCut.addActionListener(this);
        itemCopy  = new JMenuItem(" ����(C)   ");itemCopy.addActionListener(this);
        itemPaste  = new JMenuItem(" ���(P)   ");itemPaste.addActionListener(this);
        RAll  = new JMenuItem(" ȫѡ(A)   ");RAll.addActionListener(this);
        RCut  = new JMenuItem(" ����(T)   ");RCut.addActionListener(this);
        RCopy  = new JMenuItem(" ����(C)   ");RCopy.addActionListener(this);
        RPaste  = new JMenuItem(" ���(P)   ");RPaste.addActionListener(this);
        itemSearch  = new JMenuItem(" ����(F)   ");itemSearch.addActionListener(this);
        itemChange  = new JMenuItem(" �滻(R)   ");itemChange.addActionListener(this);
        itemJava  = new JMenuItem(" ����java    ");itemJava.addActionListener(this);
        itemJavac  = new JMenuItem(" ����java    ");itemJavac.addActionListener(this);
        lineWrapSelect = new JRadioButton("�Զ�����(W)");lineWrapSelect.addActionListener(this);
        itemFont = new JMenuItem("     ����(F) ");itemFont.addActionListener(this);
        itemColor = new JMenuItem("     ��ɫ(C) ");itemColor.addActionListener(this);
        itemlookSelect = new JRadioButton("״̬��(s)");itemlookSelect.addActionListener(this);
        itemHelp = new JMenuItem(" �鿴����(H)   ");itemHelp.addActionListener(this);
        itemAbout = new JMenuItem(" �йؼ��±�(A)   ");itemAbout.addActionListener(this);
        
    	/*�����������*/
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
        
    	/*һ���������*/
    	menubar.add(menu);//����ļ�
    	menubar.add(edit);//��ӱ༭
    	menubar.add(formation);//��ʽ
    	menubar.add(run);
    	menubar.add(look);
    	menubar.add(help);//����
    	
    	/*�Ҽ��˵����*/
    	menuR.add(RAll);
        menuR.add(RCopy); 
        menuR.add(RCut);  
        menuR.add(RPaste);
        
    	
    	
    	setJMenuBar(menubar);//��Ӳ˵���
    	add(menuR);menuR.setVisible(false);//���һ��Ӱ�ص��Ҽ��˵�
    	add(tabbedPane);//���ѡ�
    	
    	/*״̬��*/
		line = new JPanel();line.addMouseListener(this);
		linenumber = new JLabel("����:"+str1);
		textnumber = new JLabel("����:"+str3);
		line.setLayout(new BorderLayout());
		lineeast = new JPanel();
		lineeast.add(linenumber);
		lineeast.add(textnumber);
		lineeast.setBorder(BorderFactory.createLineBorder(Color.lightGray));

		line.add(lineeast,BorderLayout.EAST);
		add(line,BorderLayout.SOUTH);line.setVisible(false);
		
		/*����ı䴰��*/
    	menubar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
    	fileDialog = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("txt�ļ�","txt");//�����ļ����Ϳ�ѡtxt
    	FileNameExtensionFilter filter1 = new FileNameExtensionFilter("java�ļ�","java");//�����ļ����Ϳ�ѡjava
    	fileDialog.setFileFilter(filter);//filter��ӵ��ļ��Ի���
    	fileDialog.setFileFilter(filter1);//filter1��ӵ��ļ��Ի���
        
    	try {
    		newFile();//����ʼʱ������һ���ļ�
    	}catch(FileNumberException fex){
    		JOptionPane.showMessageDialog(null,fex.toString());
    	}
    	
    	buttonSelcet();//��һ���ж��ļ��˵�����ť������
    	
    	
    	
    }
    
   
    /*�½��ļ�*/	
    void newFile() throws FileNumberException {
    	if(i>8){
    		throw (new FileNumberException());
    	}
 
        /*�����壬�½�ѡ��������ı������*/
    	btn[i] = new JButton("X");
        btn[i].setForeground(Color.DARK_GRAY);
        btn[i].setBackground(new Color(200,221,245));
        btn[i].setBorderPainted(false);
        btn[i].setContentAreaFilled(false);
        btn[i].addActionListener(this);
        lab[i] = new JLabel(" �ĵ�"+(m+1));
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
    	text[i].setFont(new Font("����_gb2321",Font.PLAIN,16));
    	text[i].addCaretListener(this);
    	text[i].getDocument().addDocumentListener(this);
    	
    	/*��������*/
        text[i].addMouseListener(new MouseAdapter() {  
            public void mouseReleased(MouseEvent e) {  
	            /*������¼�*/	
	            if (e.getButton()==MouseEvent.BUTTON3) {  
	            	/*���λ��*/
	                PointerInfo pinfo = MouseInfo.getPointerInfo();
	            	Point p = pinfo.getLocation();
	            	int mx = (int) p.getX();
	            	int my = (int) p.getY();
	               //�����Ҽ��˵�  
	            	menuR.show(text[i], mx, my);
	                menuR.setVisible(true);
	               //menuR.show(text[i], e.getX()+screenSize.width/4+100, e.getY()+screenSize.height/4+100);
	            }else {
	            	menuR.setVisible(false);  
	            }
	           /* if(e.getButton()==MouseEvent.BUTTON1) {  
	                 //�ر��Ҽ��˵�  
	                 menuR.setVisible(false);  
	            } */ 
            }     
        });
    	
       	jsp[i] = new JScrollPane(text[i]);
    	tabbedPane.addTab("�ĵ�",null,jsp[i],"i");
    	buttonSelcet(); //�жϰ�ť�����Եķ���
    	tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(jsp[i]),p2[i]);//��label��X��ӵ�ѡ��Ϸ�
    	i++;//�ĵ�����1
    	m++;//���������ĵ�����1
    	
    }

    /*�����ļ�*/
    void saveFile(){
    	itemSave.setEnabled(true);//setEnabeled()�������ð�ť�ɱ༭��
		int state = fileDialog.showSaveDialog(this);//��ʾ�����ļ��Ի���
		index = tabbedPane.getSelectedIndex();//���ص�ǰѡ��Ĵ�ѡ�����������������ǰû��ѡ��ѡ����򷵻� -1��
		if(index!=-1){
			//�����ȷ����ť
			if(state==JFileChooser.APPROVE_OPTION){
				try {
					File dir=fileDialog.getCurrentDirectory();//���ɵ�ǰ����һ���ļ�����
					String name =fileDialog.getSelectedFile().getName();//��ȡ������ϵ��ļ���
					File file=new File(dir,name);
					fileWrite = new FileWriter(file);//���ļ���
					out = new BufferedWriter(fileWrite);//�������
					out.write(text[index].getText());//�����д�뵱ǰ�ļ��ı�
					out.close();//�ر�
					fileWrite.close();
					lab[index].setText(fileDialog.getSelectedFile().getName());//����ѡ��ϵ��ļ���
					path[index] = dir.getAbsolutePath();//��ȡ�ļ�����dir�ľ���·��
					
				   }catch(IOException exp) {}
			}
	    }
		 
    	
    }
    
    /*���ļ�*/
    void openFile()
    {
    	int State = fileDialog.showOpenDialog(this);//��ʾ���ļ��Ի���
    	index = tabbedPane.getSelectedIndex();//���ص�ǰѡ��Ĵ�ѡ�����������������ǰû��ѡ��ѡ����򷵻� -1��
    	//�������г�������
    	if(index!=-1){
    		//�����ǰ�ļ�û�б��༭��
    		if(flag[index]==0){
    			//ȷ��
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
				flag[index]=1;//���õ�ǰ�ļ�flagֵ1
    		}
    		//�����ǰ�ļ����༭��,�����i-1���ļ�
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
    	//�ļ���û���ļ��������һ���ļ�����i=0�����
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
    /*ɾ���ļ�*/
    void fileDelet(int j){
    	tabbedPane.removeTabAt(j);
    	
    	for(int a=j;a<=i-1;a++)
    	{
    		/*ɾ����ѡ����ѡ����ж���ǰ��һλ*/
    		p2[a]=p2[a+1];
    		lab[a]=lab[a+1];
    		text[a]=text[a+1];
    	    jsp[a] = jsp[a+1];
    		btn[a]=btn[a+1];
    		flag[a]=flag[a+1];
            path[a]=path[a+1];
    		
    	}
    	i--;//�ĵ�����1
    }
    
    /*�˳����*/
    void exit(){
    	index = tabbedPane.getSelectedIndex();
    	if(index!=-1){
	    	if(text[index].getText().equals("")){
	    		System.exit(0);
	    	}
	    	else{
	   		   int m = JOptionPane.showConfirmDialog(this,"�Ƿ񱣴���ĵ�");  
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
    /*�ֽ��ļ�������ȡ�ļ�����ȥ����׺*/
    String  getSplitname(String filename){
		String filenameafter = filename.substring(0,filename.lastIndexOf("."));
    	return filenameafter;
    	
    }
    
     /*��ť�ж�*/
    void buttonSelcet(){
    	//�ж��ļ��˵����а�ť�Ŀ�����
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
    /*X��ťѡ��*/
    void btnSelect(ActionEvent select){
    	for(int j=0;j<10;j++){
        	if(select.getSource() == btn[j]){
       		   int m = JOptionPane.showConfirmDialog(this,"�Ƿ񱣴���ļ�");  
               if(m==0){  
                   saveFile();  //���򱣴��ļ�
               }  
               if(m==1){   
            	   fileDelet(j); //����ɾ���ļ�
               }  
               buttonSelcet();//�жϰ�ť
        			
        	}
    		
    	}    	
    }
    
    /*�жϱ༭���Ͳ˵�����ť�Ƿ����*/
    void careEditButtonSelect(){
       	if(text[index].getText().equals("")){
    		RAll.setEnabled(false);
    		itemAll.setEnabled(false);
    	}
       	else{
       		RAll.setEnabled(true);
       		itemAll.setEnabled(true);
       	}
        //���ѡ���ı���괦�ģ���ʼλ��-ĩβλ��=0������ĩβ
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
    /*��ȡ��ǰ�ļ�������������*/
    void getInformation(){
    	int index = tabbedPane.getSelectedIndex();
    	str1 = "����:"+String.valueOf(text[index].getLineCount())+",";
    	linenumber.setText(str1);
    	str3 = "����:"+String.valueOf(getFontNumber(text[index])+"                                           ");
    	textnumber.setText(str3);
    }
    /*��ȡָ���ı�������*/
    int getFontNumber(JTextArea text){
    	int number=0;
    	int line = text.getLineCount();
    	String s = text.getText();
    	number = s.length();
        number = number - line +1;
    	return number;
    }
    /*������ɫ*/
    void setcolor(){  
           Color fontcolor=JColorChooser.showDialog(this,"������ɫѡ��",text[index].getForeground());  
           text[index].setForeground(fontcolor);  
    } 

    public void actionPerformed(ActionEvent e){
    	
    	buttonSelcet();//�жϰ�ť������
    	btnSelect(e);//��ȡ�������󣬹ر�Ҫ�رյ�ѡ�
    	
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
    /*ѡ���¼�*/
    public void caretUpdate(CaretEvent e) {
    	
        dot = e.getDot(); // ȡ�ù�꿪ʼλ��
        mark = e.getMark();// ȡ�ù�����λ��
        careEditButtonSelect();
    }
    /*�ĵ��¼��������ӿ�*/
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


/*�汾���ܴ���*/
@SuppressWarnings("serial")
class myversion extends JFrame {
	
	
	    Label l1 = new Label("Author:  Yuhong ");
        Label l2 = new Label("Version 1.0");
	    Label l3 = new Label("2018.1.9");
	    Label l4 = new Label("Tel:18361489220");

	    @SuppressWarnings("static-access")
		myversion() {
	    	super("����EditPad");
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
/*������ɫ����*/
class FontChange extends JFrame implements ItemListener,ActionListener{  
  
    /*��ȡ��Ļ�ֱ���*/
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
    JLabel lab1=new JLabel("���壺");  
    JLabel lab2=new JLabel("���Σ�");  
    JLabel lab3=new JLabel("�ֺţ�");  
    String name=new String("����");  
    Font f1=new Font("����",Font.PLAIN,15);  
    int style=1;  
    int size=12;  
    String []array2=new String[]{"����","��б","�Ӵ�"};  
    String []array3=new String[]{"14","15","15","16","17","18"};  
    GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();  
    String [] fontName=ge.getAvailableFontFamilyNames();
    JButton b1=new JButton("ȷ��");  
    JButton b2=new JButton("ȡ��");  
    JTextArea a1 = new JTextArea();
    void set(JTextArea n){  
        a1=n;  
    } 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public FontChange(){  
        setTitle("����");  
        setSize(400,150);  
        setLayout(new BorderLayout());  
        this.setLocation(screenSize.width/4,screenSize.height/4);//λ�� 
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
        b2.addActionListener(this); //���ü��� 
        comboBox1.addItemListener(this);  
        comboBox2.addItemListener(this);  
        comboBox3.addItemListener(this);  
        b1.addActionListener(this);  //���ü��� 
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
          if(s1.equals("�Ӵ�")){  
          style=Font.BOLD;  
          }  
          if(s1.equals("��б")){  
              style=Font.ITALIC;  
          }  
          if(s1.equals("����")){  
              style=Font.PLAIN;  
          }  
        }  
        if(e.getSource()==comboBox3){  
             String s2=comboBox3.getSelectedItem().toString();  
             size=Integer.parseInt(s2);  
        }  
          
    }  
    public void actionPerformed(ActionEvent e2) { 
    	if(e2.getActionCommand()=="ȷ��")
        {
            Font f=new Font(name,style,size);  
            a1.setFont(f); 
            setVisible(true);
        }  
        if(e2.getActionCommand()=="ȡ��")
        {
        	setVisible(false);
        	
        }
 
    }  
   


}  
/*�滻����*/
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
	//ʵ�ֲ����滻��������
    ReplaceFrame(){
    	  super("�滻");
    	  button1=new JButton("�滻��һ��");
    	  button2=new JButton("ȫ���滻");
    	  button3=new JButton("ȡ��");
    	  button1.addActionListener(this);
    	  button2.addActionListener(this);
    	  button3.addActionListener(this);
    	  texts=new JTextField(10);
    	  textd=new JTextField(10);
    	  JPanel pan1=new JPanel();
    	  JPanel pan2=new JPanel();
    	  JPanel pan3=new JPanel();
    	  pan1.add(new JLabel("��    ��:"));
    	  pan1.add(texts);
    	  pan2.add(new JLabel("�滻Ϊ:"));
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
	   str2=texts.getText();//���滻���ַ���
	   str3=textd.getText();//Ŀ���ַ���
	   int i=str1.indexOf(str2);//��ȡstr1���ı��е�λ��
	   if(i!=-1&&str1!=""&&str2!=""&&str3!="")
	   {
	       str4=str1.substring(0,i)+str3+str1.substring(i+str2.length());
	       textarea.setText(str4);
	       textarea.setCaretPosition(i);
	   }
   }
   void allChange(){
	   str1=textarea.getText();
	   str2=texts.getText();//���滻���ַ���
	   str3=textd.getText();//Ŀ���ַ���
	   str1 = str1.replaceAll(str2, str3);//����ƥ���滻
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
	  String ss = new String();//���²�ѯ��
	  int next = 0;//�Ӻδ���ʼ����
	  int start = 0;//�鵽�ĵ�һ���ַ�����ʼλ��
	  boolean  b = true;//�ж������ϻ������²�ѯ
	  //�вι���
	  public SearchFrame(JTextArea jta){
		  this.setTitle("����");
		  this.jta = jta;
		  
		  jf1.setLayout(new GridLayout(3,1));   
		   
		  JPanel jp1 = new JPanel();
		  JLabel jl1 = new JLabel("��������");
		       
		  jp1.add(jl1);
		  jp1.add(jtf);
		  jf1.add(jp1);
		   
		  JPanel jp2 = new JPanel();
		   
		  JRadioButton jr1 = new JRadioButton("����(U)");
		  jr1.setMnemonic(KeyEvent.VK_B);
		  jr1.setActionCommand("����(U)");
		  jr1.setSelected(true);
		  jr1.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e) {
		          b = true;           
		      }      
		  });     
		   
		  JRadioButton jr2 = new JRadioButton("����(D)");
		  jr2.setMnemonic(KeyEvent.VK_B);
		  jr2.setActionCommand("����(D)");
		  jr2.addActionListener(new ActionListener(){
		   
		      public void actionPerformed(ActionEvent e) {
		          b = false;           
		      }      
		  });     
		  JLabel jl2 = new JLabel("����");
		  jp2.add(jl2);
		  bg.add(jr1);
		  bg.add(jr2);
		  jp2.add(jr1);
		  jp2.add(jr2);
		  jf1.add(jp2);
		
		  JPanel jp3 = new JPanel();
		  JButton jb1 = new JButton("������һ��");
		  jb1.addActionListener(this);
		  JButton jb2 = new JButton("ȡ��");
		  jb2.addActionListener(this);
		  jp3.add(jb1);
		  jp3.add(jb2);
		  jf1.add(jp3);
		   
		  jf1.setTitle("����");
		  jf1.setSize(250,150);
		  jf1.setLocation(400,300);//���ô���λ��
		  jf1.setVisible(true);//�����ܷź�
		  jf1.setResizable(false);//���ڴ�С�̶�
	  }
	
	  public void actionPerformed(ActionEvent e) {
	      String comm = e.getActionCommand();
	      String text = jta.getText();
	      String jtff = jtf.getText();
	      int length = jtff.length();
	      if("������һ��".equals(comm)){   
		  if(jtff.equals("")){
		      JOptionPane.showMessageDialog(null, "���������ݣ�", "", JOptionPane.ERROR_MESSAGE);
		  }else{
			  if(b){
			      //���ϲ�ѯ       
			      if(text.indexOf(jtff,next)!=-1){//����ָ���ַ��ڴ��ַ����е�һ�γ��ִ�������
	                 start = text.indexOf(jtff,next);
	                 jta.setSelectionStart(start);
	                 jta.setSelectionEnd(start+length);
	                 next = start+length;                          
		          }else{
		           JOptionPane.showMessageDialog(null, "very sorry!we cann't find it!", "", JOptionPane.ERROR_MESSAGE);   
		          }               
			 }else{//���²�ѯ
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
	    }else if("ȡ��".equals(comm)){
	        jf1.setVisible(false);
	    }
	 }   
}

/*����Ի���*/
@SuppressWarnings("serial")
class CompileWindow extends JDialog{
	JTextArea showError;
	
	@SuppressWarnings("static-access")
	CompileWindow(){
		setTitle("�������жԻ���");
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
			if(bn) showError.append("����ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}

/*���жԻ���*/
@SuppressWarnings("serial")
class runWindows extends JDialog{
	JTextArea showError;
	
	@SuppressWarnings("static-access")
	runWindows(){
		setTitle("�������жԻ���");
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
				if(bn) showError.append("δʹ��out��");
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	
}


