package com.sid.bridgeandroid;

import org.multichat.CommonSoundClass;
import org.multichat.Playback;
import org.multichat.MultiChatConstants;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.net.*;

/**
 * 
 * @author Siddharth
 * The University Of Texas at Dallas
 *
 */
public class ChatClient extends JFrame implements Runnable, ActionListener{
    private ClientShared clientShared;

    private Socket socket;
    private String connectAddr = "192.168.0.10";
    private int connectPort = 6035;
    private InputStream in;
    private OutputStream out;

    // voice chat dava variables
    public CommonSoundClass cs;
    Recorder r;
    Playback playback;

    boolean imRunning = true;

    final int offset = 1;//1;
    int peacespersecond = 8; // number of peaces per second
    boolean recording = false;

    // compression variables
    int compression = 0; // level of compression
    int cmpressto = ClientShared.bytesize / 2; // sets the compression to half of the byte size
    int leftover = 0; // how much is left over to spair out of cmpressto value
    boolean useleftover = true;
    int MaximumCompression = 9; //maximum amout of compression we want

    byte reduceBy = 1;  // reduces the sample rate by N used with reduceSamplerate
    byte increaseBy = 1;// increases the sample rate by N retrieveSampleRate


    //min amd max of transmission used for stats
    int minval = ClientShared.bytesize * peacespersecond;
    int maxval = 0;
    int recievedMinval = ClientShared.bytesize * peacespersecond;
    int recievedMaxval = 0;

//    int previousNumber = 0;
//    int alternatenumber = 9999;
    int packetnumber = 9999;

    double multiplyer = 3.0;

    //hands free chat variables
    int spikesensitivity = 70; // how many noise spikes have to happen for it to think something was said
    boolean HandsFree = false;

    // text chat varibles
    private boolean nickEntered = false;
    private String NickName = ""; // your own nick name
    private Vector NickNameVector = new Vector(); //list of nick names in room
    private String NicktoSend = ""; // private message in text chat.

    // debugging variables
    public boolean debug = false; // standard debugging boolean variable

    // stats variables
    int currentsize = 0; // sent size for this second in bytes
    int avgsize = 0; // everadge sent size
    int avgcounter = 0; // counter varible for send ++

    int recievecounter = 0; // counter variable for recieve ++
    int currentsecondSound = 0; // how much data i recieved this second in bytes

    //key down recording variables
    protected javax.swing.Timer SplashTimer = new javax.swing.Timer( 500, this );
    boolean keypressed = false; // monitors the ctrl key
    boolean timercheck = false; // check if key is pressed in the timer
    boolean connected = false;
    boolean canrecord = false;

    //admin and users variables
    boolean IAmMute = false;
    boolean IAmAdmin = false; // not implemented on the client side

    // threads variables
    ProcessRecordedSoundThread mt = null;
    int size = 0;

    byte[] breaker = MultiChatConstants.BREAKER.getBytes();

    // Variables declaration - do not modify
   /* private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnExit;
    private javax.swing.JPanel btnPan;
    private javax.swing.JButton btnTalk;
    private javax.swing.JCheckBox chkHandsFree;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JList lstNick;
    private javax.swing.JMenuItem mnuMute;
    private javax.swing.JPopupMenu mnuPopup;
    private javax.swing.JMenuItem mnuUnmute;
    private javax.swing.JPanel nickPanHolder;
    private javax.swing.JTextField txtInput;
    private javax.swing.JTextArea txtOutput;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtPortAdr;
    */

    // TODO: Should reuse content from both constructors since largely the same
    public ChatClient(String connectIPAddr) {
        connectAddr = connectIPAddr;

        prepareUI();
    }

    /**
     *creates the org.multichat.client.ChatClient
     */
    private void prepareUI() {
        initComponents();
        super.setTitle( "Bridge Client " );

        txtInput.setEnabled(false);
        btnTalk.setEnabled(false);
        setButtonTalkColor( btnExit.getBackground() );

        txtOutput.setText( "Enter an ip adress and click connect\n");
        txtInput.requestFocus();

        startRecording();
        stopRecording();

        this.show();
    }

