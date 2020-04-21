package gui;

import cashshop.CashItemFactory;
import client.player.Player;
import static configure.worldworld.输出信息到所有群;
import console.MsgServer.QQMsgServer;
import static gui.MySQL.取绑定手机;
import handling.channel.ChannelServer;
import handling.login.handler.AutoRegister;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import launch.Start;
import static launch.Start.ConfigValuesMap;
import static launch.Start.GetConfigValues;
import static launch.Start.服务端维护;
import static launch.Start.杀怪任务计数;
import server.itens.ItemInformationProvider;
import tools.FileLogger;
import static tools.FileLogger.CurrentReadable_Time;
import tools.TimerTools.WorldTimer;

public class zevms extends javax.swing.JFrame {

    public zevms() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("gui/logo/logo.png"));
        setIconImage(icon.getImage());
        setTitle("ZEVMS冒险岛(027)游戏服务端控制台");
        initComponents();
        刷新账号信息(0);
        GetConfigValues();
        刷新();
        //TimerTools.WorldTimer.getInstance().start();
        //MainThread();

    }
    private long 运行时间记时 = 1;

    private void MainThread() {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                运行时间记时++;
                运行时间.setText(secondToTime(运行时间记时));
            }
        }, 1 * 1000);
    }

    private void 刷新() {
        if (ConfigValuesMap.get("账号注册") == 0) {
            账号注册开关.setSelected(true);
        }
        if (ConfigValuesMap.get("密码修改") == 0) {
            账号密码修改.setSelected(true);
        }
        if (ConfigValuesMap.get("账号查询") == 0) {
            账号信息查询.setSelected(true);
        }
        if (ConfigValuesMap.get("发言登陆") == 0) {
            发言登陆开关.setSelected(true);
        }

        if (ConfigValuesMap.get("0级账号通行") == 0) {
            通行1.setSelected(true);
        }

        if (ConfigValuesMap.get("1级账号通行") == 0) {
            通行2.setSelected(true);
        }

        if (ConfigValuesMap.get("2级账号通行") == 0) {
            通行3.setSelected(true);
        }

        /*if (ConfigValuesMap.get("蓝蜗牛大区") == 0) {
            蓝蜗牛开关.setSelected(true);
        }

        if (ConfigValuesMap.get("蘑菇仔大区") == 0) {
            蘑菇仔开关.setSelected(true);
        }*/
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        账号注册开关 = new javax.swing.JCheckBox();
        账号密码修改 = new javax.swing.JCheckBox();
        账号信息查询 = new javax.swing.JCheckBox();
        发言登陆开关 = new javax.swing.JCheckBox();
        通行3 = new javax.swing.JCheckBox();
        通行1 = new javax.swing.JCheckBox();
        通行2 = new javax.swing.JCheckBox();
        关闭付服务端 = new javax.swing.JButton();
        重载游戏商城 = new javax.swing.JButton();
        重载QQ机器人 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        密码标签 = new javax.swing.JLabel();
        账号标签 = new javax.swing.JLabel();
        注册账号按钮 = new javax.swing.JButton();
        QQ = new javax.swing.JLabel();
        账号框 = new javax.swing.JTextField();
        QQ框 = new javax.swing.JTextField();
        密码框 = new javax.swing.JTextField();
        管理员标签 = new javax.swing.JLabel();
        管理员框 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        账号表 = new javax.swing.JTable();
        账号表刷新按钮 = new javax.swing.JButton();
        在线账号 = new javax.swing.JButton();
        账号表刷新按钮2 = new javax.swing.JButton();
        离线账号 = new javax.swing.JButton();
        正常账号 = new javax.swing.JButton();
        封禁账号 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        商店装备按等级排序 = new javax.swing.JButton();
        商店道具自动设置价格 = new javax.swing.JButton();
        清除不存在的爆物物品 = new javax.swing.JButton();
        A1 = new javax.swing.JButton();
        生成CDK = new javax.swing.JButton();
        生成CDK编号 = new javax.swing.JTextField();
        QQ1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        运行时间 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        账号注册开关.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        账号注册开关.setText("账号注册开关");
        账号注册开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                账号注册开关ActionPerformed(evt);
            }
        });
        jPanel3.add(账号注册开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 40));

        账号密码修改.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        账号密码修改.setText("账号密码修改");
        账号密码修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                账号密码修改ActionPerformed(evt);
            }
        });
        jPanel3.add(账号密码修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 40));

        账号信息查询.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        账号信息查询.setText("账号信息查询");
        账号信息查询.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                账号信息查询ActionPerformed(evt);
            }
        });
        jPanel3.add(账号信息查询, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, 40));

        发言登陆开关.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        发言登陆开关.setText("账号发言登陆");
        发言登陆开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                发言登陆开关ActionPerformed(evt);
            }
        });
        jPanel3.add(发言登陆开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 40));

        通行3.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        通行3.setText("2级账号登陆");
        通行3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                通行3ActionPerformed(evt);
            }
        });
        jPanel3.add(通行3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, 40));

        通行1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        通行1.setText("玩家登陆");
        通行1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                通行1ActionPerformed(evt);
            }
        });
        jPanel3.add(通行1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, 40));

        通行2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        通行2.setText("1级账号登陆");
        通行2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                通行2ActionPerformed(evt);
            }
        });
        jPanel3.add(通行2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, 40));

        关闭付服务端.setText("关闭付服务端");
        关闭付服务端.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                关闭付服务端ActionPerformed(evt);
            }
        });
        jPanel3.add(关闭付服务端, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 120, 40));

        重载游戏商城.setText("重载游戏商城");
        重载游戏商城.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载游戏商城ActionPerformed(evt);
            }
        });
        jPanel3.add(重载游戏商城, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 120, 40));

        重载QQ机器人.setText("重载QQ机器人");
        重载QQ机器人.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载QQ机器人ActionPerformed(evt);
            }
        });
        jPanel3.add(重载QQ机器人, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 120, 40));

        jTabbedPane1.addTab("游戏一般设置", jPanel3);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        密码标签.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        密码标签.setText("密码(必填)");
        jPanel1.add(密码标签, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        账号标签.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        账号标签.setText("账号(必填)");
        jPanel1.add(账号标签, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        注册账号按钮.setText("注册账号");
        注册账号按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                注册账号按钮ActionPerformed(evt);
            }
        });
        jPanel1.add(注册账号按钮, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 130, 30));

        QQ.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        QQ.setText("QQ(选填)");
        jPanel1.add(QQ, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));
        jPanel1.add(账号框, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 130, 30));
        jPanel1.add(QQ框, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 130, 30));

        密码框.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                密码框ActionPerformed(evt);
            }
        });
        jPanel1.add(密码框, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 130, 30));

        管理员标签.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        管理员标签.setText("管理员(选填1-6)");
        jPanel1.add(管理员标签, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, -1, -1));

        管理员框.setText("0");
        jPanel1.add(管理员框, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 130, 30));

        账号表.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "账号ID", "账号", "密码", "QQ", "手机", "管理", "点券"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(账号表);
        if (账号表.getColumnModel().getColumnCount() > 0) {
            账号表.getColumnModel().getColumn(0).setResizable(false);
            账号表.getColumnModel().getColumn(1).setResizable(false);
            账号表.getColumnModel().getColumn(2).setResizable(false);
            账号表.getColumnModel().getColumn(3).setResizable(false);
            账号表.getColumnModel().getColumn(4).setResizable(false);
            账号表.getColumnModel().getColumn(4).setHeaderValue("手机");
            账号表.getColumnModel().getColumn(5).setHeaderValue("管理");
            账号表.getColumnModel().getColumn(6).setHeaderValue("点券");
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 750, 460));

        账号表刷新按钮.setText("刷新信息");
        账号表刷新按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                账号表刷新按钮ActionPerformed(evt);
            }
        });
        jPanel1.add(账号表刷新按钮, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 130, 30));

        在线账号.setText("在线");
        在线账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                在线账号ActionPerformed(evt);
            }
        });
        jPanel1.add(在线账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 60, 30));

        账号表刷新按钮2.setText("刷新信息");
        账号表刷新按钮2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                账号表刷新按钮2ActionPerformed(evt);
            }
        });
        jPanel1.add(账号表刷新按钮2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 130, 30));

        离线账号.setText("离线");
        离线账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线账号ActionPerformed(evt);
            }
        });
        jPanel1.add(离线账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 330, 60, 30));

        正常账号.setText("正常");
        正常账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                正常账号ActionPerformed(evt);
            }
        });
        jPanel1.add(正常账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 60, 30));

        封禁账号.setText("封禁");
        封禁账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                封禁账号ActionPerformed(evt);
            }
        });
        jPanel1.add(封禁账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 370, 60, 30));

        jTabbedPane1.addTab("游戏账号", jPanel1);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        商店装备按等级排序.setText("商店装备按等级排序");
        商店装备按等级排序.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商店装备按等级排序ActionPerformed(evt);
            }
        });
        jPanel2.add(商店装备按等级排序, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 190, -1));

        商店道具自动设置价格.setText("商店道具自动设置价格");
        商店道具自动设置价格.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商店道具自动设置价格ActionPerformed(evt);
            }
        });
        jPanel2.add(商店道具自动设置价格, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 190, -1));

        清除不存在的爆物物品.setText("清除不存在的爆物物品");
        清除不存在的爆物物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                清除不存在的爆物物品ActionPerformed(evt);
            }
        });
        jPanel2.add(清除不存在的爆物物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 190, -1));

        A1.setText("A1");
        A1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                A1ActionPerformed(evt);
            }
        });
        jPanel2.add(A1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 190, -1));

        生成CDK.setText("生成CDK");
        生成CDK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成CDKActionPerformed(evt);
            }
        });
        jPanel2.add(生成CDK, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 420, 130, 30));
        jPanel2.add(生成CDK编号, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 70, 30));

        QQ1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        QQ1.setText("礼包编号");
        jPanel2.add(QQ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, -1, -1));

        jTabbedPane1.addTab("*", jPanel2);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jTabbedPane1.addTab("*", jPanel4);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 570));

        运行时间.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        运行时间.setText("时间");
        getContentPane().add(运行时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 575, 330, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void 注册账号按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_注册账号按钮ActionPerformed
        if (账号框.getText().equals("") || 密码框.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "请填写注册的账号密码。");
        } else if (!账号框.getText().matches("^[0-9]{6,11}$")) {
            JOptionPane.showMessageDialog(null, "账号格式不对，必须由6-11位数字或字母组成。");
        } else if (!管理员框.getText().matches("^[0-6]{1,1}$")) {
            JOptionPane.showMessageDialog(null, "管理员等级填入错误，只能填入0-6。");
        } else {
            Connection con;
            String account = 账号框.getText();
            String password = 密码框.getText();
            String QQ = QQ框.getText();
            String GM = 管理员框.getText();
            if (password.length() > 10) {
                JOptionPane.showMessageDialog(null, "注册失败，密码过长。");
                return;
            }
            if (AutoRegister.getAccountExists(account)) {
                JOptionPane.showMessageDialog(null, "注册失败，账号已存在。");
                return;
            }
            try {
                con = Start.getInstance().getConnection();
            } catch (Exception ex) {
                System.out.println("账号注册出错01" + ex);
                return;
            }
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password,qq,gm) VALUES (?,?,?,?)");
                ps.setString(1, account);
                ps.setString(2, password);//hexSha1(password)
                if ("".equals(QQ)) {
                    ps.setString(3, null);
                } else {
                    ps.setString(3, QQ);
                }
                ps.setInt(4, Integer.parseInt(GM));
                ps.executeUpdate();
                ps.close();
                JOptionPane.showMessageDialog(null, "注册成功。\r\n账号: " + account + " \r\n密码: " + password + "");
                账号框.setText(null);
                密码框.setText(null);
                管理员框.setText("0");
                QQ框.setText(null);
                刷新账号信息(1);
            } catch (SQLException ex) {
                System.out.println("账号注册出错02" + ex);
            } finally {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException e) {

                }
            }
        }
    }//GEN-LAST:event_注册账号按钮ActionPerformed

    private void 密码框ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_密码框ActionPerformed

    }//GEN-LAST:event_密码框ActionPerformed

    private void 账号表刷新按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_账号表刷新按钮ActionPerformed
        刷新账号信息(1);
    }//GEN-LAST:event_账号表刷新按钮ActionPerformed

    private void 在线账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_在线账号ActionPerformed
        刷新账号信息(2);
    }//GEN-LAST:event_在线账号ActionPerformed

    private void 账号表刷新按钮2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_账号表刷新按钮2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_账号表刷新按钮2ActionPerformed

    private void 离线账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线账号ActionPerformed
        刷新账号信息(3);
    }//GEN-LAST:event_离线账号ActionPerformed

    private void 正常账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_正常账号ActionPerformed
        刷新账号信息(4);
    }//GEN-LAST:event_正常账号ActionPerformed

    private void 封禁账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_封禁账号ActionPerformed
        刷新账号信息(5);
    }//GEN-LAST:event_封禁账号ActionPerformed

    private void 商店装备按等级排序ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商店装备按等级排序ActionPerformed

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM shopitems WHERE itemid < 2000000");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                try {
                    PreparedStatement psu = con.prepareStatement("UPDATE shopitems SET position = ? WHERE itemid = " + rs.getInt("itemid") + "");
                    if (ii.itemExists(rs.getInt("itemid"))) {
                        psu.setInt(1, ii.getReqLevel(rs.getInt("itemid")));
                    } else {
                        System.err.println("跳过不存在物品 : " + rs.getInt("itemid"));
                    }
                    psu.executeUpdate();
                    psu.close();
                } catch (SQLException ex) {
                    System.err.println("RecordOnlineTime : " + ex.getMessage());
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {

            }
        }

    }//GEN-LAST:event_商店装备按等级排序ActionPerformed

    private void 商店道具自动设置价格ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商店道具自动设置价格ActionPerformed
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM shopitems WHERE itemid > 0 && price =0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                try {
                    PreparedStatement psu = con.prepareStatement("UPDATE shopitems SET price = ? WHERE itemid = " + rs.getInt("itemid") + "");
                    if (ii.itemExists(rs.getInt("itemid"))) {
                        psu.setInt(1, (int) ii.getPrice(rs.getInt("itemid")) * 2);
                    } else {
                        System.err.println("跳过不存在物品 : " + rs.getInt("itemid"));
                    }
                    psu.executeUpdate();
                    psu.close();
                } catch (SQLException ex) {
                    System.err.println("RecordOnlineTime : " + ex.getMessage());
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {

            }
        }
    }//GEN-LAST:event_商店道具自动设置价格ActionPerformed

    private void 清除不存在的爆物物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_清除不存在的爆物物品ActionPerformed
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid > 0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM drop_data WHERE itemid = " + rs.getInt("itemid"))) {
                        ps1.executeUpdate();
                        ps1.close();
                    }
                    System.err.println("不存在物品 : " + rs.getInt("itemid"));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {

            }
        }
    }//GEN-LAST:event_清除不存在的爆物物品ActionPerformed

    private void A1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_A1ActionPerformed
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_mxd ");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try (PreparedStatement ps1 = con.prepareStatement("UPDATE wz_mxd SET job = " + ii.getReqJob(rs.getInt("itemid")) + " WHERE itemid = " + rs.getInt("itemid") + "")) {
                    System.err.println("不存在物品 : " + rs.getInt("itemid") + " " + ii.getReqJob(rs.getInt("itemid")));
                    ps1.executeUpdate();
                    ps1.close();
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {

            }
        }
    }//GEN-LAST:event_A1ActionPerformed

    private void 生成CDKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成CDKActionPerformed

        for (int i = 1; i <= 10; i++) {
            生成礼包();
        }
        JOptionPane.showMessageDialog(null, "10个CDK生成成功，请查看服务端目录。");
    }//GEN-LAST:event_生成CDKActionPerformed

    private void 账号注册开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_账号注册开关ActionPerformed
        按键开关(1);

    }//GEN-LAST:event_账号注册开关ActionPerformed

    private void 账号密码修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_账号密码修改ActionPerformed
        按键开关(2);
    }//GEN-LAST:event_账号密码修改ActionPerformed

    private void 账号信息查询ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_账号信息查询ActionPerformed
        按键开关(3);
    }//GEN-LAST:event_账号信息查询ActionPerformed

    private void 发言登陆开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_发言登陆开关ActionPerformed
        按键开关(4);
    }//GEN-LAST:event_发言登陆开关ActionPerformed

    private void 通行3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_通行3ActionPerformed
        按键开关(12);
    }//GEN-LAST:event_通行3ActionPerformed

    private void 通行1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_通行1ActionPerformed
        按键开关(10);
    }//GEN-LAST:event_通行1ActionPerformed

    private void 通行2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_通行2ActionPerformed
        按键开关(11);
    }//GEN-LAST:event_通行2ActionPerformed

    private void 关闭付服务端ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_关闭付服务端ActionPerformed
        int n = JOptionPane.showConfirmDialog(this, "你需要关闭服务端吗？", "信息", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            if (服务端维护 != true) {
                String txt = "[系统公告]:服务端停机维护。";
                for (World world : Start.getInstance().getWorlds()) {
                    全服喇叭(world.getWorldId(), 1, txt);
                    全服喇叭(world.getWorldId(), 6, txt);
                }
                服务端维护 = true;
                保存杀怪数量();
                for (World world : Start.getInstance().getWorlds()) {
                    存档(world.getWorldId());
                    切断所有玩家(world.getWorldId());
                }
                //输出信息到所有群("服务端关闭成功。");
                JOptionPane.showMessageDialog(null, "服务端关闭成功，请叉掉控制台。");
                /*new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000 * 60 * 1);
                            for (World world : Start.getInstance().getWorlds()) {
                                存档(world.getWorldId());
                                切断所有玩家(world.getWorldId());
                            }
                            System.exit(0);
                        } catch (InterruptedException e) {
                        }
                    }
                }.start();*/
            } else {
                JOptionPane.showMessageDialog(null, "服务端正在关闭。");
            }
        }
    }//GEN-LAST:event_关闭付服务端ActionPerformed

    private void 重载游戏商城ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载游戏商城ActionPerformed
        CashItemFactory.loadCashItemsFromDatabase();
        JOptionPane.showMessageDialog(null, "重载游戏商城完成。");
    }//GEN-LAST:event_重载游戏商城ActionPerformed

    private void 重载QQ机器人ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载QQ机器人ActionPerformed
        new Thread(QQMsgServer.getInstance()).start();
        JOptionPane.showMessageDialog(null, "重载QQ机器人完成。");
    }//GEN-LAST:event_重载QQ机器人ActionPerformed
    public void 保存杀怪数量() {

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO `characters_kmob` (`id`, `mobshuliang`) VALUES (?, ?)");
            for (Map.Entry<Integer, Integer> entry : 杀怪任务计数.entrySet()) {
                ps.setInt(1, entry.getKey());
                ps.setInt(2, entry.getValue());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
        }

    }

    public void 切断所有玩家(int world) {
        Start.getInstance().getWorldById(world).getChannels().forEach((cserv) -> {
            cserv.getPlayerStorage().disconnectAlll();
        });
    }

    public void 存档(int world) {
        for (ChannelServer cserv : Start.getInstance().getWorldById(world).getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                chr.saveDatabase();
            }
        }
    }

    public void 全服喇叭(int world, int a, String b) {
        for (ChannelServer cserv : Start.getInstance().getWorldById(world).getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                chr.dropMessage(a, b);
            }
        }
    }

    public void 生成礼包() {
        if (生成CDK编号.getText() == null) {
            生成CDK编号.setText("1");
        }
        int 礼包 = Integer.parseInt(生成CDK编号.getText());

        String chars = "1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        char 生成1 = chars.charAt((int) (Math.random() * 62));
        char 生成2 = chars.charAt((int) (Math.random() * 62));
        char 生成3 = chars.charAt((int) (Math.random() * 62));
        char 生成4 = chars.charAt((int) (Math.random() * 62));
        char 生成5 = chars.charAt((int) (Math.random() * 62));
        char 生成6 = chars.charAt((int) (Math.random() * 62));
        char 生成7 = chars.charAt((int) (Math.random() * 62));
        char 生成8 = chars.charAt((int) (Math.random() * 62));
        char 生成9 = chars.charAt((int) (Math.random() * 62));
        char 生成10 = chars.charAt((int) (Math.random() * 62));
        char 生成11 = chars.charAt((int) (Math.random() * 62));
        char 生成12 = chars.charAt((int) (Math.random() * 62));
        char 生成13 = chars.charAt((int) (Math.random() * 62));
        char 生成14 = chars.charAt((int) (Math.random() * 62));
        char 生成15 = chars.charAt((int) (Math.random() * 62));
        char 生成16 = chars.charAt((int) (Math.random() * 62));
        char 生成17 = chars.charAt((int) (Math.random() * 62));
        char 生成18 = chars.charAt((int) (Math.random() * 62));
        char 生成19 = chars.charAt((int) (Math.random() * 62));
        char 生成20 = chars.charAt((int) (Math.random() * 62));
        char 生成21 = chars.charAt((int) (Math.random() * 62));
        char 生成22 = chars.charAt((int) (Math.random() * 62));
        char 生成23 = chars.charAt((int) (Math.random() * 62));
        char 生成24 = chars.charAt((int) (Math.random() * 62));
        char 生成25 = chars.charAt((int) (Math.random() * 62));
        char 生成26 = chars.charAt((int) (Math.random() * 62));
        char 生成27 = chars.charAt((int) (Math.random() * 62));
        char 生成28 = chars.charAt((int) (Math.random() * 62));
        char 生成29 = chars.charAt((int) (Math.random() * 62));
        char 生成30 = chars.charAt((int) (Math.random() * 62));

        String 充值卡 = "MX" + 生成1 + "" + 生成2 + "" + 生成3 + "" + 生成4 + "" + 生成5 + "" + 生成6 + "" + 生成7 + "" + 生成8 + "" + 生成9 + "" + 生成10 + "" + 生成11 + "" + 生成12 + "" + 生成13 + "" + 生成14 + "" + 生成15 + "" + 生成16 + "" + 生成17 + "" + 生成18 + "" + 生成19 + "" + 生成20 + "" + 生成21 + "" + 生成22 + "" + 生成23 + "" + 生成24 + "" + 生成25 + "" + 生成26 + "" + 生成27 + "" + 生成28 + "" + 生成29 + "" + 生成30 + "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO nxcodez ( code,leixing) VALUES (?,?)")) {
                ps.setString(1, 充值卡);
                ps.setInt(2, 礼包);
                ps.executeUpdate();
                FileLogger.logToFile("Cdk/[" + CurrentReadable_Time() + "]" + 礼包 + "礼包兑换卡.txt", "" + 充值卡 + "\r\n");
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {

            }
        }
    }

    /*
     1刷新所有账号
     2刷新在线账号
     3刷新离线账号
     4刷新正常账号
     5刷新封禁账号
     */
    private void 刷新账号信息(int a) {
        for (int i = ((DefaultTableModel) (this.账号表.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号表.getModel())).removeRow(i);
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts order by id desc");
            rs = ps.executeQuery();
            while (rs.next()) {
                switch (a) {
                    case 2:
                        if (rs.getInt("loggedin") == 0) {
                            continue;
                        }
                        break;
                    case 3:
                        if (rs.getInt("loggedin") > 0) {
                            continue;
                        }
                        break;
                    case 4:
                        if (rs.getInt("banned") > 0) {
                            continue;
                        }
                        break;
                    case 5:
                        if (rs.getInt("banned") == 0) {
                            continue;
                        }
                        break;
                }

                String QQ = null;
                if (rs.getString("qq") != null) {
                    QQ = rs.getString("qq");
                } else if (rs.getString("qq") == null) {
                    QQ = "未绑定QQ";
                } else {
                    QQ = "未绑定QQ";
                }
                String SJ = 取绑定手机(QQ);
                /*if (rs.getString("sj") != null) {
                    SJ = rs.getString("sj");
                } else if (rs.getString("sj") == null) {
                    SJ = "未绑定手机";
                } else {
                    SJ = "未绑定手机";
                }*/
                ((DefaultTableModel) 账号表.getModel()).insertRow(账号表.getRowCount(), new Object[]{
                    //账号ID
                    rs.getInt("id"),
                    //账号
                    rs.getString("name"),
                    //密码
                    rs.getString("password"),
                    //绑定QQ
                    QQ,
                    //手机
                    SJ,
                    //管理等级
                    rs.getInt("gm"),
                    //点券
                    rs.getInt("cardNX")
                });
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }

    }

    public static int Val(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Val as DATA FROM configvalues WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 按键开关(int a) {
        int 检测开关 = Val(a);
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (检测开关 == 0) {
            Connection con = null;
            try {
                con = Start.getInstance().getConnection();
                ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps.setInt(1, a);
                rs = ps.executeQuery();
                if (rs.next()) {
                    PreparedStatement e = con.prepareStatement("update configvalues set Val = '1' where id = '" + a + "';");
                    e.executeUpdate();
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {

            } finally {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }

                } catch (SQLException e) {
                }
            }
        } else {
            Connection con = null;
            try {
                con = Start.getInstance().getConnection();
                ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps.setInt(1, a);
                rs = ps.executeQuery();
                if (rs.next()) {
                    PreparedStatement e = con.prepareStatement("update configvalues set Val = '0' where id = '" + a + "';");
                    e.executeUpdate();
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {

            } finally {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }

                } catch (SQLException e) {
                }
            }
        }
        GetConfigValues();
        刷新();
    }

    public void secondTest() {
        //秒
        long second = 1509412775l;
        //转换为日时分秒
        String days = secondToTime(second);
        System.out.println(days);
        //转换为所需日期格式
        String dateString = secondToDate(second, "yyyy-MM-dd hh:mm:ss");
        System.out.println(dateString);
    }

    /**
     * 秒转换为指定格式的日期
     *
     * @param second
     * @param patten
     * @return
     */
    private String secondToDate(long second, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * 返回日时分秒
     *
     * @param second
     * @return
     */
    private String secondToTime(long second) {
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days) {
            return "运行时间  " + days + " : " + hours + " : " + minutes + " : " + second;
        } else {
            return "运行时间  " + hours + " : " + minutes + " : " + second;
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(zevms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(zevms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(zevms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(zevms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new zevms().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton A1;
    private javax.swing.JLabel QQ;
    private javax.swing.JLabel QQ1;
    private javax.swing.JTextField QQ框;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton 关闭付服务端;
    private javax.swing.JCheckBox 发言登陆开关;
    private javax.swing.JButton 商店装备按等级排序;
    private javax.swing.JButton 商店道具自动设置价格;
    private javax.swing.JButton 在线账号;
    private javax.swing.JLabel 密码标签;
    private javax.swing.JTextField 密码框;
    private javax.swing.JButton 封禁账号;
    private javax.swing.JButton 正常账号;
    private javax.swing.JButton 注册账号按钮;
    private javax.swing.JButton 清除不存在的爆物物品;
    private javax.swing.JButton 生成CDK;
    private javax.swing.JTextField 生成CDK编号;
    private javax.swing.JButton 离线账号;
    private javax.swing.JLabel 管理员标签;
    private javax.swing.JTextField 管理员框;
    private javax.swing.JCheckBox 账号信息查询;
    private javax.swing.JCheckBox 账号密码修改;
    private javax.swing.JLabel 账号标签;
    private javax.swing.JTextField 账号框;
    private javax.swing.JCheckBox 账号注册开关;
    private javax.swing.JTable 账号表;
    private javax.swing.JButton 账号表刷新按钮;
    private javax.swing.JButton 账号表刷新按钮2;
    private javax.swing.JLabel 运行时间;
    private javax.swing.JCheckBox 通行1;
    private javax.swing.JCheckBox 通行2;
    private javax.swing.JCheckBox 通行3;
    private javax.swing.JButton 重载QQ机器人;
    private javax.swing.JButton 重载游戏商城;
    // End of variables declaration//GEN-END:variables

}
