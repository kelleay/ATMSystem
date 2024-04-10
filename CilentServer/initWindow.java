package experimentCode.CilentServer;

import javax.swing.*;
import java.awt.*;

public class initWindow {
    public static void main(String[] args) {
        JFrame initFrame = new JFrame("连接服务器");
        initFrame.setBounds(400,200,400,300);
        initFrame.setLayout(null);

        // 输入ip
        JLabel ip = new JLabel("ip");
        JTextField inputIp = new JTextField();
        inputIp.setPreferredSize(new Dimension(200, 30));
        initFrame.add(ip);
        ip.setFont(new Font("宋体",Font.BOLD,30));
        ip.setBounds(180,10,60,30);
        initFrame.add(inputIp);
        inputIp.setBounds(100, 50,200,30);

        // 输入port
        JLabel port = new JLabel("port");
        JTextField inputPort = new JTextField();
        inputPort.setPreferredSize(new Dimension(200, 30));
        port.setFont(new Font("宋体", Font.BOLD, 30));
        initFrame.add(port);
        port.setBounds(170,100,80,30);
        initFrame.add(inputPort);
        inputPort.setBounds(100,150,200,30);

        // 连接按钮
        JButton getConnection = new JButton("连接");
        getConnection.setPreferredSize(new Dimension(50,30));
        initFrame.add(getConnection);
        getConnection.setBounds(150,200,100,30);

        // 点击连接
        getConnectionListener GetConnectionListener = new getConnectionListener(inputIp, inputPort,initFrame);
        getConnection.addActionListener(GetConnectionListener);


        initFrame.setVisible(true);
        initFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}