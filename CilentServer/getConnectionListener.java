package experimentCode.CilentServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class getConnectionListener implements ActionListener {
    private JTextField ip;
    private JTextField port;
    private JFrame currFrame;

    public getConnectionListener(JTextField ip, JTextField port, JFrame currFrame) {
        this.ip = ip;
        this.port = port;
        this.currFrame = currFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ip = this.ip.getText().trim();
        int port = 0;
        try {
            port = Integer.parseInt(this.port.getText().trim()); //将字符串转为整数
        } catch (NumberFormatException ex) {
            // 处理端口号格式错误
            JOptionPane.showMessageDialog(null, "端口号格式错误, 请输入一个有效的整数!");
            return;
        }

        if (ip.equals("192.168.0.178") && port == 2525) {
            // 输入正确 等待连接进入ATM交互界面
            this.currFrame.dispose(); //关闭initFrame;
            try {
                ClientWindow clientWindow = new ClientWindow(ip, port); //创建新窗口: 客户端
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            // 输入错误，弹出错误提示框
            JOptionPane.showMessageDialog(null, "输入错误, 请重新输入IP和端口号!");
        }
    }
}
