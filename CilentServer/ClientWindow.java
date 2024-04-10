package experimentCode.CilentServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ClientWindow {
    private final int port;
    private String ip;
    public ClientWindow(String ip, int port) throws Exception{
        this.ip = ip;
        this.port = port;
        JFrame clientFrame = new JFrame("客户端");
        clientFrame.setBounds(200,200,800,600);

        // 创建聊天框
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false); // 设置为不可编辑
        JScrollPane scrollPane = new JScrollPane(chatArea); // 滚动面板
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); //始终显示垂直滚动条
        clientFrame.add(scrollPane, BorderLayout.CENTER);

        // 创建输入框和发送按钮
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("发送");

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        clientFrame.add(inputPanel, BorderLayout.SOUTH);

        // 设置显示的字体
        chatArea.setFont(new Font("宋体", Font.BOLD, 32));

        // 创建与服务器的连接
        Socket clientSocket = new Socket(ip, port);

        // 点击发送
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText().trim(); //从输入框获取信息
                if (!message.isEmpty()) {
                    try {

                        // 获取输出流
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        // 发送消息到服务器: 即把信息写入输出流
                        outToServer.writeBytes(message + '\n');
                        // 获取来自服务器的消息
                        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String getSentenceFromSever = inFromServer.readLine();
                        // 在聊天框中显示发送的消息
                        chatArea.append("From Client: " + message + "\n");
                        // 清空输入框
                        messageField.setText("");
                        // 显示来自服务端的消息
                        chatArea.append("From Sever: " + getSentenceFromSever + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        clientFrame.setVisible(true);
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