    /** Creates new form JFrame */
    public ChatClient() {
        prepareUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        /**
    	*Initialize all the components
    	*/
        mnuPopup = new JPopupMenu();
        mnuMute = new JMenuItem();
        mnuUnmute = new JMenuItem();
        jPanel3 = new JPanel();
        jPanel2 = new JPanel();
        btnExit = new JButton();
        txtPort = new JTextField();
        txtPortAdr = new JTextField();
        btnConnect = new JButton();
        jScrollPane1 = new JScrollPane();
        txtOutput = new JTextArea();
        nickPanHolder = new JPanel();
        jScrollPane2 = new JScrollPane();
        lstNick = new JList();
        jPanel1 = new JPanel();
        btnPan = new JPanel();
        btnTalk = new JButton();
        chkHandsFree = new JCheckBox();
        txtInput = new JTextField();
        lblStatus = new JLabel();

        mnuMute.setText("Mute");
        mnuMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMuteActionPerformed(evt);
            }
        });

        mnuPopup.add(mnuMute);

        mnuUnmute.setText("Unmute");
        mnuUnmute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnmuteActionPerformed(evt);
            }
        });

        mnuPopup.add(mnuUnmute);

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel2.setMinimumSize(new java.awt.Dimension(10, 35));
        jPanel2.setPreferredSize(new java.awt.Dimension(10, 35));
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        jPanel2.add(btnExit);

        txtPort.setColumns(18);
        txtPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortActionPerformed(evt);
            }
        });

        txtPort.setText(connectAddr);
        txtPortAdr.setText("6035");

        txtPortAdr.setColumns(5);
        txtPortAdr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortAdrActionPerformed(evt);
            }
        });

        jPanel2.add(txtPort);
        jPanel2.add(txtPortAdr);


        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jPanel2.add(btnConnect);

        jPanel3.add(jPanel2, java.awt.BorderLayout.NORTH);

        jScrollPane1.setAutoscrolls(true);
        txtOutput.setEditable(false);
        txtOutput.setFont(new java.awt.Font("Dialog", 0, 18));
        txtOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtOutputKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOutputKeyReleased(evt);
            }
        });

        jScrollPane1.setViewportView(txtOutput);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        nickPanHolder.setLayout(new java.awt.BorderLayout());

        nickPanHolder.setMinimumSize(new java.awt.Dimension(100, 100));
        nickPanHolder.setPreferredSize(new java.awt.Dimension(100, 100));
        lstNick.setFont(new java.awt.Font("Dialog", 1, 12));
        lstNick.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstNickKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstNickKeyReleased(evt);
            }
        });
        lstNick.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lstNickMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstNickMouseReleased(evt);
            }
        });

        jScrollPane2.setViewportView(lstNick);

        nickPanHolder.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel3.add(nickPanHolder, java.awt.BorderLayout.EAST);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        jPanel1.setMinimumSize(new java.awt.Dimension(4, 80));
        jPanel1.setPreferredSize(new java.awt.Dimension(63, 80));
        btnPan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnTalk.setText("Talk");
        btnTalk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTalkActionPerformed(evt);
            }
        });
        btnTalk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnTalkKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnTalkKeyReleased(evt);
            }
        });

        btnPan.add(btnTalk);

        chkHandsFree.setSelected(false);
        chkHandsFree.setText("Hands Free");
        chkHandsFree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHandsFreeActionPerformed(evt);
            }
        });
        chkHandsFree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chkHandsFreeKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                chkHandsFreeKeyReleased(evt);
            }
        });

        btnPan.add(chkHandsFree);

        jPanel1.add(btnPan);

        txtInput.setFont(new java.awt.Font("Dialog", 0, 18));
        txtInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInputActionPerformed(evt);
            }
        });
        txtInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtInputKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtInputKeyReleased(evt);
            }
        });

        jPanel1.add(txtInput);

        jPanel3.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        lblStatus.setText("Status:");
        getContentPane().add(lblStatus, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-442)/2, (screenSize.height-316)/2, 442, 316);
    }

    private void lstNickMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstNickMouseReleased
        // TODO add your handling code here:
        if( evt.isPopupTrigger() && connected ){
            mnuPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void mnuUnmuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnmuteActionPerformed
        // TODO add your handling code here:
        try{
            if(! getSelectedUsersName().equals("") ){
                out.write( ("UNMUTE" + getSelectedUsersName() ).getBytes() );
                out.flush();
                out.write( breaker );
           		out.flush();
            }
        }catch(  java.net.UnknownHostException uhkx  ){
            System.out.println("unknown host");
        }catch(  java.io.IOException iox  ){
            txtOutput.append( "\nNot Connected to the server." );
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        }
    }

    private void mnuMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMuteActionPerformed
        // TODO add your handling code here:
        try{
            out.write( ("MUTE" + getSelectedUsersName() ).getBytes() );
            out.flush();
            out.write( breaker );
            out.flush();
        }catch(  java.net.UnknownHostException uhkx  ){
            System.out.println("unknown host");
        }catch(  java.io.IOException iox  ){
            txtOutput.append( "\nNot Connected to the server." );
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        }
    }

    private void lstNickKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstNickKeyReleased
        // TODO add your handling code here:
        KeyWasReleased( evt);
    }

    private void txtOutputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputKeyReleased
        // TODO add your handling code here:
        KeyWasReleased( evt);
    }

    private void chkHandsFreeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chkHandsFreeKeyReleased
        // TODO add your handling code here:
        KeyWasReleased( evt);
    }

    private void btnTalkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnTalkKeyReleased
        // TODO add your handling code here:
        KeyWasReleased( evt);
    }

    private void txtInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInputKeyReleased
        // TODO add your handling code here:
        KeyWasReleased( evt);
    }

    private void chkHandsFreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chkHandsFreeKeyPressed
        // TODO add your handling code here:
        KeyWasPressed( evt.getKeyCode() );
    }

    private void lstNickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstNickKeyPressed
        // TODO add your handling code here:
        KeyWasPressed( evt.getKeyCode() );
    }

    private void txtOutputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputKeyPressed
        // TODO add your handling code here:
        KeyWasPressed( evt.getKeyCode() );
    }

    private void btnTalkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnTalkKeyPressed
        // TODO add your handling code here:
        KeyWasPressed( evt.getKeyCode() );
    }

    private void txtInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInputKeyPressed
        // TODO add your handling code here:
        KeyWasPressed( evt.getKeyCode() );
    }

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        // TODO add your handling code here:
    }

    private void chkHandsFreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHandsFreeActionPerformed
        // TODO add your handling code here:
        HandsFree = chkHandsFree.isSelected();
        if( !HandsFree && recording ){
            setButtonTalkColor( Color.red );
        }
    }

    /**
    *When Talk Button is pressed
    *toggles the talk, dont talk
    */
    private void btnTalkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTalkActionPerformed
        // TODO add your handling code here:
        if( recording ){
            stopRecording();
            btnTalk.setText("Talk");
            setButtonTalkColor( btnExit.getBackground() );
        }else{
            if( canrecord && IAmMute != true ){
                startRecording();
                btnTalk.setText("Stop Talk");
                setButtonTalkColor( Color.red );
            }else{
                recording = !recording;
            }
        }
        recording = !recording;
    }

   private void txtPortActionPerformed(java.awt.event.ActionEvent evt) {
       // TODO add your handling code here:
       btnConnectActionPerformed( evt );
   }

   private void txtPortAdrActionPerformed(java.awt.event.ActionEvent evt) {
   		btnConnectActionPerformed( evt );
   }

   private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
       // Add your handling code here:

		/**
		*Exits org.multichat.client.ChatClient
		*Stops the recodrer from running
		*And calls its the recorders onExit() function to unload its thread
		*/

		if( r != null ){
			r.onExit();
		}
		imRunning = false;
		System.exit(0);
   }

   private void lstNickMousePressed(java.awt.event.MouseEvent evt) {
       // Add your handling code here:
       // used to private messaging
       NicktoSend = new String(lstNick.getSelectedValue() + "");
       this.NicktoSend = NicktoSend;
   }

    /**
     * starts recording
     * careate a recorder class
     * calls the org.multichat.client.Recorder clas  startRecording() if its already running
     */
    private void startRecording(){
       if(cs == null || r == null) {
           if( cs == null){
               cs = new CommonSoundClass();
           }
           r = new Recorder( cs);
       } else {
           r.startRecording();
       }

       if( mt == null ){
           mt = new ProcessRecordedSoundThread();
           mt.start();
       }
   }

    /**
     *stops recording by
     *calling the recorders stopRecording();
     */
    private void stopRecording(){
       packetnumber = 0; // resets the packet number for tracking.

       if( r != null){
           r.stopRecording();
           //r = null;
       }
   }

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
       // Add your handling code here:

        /**
    	*Tryes to connect to the server
    	*Enables the text fields
    	*/
       if (! txtPort.getText().equals("")){
           try{
               connectAddr = txtPort.getText();
               try{
               connectPort = Integer.parseInt(txtPortAdr.getText() );
               }catch( Exception Exp ){
               		connectPort = 6035;
               }
               System.out.println("Connecting to " + connectAddr + ":" + connectPort);
               socket = new Socket( connectAddr, connectPort );
               System.out.println("Connected.");
               in = socket.getInputStream();
               out = socket.getOutputStream();
               out.flush();

               Thread t = new Thread( this, "socket listener" );
               t.start();

               connected = true;

               btnConnect.setEnabled(false);
               txtPort.setEditable(false);
               txtPortAdr.setEditable(false);
               txtInput.setEnabled(true);
               btnTalk.setEnabled(true);
               txtOutput.setText( "Enter a user name and press enter\n");

           }catch(  java.net.UnknownHostException uhkx  ){
               System.out.println("unknown host");
           }catch(  java.io.IOException iox  ){
               txtOutput.append( "\nUnable To Connect to the Server." );
               txtOutput.moveCaretPosition(txtOutput.getText().length() );
           }
       }
   }//GEN-LAST:event_btnConnectActionPerformed

    private void txtInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInputActionPerformed
        // Add your handling code here:

        /**
    	*Sends what ever you typed in the input field to the server
    	*Send the username to the server
    	*/

        if( txtInput.getText().equals("/help") ){
            txtOutput.append( "\nAvailable commands\n/setmax - maximum compression\n/compressto - size to compress it down to\n/leftover - use the leftover bytes\n/sensitivity - hands free sensitivity \n/amplify - increase volume upto 3x\n/stats - shows current settings\n/reset - resets the min and max sent and recieved values" );
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );

        } else if( txtInput.getText().equals("/stats") ){
            txtOutput.append( "\ncompressto = " + getCompressto() + "\nsetmax = " + MaximumCompression + "\nsensitivity = " + spikesensitivity  );
            txtOutput.append( "\namplify = " + multiplyer + "\nleftover = " + useleftover + "\nreduceby = " + reduceBy + "\nincreaseby = " + increaseBy );
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        } else if( txtInput.getText().length()  >= 9 &&  txtInput.getText().substring(0,9).equals("/reduceby") ){

            try{
                int i = Integer.parseInt( txtInput.getText().substring(9).trim() );
                if( i >= 0 ){
                    reduceBy = (byte)i;
                }
            }catch(Exception exp){}

            txtOutput.append( "\nSample rate is now reduced by: " + reduceBy );
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        } else if( txtInput.getText().length()  >= 11 &&  txtInput.getText().substring(0,11).equals("/increaseby") ){
            try{
                int i = Integer.parseInt( txtInput.getText().substring(11).trim() );
                if( i >= 0 ){
                    increaseBy = (byte)i;
                }
            }catch(Exception exp){}

            txtOutput.append( "\nSample rate is now increased by: " + increaseBy);
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );

        } else if( txtInput.getText().length()  >= 8 &&  txtInput.getText().substring(0,8).equals("/amplify") ){
            try{
                double i = Double.parseDouble( txtInput.getText().substring(8).trim() );
                if( i > 0.1 && i <= 3.0 ){
                    multiplyer = i;
                }
            }catch(Exception exp){}

            txtOutput.append( "\nAmplification is now: " + multiplyer);
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );

        } else if( txtInput.getText().length()  >= 12 &&  txtInput.getText().substring(0,12).equals("/sensitivity") ){
            try{
                int i = Integer.parseInt( txtInput.getText().substring(12).trim() );
                if( i >= 0 ){
                    spikesensitivity = i;
                }
            }catch(Exception exp){}

            txtOutput.append( "\nSensitivity is now: " + spikesensitivity);
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );

        } else if( txtInput.getText().equals("/reset") ){
            minval = ClientShared.bytesize * peacespersecond;
            maxval = 0;

            recievedMinval = ClientShared.bytesize * peacespersecond;
            recievedMaxval = 0;

            txtOutput.append( "\n" + "stats reset" );
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        } else if( txtInput.getText().equals("/leftover") ){
            useleftover = !useleftover;
            if( useleftover ){
                txtOutput.append( "\n" + "leftover on" );
            }else{
                txtOutput.append( "\n" + "leftover off" );
            }
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        } else if( txtInput.getText().length()  >= 7 &&  txtInput.getText().substring(0,7).equals("/setmax") ){
            try{
                int i = Integer.parseInt( txtInput.getText().substring(7).trim() );
                if( i >= 0 ){
                    MaximumCompression = i;
                }
            }catch(Exception exp){}

            txtOutput.append( "\nMaximum compression: " + MaximumCompression);
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        } else if( txtInput.getText().length()  >= 11 &&  txtInput.getText().substring(0,11 ).equals("/compressto") ){
            try{
                int i = Integer.parseInt( txtInput.getText().substring(11).trim() );
                if( i > 0 ){
                    setCompressto( i );
                }
            }catch(Exception exp){}

            txtOutput.append( "\nWill try to compress down to: " + getCompressto() );
            txtInput.setText("");
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        }

        if( nickEntered == false && !txtInput.getText().equals("")){
            NickName = txtInput.getText();
            nickEntered = true;
            NickNameVector.addElement( (Object)NickName );
            lstNick.setListData( NickNameVector );
            try{
                out.write( ("NN" + NickName + MultiChatConstants.BREAKER).getBytes() );
                out.flush();
                canrecord = true; // cant record sound until you log in with a name.
            }catch(  java.net.UnknownHostException uhkx  ){
                System.out.println("unknown host");
            }catch(  java.io.IOException iox  ){
                txtOutput.append( "\nNot Connected to the server." );
                txtOutput.moveCaretPosition(txtOutput.getText().length() );
            }
        } else if( !txtInput.getText().equals("")){
            try{
                out.write( ("TXT" + NickName + ": "  + txtInput.getText() + MultiChatConstants.BREAKER).getBytes() );
                out.flush();
            }catch(  java.net.UnknownHostException uhkx  ){
                System.out.println("unknown host");
            }catch(  java.io.IOException iox  ){
                txtOutput.append( "\nNot Connected to the server." );
                txtOutput.moveCaretPosition(txtOutput.getText().length() );
            }
        }

        txtInput.setText("");
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm

	/**
	*Exits org.multichat.client.ChatClient
	*Stops the recodrer from running
	*And calls its the recorders onExit() function to unload its thread
	*/
        if( r != null ){
            r.onExit();
        }
        imRunning = false;
        System.exit(0);
    }//GEN-LAST:event_exitForm

    public static void main( String args[] ) {
        System.out.println("Bridge Client started");
        ChatClient app;
        System.out.println(args.length);
        if (args.length == 0) {
            app = new ChatClient();
        } else {
            System.out.println("Default IP address specified: " + args[0]);
            app = new ChatClient(args[0]);
        }
    }
    public void windowOpened(java.awt.event.WindowEvent windowEvent) {
    }

    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
    }

    public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
    }

    /**
    *Exits org.multichat.client.ChatClient
    *Stops the recodrer from running
    *And calls its the recorders onExit() function to unload its thread
    */
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        if( r != null ){
            r.onExit();
        }
        imRunning = false;
        System.exit( 0 );
    }
    public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
    }
    public void windowActivated(java.awt.event.WindowEvent windowEvent) {
    }
    public void windowIconified(java.awt.event.WindowEvent windowEvent) {
    }

    public class vectorandsize implements Serializable{
    	public byte[] b;
    	public int size;
    	public vectorandsize( byte[] b, int size ){
    		this.b = b;
    		this.size = size;
    	}
    }

    /**
     * common sound class queue for recieved data, so the in.read would not freeze up during calculations.
     */
    Vector recievedByteVector  = new Vector();

    public class ClientMessageHandlerThread extends Thread {
        synchronized public void run() {
            try {
                while ( true ) {
                    if (recievedByteVector.size() > 0) { // Data available to process
                        vectorandsize vs = (vectorandsize) recievedByteVector.remove(0);
                        byte[] bytepassedObj = vs.b;
                        int sizeread = vs.size;

                        // Command or text message determination
                        // Wouldn't this run into problems with large text messages?
                        String passedObj = "";
                        if (sizeread < 100 && sizeread >= 2) {
                            passedObj = new String(bytepassedObj, 0, sizeread);
                        }

                        // Text message
                        if (sizeread < 100 && sizeread > 3 && passedObj.substring(0, 3).equals("TXT")) {
                            // TODO: Should probably just clear the oldest message line
                            if (txtOutput.getText().length() > MultiChatConstants.maxTxtConvoSize) { // clear the text if its more then 1024
                                txtOutput.setText("");
                            }
                            txtOutput.append("\n" + passedObj.substring(3));
                            txtOutput.moveCaretPosition(txtOutput.getText().length());
                        // Remove nickname
                        } else if (sizeread < 100 && passedObj.length() >= 2 && passedObj.substring(0, 2).equals("NC")) {
                                for (int i = 0; i < NickNameVector.size(); i++) {
                                    if ((NickNameVector.elementAt(i) + "").equals(passedObj.substring(2))) {
                                        NickNameVector.removeElementAt(i);
                                        break;
                                    }
                                }
                                lstNick.setListData(NickNameVector);
                        // Logoff
                        } else if (sizeread < 10 && passedObj.length() >= 3 && passedObj.substring(0, 3).equals("bye")) {
                            imRunning = false;
                        // Add nickname
                        } else if (sizeread < 100 && passedObj.length() >= 2 && passedObj.substring(0, 2).equals("NN")) {
                            NickNameVector.addElement(passedObj.substring(2));
                            lstNick.setListData(NickNameVector);
                        // Not Taking
                        } else if (sizeread < 100 && passedObj.length() >= 2 && passedObj.substring(0, 2).equals("NT")) {
                            if (NickName != "") {
                                canrecord = true;
                                lstNick.clearSelection();
//
//                                alternatenumber = 9999;
//                                previousNumber = 9999;
                            }
                       // more then two people in chat someone talking
                        } else if (sizeread < 100 && passedObj.length() >= 2 && passedObj.substring(0, 2).equals("ST")) {
                            if (!passedObj.substring(2).equals(NickName)) { // the the person that is talking is not you
                                SomeoneIsTalking();
                            }
                            for (int i = 0; i < NickNameVector.size(); i++) {
                                if ((NickNameVector.elementAt(i) + "").equals(passedObj.substring(2))) {
                                    lstNick.setSelectedIndex(i);
                                    break;
                                }
                            }
                        // admin mutes you
                        } else if (sizeread < 100 && passedObj.length() >= 4 && passedObj.substring(0, 4).equals("MUTE")) {
                            SomeoneIsTalking();
                            IAmMute = true;
                        // admin unmutes you
                        } else  if (sizeread < 100 && passedObj.length() >= 6 && passedObj.substring(0, 6).equals("UNMUTE")) {
                            IAmMute = false;
                            canrecord = true;
                        // one on one chat someone is talking
                        } else if (sizeread < 100 && passedObj.length() >= 2 && passedObj.substring(0, 2).equals("PT")) {
                            for (int i = 0; i < NickNameVector.size(); i++) {
                                if ((NickNameVector.elementAt(i) + "").equals(passedObj.substring(2))) {
                                    lstNick.setSelectedIndex(i);
                                    break;
                                }
                            }
                        // Received voice data
                        } else {
//                            boolean playThisPacket = true;
//                            int packetNumber = bytepassedObj[sizeread - 1];
                            //System.out.print( "\n" + sizeread );

                            if (sizeread >= 1) {
                                increaseBy = bytepassedObj[sizeread - 1];
                                byte[] newbyte = new byte[sizeread - 1];
                                for (int i = 0; i < sizeread - 1; i++) {
                                    newbyte[i] = bytepassedObj[i];
                                }
                                bytepassedObj = newbyte;
                            }

                            // Decoding data happens here
                            bytepassedObj = split(bytepassedObj);
                            bytepassedObj = decompress(bytepassedObj);// decompress the data
                            bytepassedObj = retrieveSampleRate(bytepassedObj, increaseBy);

                            if (playback == null) {
                                playback = new Playback(bytepassedObj);
                            } else {
                                playback.setSound(bytepassedObj);
                            }

                            updateDataStats(sizeread);
                        }
                        //out.write( "PR".getBytes() );
                        //out.flush();
                    } else {
                        try {
                            synchronized (this) {
                                wait(50);
                            }
                        } catch (Exception mexp) {
                            mexp.printStackTrace();
                        }
                    }
                }
            } catch (Exception exp) {
            }
        }

        private void updateDataStats(int sizeread) {
            recievecounter++;
            currentsecondSound += sizeread;
            if (recievecounter % peacespersecond == 0) {
                recievedMinval = Math.min(currentsecondSound, recievedMinval);
                recievedMaxval = Math.max(currentsecondSound, recievedMaxval);

                setStats("recieved: " + currentsecondSound + "| Max: " + recievedMaxval + " | Min: " + recievedMinval);
                recievecounter = 0;
                currentsecondSound = 0;
            }
        }
    }

    /**
     *Listens for the data from the server
     *If you're going to write a program that will use this as the main chat Buddy list instead of a chat window
     *You can pass all these thigns to its child processes.
     */
    public void run() {
        try{
        	ClientMessageHandlerThread il = new ClientMessageHandlerThread();
        	il.start();

            byte[] mybyte = new byte[1024 * 3];

            int j = 0;
            while( imRunning ) {
                byte[] bytepassedObj = new byte[ClientShared.bytesize + offset];
//                System.out.println("Read up to " + (ClientShared.bytesize + offset) + " bytes.");
                int sizeread = in.read( bytepassedObj, 0, ClientShared.bytesize + offset );
                //recievedByteVector.addElement( ((Object)(new vectorandsize( bytepassedObj, sizeread ))) );

				for(int i = 0; i < sizeread; i++){
					mybyte[j] = bytepassedObj[i];

                    if( j == (1024 * 3 - 1 ) ||  (j >= 4 && mybyte[j-4] == breaker[0] && mybyte[j-3] == breaker[1]
                            && mybyte[j-2] == breaker[2] && mybyte[j-1] == breaker[3] && mybyte[j] == breaker[4] ) ){
						if( j-4 != 0 ){
							recievedByteVector.addElement( ((Object)(new vectorandsize( mybyte, j-4 ))) );
						}

						j=-1;
						mybyte = new byte[1024 * 3];
					}
                    j++;
                }
            }
            in.close();
            out.close();
            socket.close();
        }catch ( NullPointerException npx ){
            npx.printStackTrace();
        }catch(IOException ioe) {
            txtOutput.append( "\nLost Connection with the server." );
            txtOutput.moveCaretPosition(txtOutput.getText().length() );
        }catch(Exception exp) {
            exp.printStackTrace();
        }

    }

    /**
     *Update the status bar
     */
    public void setStats( String str){
        lblStatus.setText( str );
    }

    /**
     * Gets the data from the recording queue and sends it to the server
     */
    private class ProcessRecordedSoundThread extends Thread{
        public void run() {
            // Runs while org.multichat.CommonSoundClass is not null
            while ( true ) {
                while ( cs != null ) {
                    byte[] b = (byte[]) cs.readbyte();
//                    System.out.println("Read in " + b.length + " bytes.");

                    if (socket != null) {
                        try {
                            if (b.length < 10) {
                                out.write(b);
                                out.flush();
                                // Clearing out the buffer???
                                while (cs.vec.size() > 0) {
                                    b = (byte[]) cs.readbyte();
                                }
                            } else {
                                /*reduceBy = (byte)packetnumber++;
                                    if( reduceBy > 255 ){
                                        reduceBy = 0;
                                    }*/

                                avgcounter++; // add one to the avgcounter

                                byte[] localbyte = encode(b);

//                                System.out.println("Writing out " + localbyte.length + "bytes.");
                                out.write(localbyte, 0, getDataSize());
                                out.flush();

                                try {
                                    synchronized (this) {
                                        //wait(1);
                                    }
                                } catch (Exception mexp) {
                                    mexp.printStackTrace();
                                }

                                //calculate avg bytes a second
                                avgsize += getDataSize();
                                currentsize += getDataSize();

                                if (avgcounter % peacespersecond == 0) {
                                    minval = Math.min(currentsize, minval);
                                    maxval = Math.max(currentsize, maxval);

                                    setMyTttle(" Client compression " + getCompression() + " transfer rate: "
                                            + currentsize + " avg: " + avgsize / (avgcounter / peacespersecond)
                                            + "| Max: " + maxval + " | Min: " + minval);

                                    currentsize = 0;
                                    if (avgcounter > 500) {
                                        avgcounter = 0;
                                        avgsize = 0;
                                    }
                                }
                            }

                            out.write(breaker);
                            out.flush();

                        } catch (java.net.UnknownHostException uhkx) {
                            System.out.println("unknown host");
                        } catch (java.io.IOException iox) {
                            if (canrecord && IAmMute != true) {
                                SomeoneIsTalking(); // stop talking because your not connected to the server.
                                txtOutput.append("\nLost Connection with the server.");
                                txtOutput.moveCaretPosition(txtOutput.getText().length());
                            }
                        }
                    }
                }
                try {
                    wait(100);
                } catch (InterruptedException ie) {
                }
            }
        }

        private byte[] encode(byte[] b) {
            int localcompression = 0;
            byte[] localbyte = new byte[ClientShared.bytesize]; // create a byte
            do{
                setCompression(localcompression); //sets compression to N
                for( int i = 0; i < localbyte.length && i < b.length; i++ ){ // copy the byte from the b
                    localbyte[i] = b[i];
                }

                setDataSize( ClientShared.bytesize );
                localbyte = reduceSamplerate( localbyte, reduceBy  ); // reduce samples by 2
                localbyte = compress( localbyte ); // compression
                localbyte = merge( localbyte ); // second layer compression
                localcompression++;
            } while ((getDataSize() - leftover) > getCompressto()
                    && getCompression() < MaximumCompression);

            if( debug == true ){
                printbyte(localbyte);
            }

            if( useleftover == false ){
                leftover = 0;
            }

            if( getCompressto() - getDataSize() > 0 ){
                leftover += (getCompressto() - getDataSize()); // sets the left over value which is reset every second
            } else {
                leftover -= (getDataSize() - getCompressto());
                if( leftover < 0){
                    leftover = 0;
                }
            }

            if( avgcounter % peacespersecond == 0 ){
                leftover = 0;
            }

            byte[] b2 = null;
            if (localbyte == null) { // if i'm not talking and using hands free mode
                localbyte = ("NT|".getBytes());
                setDataSize(localbyte.length);
            } else {
                b2 = new byte[getDataSize() + 1];
                for (int i = 0; i < getDataSize(); i++) {
                    b2[i] = localbyte[i];
                }

                b2[getDataSize()] = reduceBy;
                localbyte = b2;
                setDataSize(getDataSize() + 1);
            }
            return localbyte;
        }
    }


    public void setMyTttle( String title ){
        super.setTitle( title );
    }


    int totalcounter = 0;
    public byte[] removeNoise(byte[] b ){ // doesnt really remove noise i use this for hands free mode

        /**
		*Is used for hands free recording mode doesnt remove noise
		*/
        if( HandsFree || keypressed ){
            byte[] returnThis = new byte[b.length];

            int hit = 0;
            int counterhit = 0;

            for( int i = 0; i < b.length; i++){
                if( b[i] <= (byte) 3 && b[i] >= (byte) -3 ){ // proboble noise
                    hit++;
                }else if( b[i] >= (byte)4| b[i] <= (byte)-4){
                    counterhit++;
                }
            }

            if( counterhit > spikesensitivity || keypressed ){
                totalcounter=16;//record for 2 seconds of sound no matter what
                setButtonTalkColor( Color.red );
                btnTalk.setText( "Stop Talk " + counterhit );
                return b;
            }

            if( totalcounter > 0 ){
                totalcounter--;
                setButtonTalkColor( Color.red );
                return b;
            }

            for( int i = 0; i < b.length; i++){
                returnThis[i] = (byte)0;
            }
            returnThis[0] = 'N';
            returnThis[1] = 'T';

            setButtonTalkColor( Color.yellow );
            return null;
        }else{
            //setButtonTalkColor( btnExit.getBackground() );
            return b;
        }
    }

    /**
     * level one ultra low compression
     *Compresses the data by representing a group of
     *repeating numbers as a number followed by how many times it repeats
     */
    public byte[] compress( byte[] b ){
        b = removeNoise( b );

        if( b == null ){
			setDataSize(0);
			return b;
		}

        byte[] returnThis = new byte[b.length];
        int j = 0;
        for( int i = 0; b != null && i < b.length; i++){
            int same = areTheySame(b, i);
            if( (same > 3 && (b[i] >= 10 || ( b[i] > -6 && b[i] < 0 )  ) ) || ( same > 4 ) ){
                returnThis[j] = (byte)59;// keeps track of it by this number
                j++;
                returnThis[j] = b[i];// the actual value
                j++;
                returnThis[j] = (byte)same;// how many are repeating
                i+=(same - 1);
            }else{
                if( b[i] < (byte)59 && b[i] > -59 ){ // gets rid of numbers higher then 59
                    returnThis[j] = b[i];
                }else{
                    if( b[i] > -59 ){
                        returnThis[j] = (byte)((int)b[i] / 2.2);
                    }else{
                        returnThis[j] = (byte)((int)b[i] / 2.2);
                    }
                }
            }
            j++;
        }
        setDataSize( j );
        return returnThis;
    }

    // level one ultra low compression
    /**
     *Checks if the next number is the same as the first one
     *called by the compress function
     */
    private int areTheySame( byte[] b, int start ){
        for(int i=start+1; i < b.length; i++ ){

            if( getCompression() >= 1 && ( b[start] == b[i] + (byte)1 || b[start] == b[i] - (byte)1  ) ){
            }else if( getCompression() >= 2 && (b[start] == ( b[i] + (byte)2 ) || b[start] ==  b[i] - (byte)2 ) ){
            }else if( getCompression() >= 3 && (b[start] == ( b[i] + (byte)3 ) || b[start] ==  b[i] - (byte)3 ) ){
            }else if( getCompression() >= 4 && (b[start] == ( b[i] + (byte)4 ) || b[start] ==  b[i] - (byte)4 ) ){
            }else if( getCompression() >= 5 && (b[start] == ( b[i] + (byte)5 ) || b[start] ==  b[i] - (byte)5 ) ){
            }else if( getCompression() >= 6 && (b[start] == ( b[i] + (byte)6 ) || b[start] ==  b[i] - (byte)6 ) ){
            }else if( getCompression() >= 7 && (b[start] == ( b[i] + (byte)7 ) || b[start] ==  b[i] - (byte)7 ) ){
            }else if( getCompression() >= 8 && (b[start] == ( b[i] + (byte)8 ) || b[start] ==  b[i] - (byte)8 ) ){
            }else if( getCompression() >= 9 && (b[start] == ( b[i] + (byte)9 ) || b[start] ==  b[i] - (byte)9 ) ){
            }else if( b[start] != b[i] ){
                return (i - start );
            }

            if( i - start >= 59 ){
                return (i - start );
            }
        }
        return b.length - start;
    }

    /**
     *Decompresses the data
     */
    public byte[] decompress( byte[] b ){



        byte[] returnThis = new byte[ClientShared.bytesize];
        int j = 0;

        for( int i = 0; i < b.length && j < returnThis.length; i++){
            double myMultiplyer = multiplyer; //local multiplyer

            if( b[i] == 59 && (i+2) < (b.length)  ){

                if( Math.abs(b[i+1]) > 50 && myMultiplyer > 2.5){ // adjust the multiplyer so you can amplify upto 3 times
                    myMultiplyer = 2.1;
                }else if( Math.abs(b[i+1]) > 42 && myMultiplyer > 2.5){ // adjust the multiplyer so you can amplify upto 3 times
                    myMultiplyer = 2.5;
                }

                returnThis[j] = (byte)(b[i+1] * myMultiplyer); // multiplyer sets the amplification
                for( int x = 0; returnThis.length > j+1 && b.length > i+2 && x < b[i+2] -1; x++){
                    j++;
                    returnThis[j] = (byte)(b[i+1] * myMultiplyer); //multiplyer sets the amplification
                }
                i+=2;
            }else{
                if( Math.abs(b[i]) > 50 && myMultiplyer > 2.5){ // adjust the multiplyer so you can amplify upto 3 times
                    myMultiplyer = 2.1;
                }else if( Math.abs(b[i]) > 42 && myMultiplyer > 2.5){ // adjust the multiplyer so you can amplify upto 3 times
                    myMultiplyer = 2.5;
                }
                returnThis[j] = (byte)(b[i] * myMultiplyer); // multiplyer sets the amplification
            }
            j++;
        }
        return returnThis;
    }

  /**
    * Merges numbrs between 0 and 9 and between -6 and -9
    * represents two bytes as one for example
    * (byte)2 and (byte)7 would be (byte)127
    */
    public byte[] merge( byte[] b ){
		if( b == null ){
			setDataSize(0);
			return null;
		}

        byte[] returnThis = new byte[b.length];
        int j = 0;

        for( int i = 0; b != null && i < getDataSize() && j < returnThis.length; i++){
            if( b.length > i+1  ){
                if( b[i] < (byte)10 && b[i+1] < (byte)10 && b[i] >= (byte)0 && b[i+1] >= (byte)0  ){
                    if (b[i] > (byte) 5) {                                                            // 6 - 9
                        returnThis[j] = (byte) (((int) b[i] * 10) + (int) b[i + 1]);
                    } else if (b[i] < (byte) 2) {                                                    //0 - 1
                        returnThis[j] = (byte) (100 + ((int) b[i] * 10) + (int) b[i + 1]);
                    } else if (b[i] == (byte) 2 && b[i + 1] <= 7) {                                        //2 and 0 - 7
                        returnThis[j] = (byte) (100 + ((int) b[i] * 10) + (int) b[i + 1]);
                    } else if (b[i] == (byte) 3) {                                                    //3 -100
                        returnThis[j] = (byte) (-1 * (100 + (int) b[i + 1]));
                    } else if (b[i] == (byte) 4) {                                                    //4 -111
                        returnThis[j] = (byte) (-1 * (110 + (int) b[i + 1]));
                    } else
                    if (b[i] == (byte) 5 && b[i + 1] <= 7) {                                        //5 and 0 - 7 -122
                        returnThis[j] = (byte) (-1 * (120 + (int) b[i + 1]));
                    } else {
                        //if cant compress
                        returnThis[j] = b[i];
                        i--;
                    }
                    i++; //increment the i to skip next record
                }else if( b[i] <= (byte)-6 && b[i+1] <= (byte)0 && b[i] >= (byte)-9 && b[i+1] >= (byte)-9 ){
                    returnThis[j] = (byte)( ((int)b[i]  * 10) + (int)b[i+1]);
                    i++; //increment the i to skip next record
                }else{
                    returnThis[j] = b[i];
                }
            }
            j++;
        }
        setDataSize( j );
        return returnThis;

    }

  /**
    * Splits the compressed numbers
    * Ex: 127 becomes 2 and 7
    */
    public byte[] split( byte[] b ){ // reverses the merge
        byte[] returnThis = new byte[ClientShared.bytesize];
        int j = 0;

        for( int i = 0; i < b.length && j < returnThis.length; i++){
            if( b.length > i+1  ){
                if( (b[i] > (byte)59 || b[i] <= (byte)-100  ) && j+1 < returnThis.length ){
                    if( b[i] > (byte)59 && b[i] < 100 ){// 6 - 9
                        returnThis[j] = (byte)(b[i] / 10);
                        returnThis[++j] = (byte)(b[i] % 10);
                    }else if( b[i] >= (byte)100 && b[i] <= (byte)127 ){	//0 - 1
                        returnThis[j] = (byte)( (b[i] - 100) / 10);
                        returnThis[++j] = (byte)( (b[i] - 100) % 10);
                    }else if( b[i] <= (byte)-100 && b[i] >= (byte)-127 ){//3 - 5 -100
                        returnThis[j] = (byte)( ((b[i]*-1) - 70) / 10);
                        returnThis[++j] = (byte)( ((b[i]*-1) - 70) % 10);
                    }else{												//if cant compress
                        returnThis[j] = b[i];
                    }
                } else if( b[i] <= -60 && b[i] >= (byte)-99 && j+1 < returnThis.length  ){
                    returnThis[j] = (byte)(b[i] / 10);
                    returnThis[++j] = (byte)(b[i] % 10);
                }else{
                    returnThis[j] = b[i];
                }
            }
            j++;
        }
        return returnThis;
    }


    /**
     * Adjust sample rate down to specified amount
     * @param b
     * @param LowerSampleRateBy number of times to reduce sample rate by
     * @return
     */
    public byte[] reduceSamplerate( byte[] b , int LowerSampleRateBy ){
    	if( LowerSampleRateBy <= 1 || LowerSampleRateBy >= 127 ){
    		return b;
    	}

		byte[] returnThis = new byte[ClientShared.bytesize - (ClientShared.bytesize / LowerSampleRateBy )];

        int j = 0;

        if( b == null ){
			setDataSize(0);
			return b;
		}
        for(  int i = 0; j < returnThis.length && i < b.length ; i++ ){
    		returnThis[j] = (byte)(b[i] );
    		if( i % LowerSampleRateBy == 0 ){
    			i++;
    		}
    		j++;
        }

        return returnThis;
    }

    public byte[] retrieveSampleRate( byte[] b, int LowerSampleRateBy ){
    	if( LowerSampleRateBy <= 1 || LowerSampleRateBy >= 127 ){
    		return b;
    	}

    	byte returnThis[] = new byte[ClientShared.bytesize];
    	int j = 0;
		for( int i = 0; i < returnThis.length; i++ ){
			returnThis[i] = (byte)(b[j] );
			if( i % LowerSampleRateBy == 0 ){
				i++;
				if( i < returnThis.length ){
					returnThis[i] = returnThis[i-1];
				}
			}
			j++;
        }
    	return returnThis;
    }


    /**
     *Returns the data size after compression or a merge used for sending only the data and not the succeeding zeros
     */
    public int getDataSize(){
        return this.size;
    }

    /**
     *Sets the data size called form merge and compress functions
     */
    public void setDataSize( int size ){
        this.size = size;
    }

    /**
     *Sets the maxumum compression that the program will try before reaching the desired file size
     *more ocmpression = worse sound quality and a smaller file
     */
    public void setCompressto( int number ){
        cmpressto = number;
    }

    /**
     * Gets the maxumum compression that the program will try before reaching the desired file size
     */
    public int getCompressto() {
        return cmpressto;
    }

    /**
     *Called from inside Mythread's run() when it increments the compression
     */
    public void setCompression( int number){
        compression = number;
    }

    /**
     *Called by Compress's areTheySame() function when it has to deside how much compression to use
     */
    public int getCompression(){
        return compression;
    }

    public String getSelectedUsersName(){
        return NicktoSend;
    }

    public void setButtonTalkColor(Color c ){
        btnTalk.setBackground( c );
    }

    public void KeyWasPressed( int keycode ){

        if( canrecord && IAmMute != true ){
            if( keycode == KeyEvent.VK_CONTROL && keypressed == false && connected == true ){
                startRecording();
                keypressed = true;
                timercheck = true;
                recording = true;
                SplashTimer.start();
            }else if( keycode == KeyEvent.VK_CONTROL && keypressed && timercheck == false ){
                timercheck = true;
            }
        }
    }
    public void KeyWasReleased( java.awt.event.KeyEvent evt ){
    }

    public void printbyte( byte[] b ){
        for( int i = 0; i < b.length; i++ ){
            System.out.print( b[i] + " " );
        }
        System.out.println("\n\n");
        debug = true;
    }

    /**
    * stop talking - Called from inside the rum() method when the server wants you to
    *stop talking or notifies you that its not listening to you or reboadcasting your signal
    */
    public void SomeoneIsTalking(){
        canrecord = false;
        stopRecording();
        keypressed = false;
        SplashTimer.stop();
        btnTalk.setText("Talk");
        setButtonTalkColor( btnExit.getBackground() );
        recording = false;
    }

    /**
     *Called by the SplashTimer every half a second
     *checks if the CTRL button is being pushed down
     *if not it calls the stopRecording() method
     */
    public void actionPerformed(ActionEvent e) { //timers action
        if( timercheck == false){
            stopRecording();
            keypressed = false;
            SplashTimer.stop();
            btnTalk.setText("Talk");
            setButtonTalkColor( btnExit.getBackground() );
            recording = false;
        }
        timercheck = false; // set the timercheck to false afterwards
    }
}
