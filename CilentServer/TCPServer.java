package experimentCode.CilentServer;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class TCPServer {
    public static String fileName = "G:\\IntellijIDEA\\IntelliJ IDEA 2023.2.3\\program\\JAVAbase\\basicCode\\src\\experimentCode\\CilentServer\\log.txt";;
    public static String clientSentence;
    public static String toClientSentence;
    public static BufferedReader inFromClient;
    public static DataOutputStream outToClient;
    public static Socket connectionSocket;
    public static String clientId;
    public static String clientpwd;
    public static double balance; //账户余额

    public static String url = "jdbc:mysql://localhost:3306/atmuser";
    public static String user = "root";
    public static String password ="041029ll";
    public static String driverName = "com.mysql.jdbc.Driver";

    public static Connection conn = null;

    public static void main(String argv[]) throws Exception {
        // 实例化driver
        Class clazz = Class.forName(driverName);
        Driver driver = (Driver) clazz.newInstance();
        // 连接数据库
        DriverManager.registerDriver(driver);
        conn = DriverManager.getConnection(url,user,password);


        while(true) {
            ServerSocket welcomeSocket = new ServerSocket(6789);
            connectionSocket = welcomeSocket.accept(); //等待客户端连接
            System.out.println("客户端已连接");
            writeToLog("客户端已连接\n");
            // 搜索用户名
            String query = "SELECT id FROM user";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet idResultSet = preparedStatement.executeQuery(); //执行
            // 用hash table 来存id 用于判断
            Set<String> userIdSet = new HashSet<>();
            while (idResultSet.next()) {
                userIdSet.add(idResultSet.getString("id"));
            }

            boolean ByeCLose = false;
            // 首先等待读取账号
            while (true) {
                // 获取来自客户端的信息
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientSentence = inFromClient.readLine();
                System.out.println(clientSentence);
                writeToLog(clientSentence);

                // 解析客户端消息中的ID
                clientId = clientSentence.replace("HELO ", "");

                if (userIdSet.contains(clientId)) {
                    toClientSentence = "500 AUTH REQUIRE\n";
                    System.out.println(toClientSentence);
                    writeToLog(toClientSentence);
                    // 给客户端发送信息
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(toClientSentence);
                    // 退出输入账号循环
                    break;
                }
                else if(clientSentence.equals("BYE")){
                    ByeCLose = true;
                    toClientSentence = "BYE" + "\n";
                    System.out.println(toClientSentence);
                    writeToLog(toClientSentence);
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(toClientSentence);
                    break;
                }
                else {
                    errorMessage();
                }
            }

            if(ByeCLose == true){
                writeToLog("服务器关闭");
                System.out.println("服务器关闭");
                welcomeSocket.close();
                continue;
            }

            // 从数据库获取密码
            String query1 = "SELECT pwd FROM user where id = " + clientId;
            PreparedStatement preparedStatement1 = conn.prepareStatement(query1);
            ResultSet pwdResultSet = preparedStatement1.executeQuery(); // 执行查询

            if (pwdResultSet.next()) {
                // 从结果集中获取密码并转换为字符串
                clientpwd = pwdResultSet.getString("pwd");
            } else {
                System.out.println("No password found for the given ID");
            }

            // 第二层等待读取密码
            while (true) {
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientSentence = inFromClient.readLine();
                System.out.println(clientSentence);
                writeToLog(clientSentence);

                // 解析客户端消息中的pwd
                String getPwd = clientSentence.replace("PASS ", "");

                if (getPwd.equals(clientpwd)) {
                    toClientSentence = "525 OK!\n";
                    System.out.println(toClientSentence);
                    writeToLog(toClientSentence);
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(toClientSentence);
                    // 退出密码验证循环
                    break;
                }
                else if(clientSentence.equals("BYE")){
                    ByeCLose = true;
                    toClientSentence = "BYE" + "\n";
                    System.out.println(toClientSentence);
                    writeToLog(toClientSentence);
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(toClientSentence);
                    break;
                }
                else {
                    // 密码错误
                    errorMessage();
                }
            }
            if(ByeCLose == true){
                writeToLog("服务器关闭");
                System.out.println("服务器关闭");
                welcomeSocket.close();
                continue;
            }

            // 第三层之后先查余额
            String query2 = "SELECT balance FROM user where id = " + clientId;
            PreparedStatement preparedStatement2 = conn.prepareStatement(query2);
            ResultSet balanceResultSet = preparedStatement2.executeQuery(); // 执行查询

            // 检查是否有匹配的结果
            if (balanceResultSet.next()) {
                balance = balanceResultSet.getDouble("balance");
            } else {
                System.out.println("No balance found for the given ID");
            }

            // 第三层进行查询余额 / 取款操作 / 拜拜操作
            while (true) {
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientSentence = inFromClient.readLine();
                System.out.println(clientSentence);
                writeToLog(clientSentence);
                // 正则表达式来匹配取款指令
                Pattern pattern = Pattern.compile("WDRA \\d+");
                Matcher matcher = pattern.matcher(clientSentence);

                // 查询
                if (clientSentence.equals("BALA")) {
                    Bala();
                }
                // 取款
                else if (matcher.matches()) {
                    Wdra(clientSentence);
                }
                else if (clientSentence.equals("BYE")) {
                    toClientSentence = "BYE" + "\n";
                    System.out.println(toClientSentence);
                    writeToLog(toClientSentence);
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(toClientSentence);
                    break;
                }
                // 错误指令
                else {
                    errorMessage();
                }
            }
            writeToLog("服务器关闭");
            System.out.println("服务器关闭");
            welcomeSocket.close();
        }
    }

    public static void Bala() throws IOException {
        toClientSentence = "AMNT " + balance + "\n";
        System.out.println(toClientSentence);
        writeToLog(toClientSentence);
        // 向客户端发送信息
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        outToClient.writeBytes(toClientSentence);
    }

    public static void Wdra(String clientSentence) throws IOException {
        // 提取取款金额
        String[] parts = clientSentence.split(" ");
        int amount = Integer.parseInt(parts[1]);

        // 余额不足
        if(amount > balance){
            errorMessage();
            return;
        }

        balance -= amount;
        toClientSentence = "525 OK!\n";
        System.out.println(toClientSentence);
        writeToLog(toClientSentence);
        // 向客户端发送信息
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        outToClient.writeBytes(toClientSentence);
    }

    public static void errorMessage() throws IOException {
        toClientSentence = "401 error\n";
        System.out.println(toClientSentence);
        writeToLog(toClientSentence);
        // 向客户端发送信息
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        outToClient.writeBytes(toClientSentence);
    }
    // 获取当前时间
    public static String getCurrentDateTime() {
        Date date = new Date();
        return date.toString();
    }
    public static void writeToLog(String sentence){
        String currentDateTime = getCurrentDateTime();
        try {
            // 创建一个 BufferedWriter 对象，用于向文件中写入内容
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            System.out.println("writer");
            // 将句子和时间写入文件
            writer.write(currentDateTime + " ---- " + sentence);
            writer.newLine(); // 换行
            // 关闭 writer 对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}