package com.dnsxo.app;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

/**
 * @author GAOFENG (http://www.dnsxo.com)
 * @date 2020年1月9日 下午11:02:24
 */
public class MainUI extends JFrame implements ActionListener {

    private JButton createBtn;
    //补丁目录
    private JLabel pathLabel;
    private JTextField folderFiled;
    //版本号
    private JLabel versionLabel;
    private JTextField versionNoFiled;
    //云标识
    private JLabel cloudLabel;
    private JTextField cloudFiled;
    //应用标识
    private JLabel appsLabel;
    private JTextField appsFiled;

    private JTextArea info;

    public MainUI() {
        init();
    }

    public void init() {
        initFrame();
    }

    public void initFrame() {
        this.setTitle("金蝶苍穹行业版补丁制作工具");
        this.setSize(200, 200);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FlowLayout fl = new FlowLayout();
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();

        jp1.setLayout(fl);
        jp2.setLayout(fl);
        jp3.setLayout(fl);

        getContentPane().add("North", jp1);
        getContentPane().add("Center", jp2);
        getContentPane().add("South", jp3);


        cloudLabel = new JLabel("云标识");
        jp1.add(cloudLabel);
        cloudFiled = new JTextField(6);
        //设置默认值
        cloudFiled.setText("ec");
        jp1.add(cloudFiled);

        appsLabel = new JLabel("应用标识");
        jp1.add(appsLabel);
        appsFiled = new JTextField(15);
        //设置默认值
        appsFiled.setText("cont,ecbd,ecco,ecma");
        jp1.add(appsFiled);

        versionLabel = new JLabel("版本号");
        jp1.add(versionLabel);
        versionNoFiled = new JTextField(10);
        //设置默认值
        versionNoFiled.setText("2.0.");
        jp1.add(versionNoFiled);

        pathLabel = new JLabel("补丁文件目录");
        jp1.add(pathLabel);
        folderFiled = new JTextField(30);
        jp1.add(folderFiled);

        createBtn = new JButton("开始制作");
        createBtn.addActionListener(this);
        jp1.add(createBtn);

        info = new JTextArea(30, 100);
        JScrollPane sp = new JScrollPane(info);
        jp2.add(sp);

        //版权
        JLabel rightLabel = new JLabel("Mr.靠谱出品 必属精品");
        jp3.add(rightLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cloud = cloudFiled.getText().trim();
        if ("".equals(cloud)) {
            JOptionPane.showMessageDialog(null, "请输入云标识", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String apps = appsFiled.getText().trim();
        if ("".equals(apps)) {
            JOptionPane.showMessageDialog(null, "请输入应用标识", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String versionNo = versionNoFiled.getText().trim();
        if ("".equals(versionNo)) {
            JOptionPane.showMessageDialog(null, "请输入补丁版本号", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String folderPath = folderFiled.getText().trim();
        if ("".equals(folderPath)) {
            JOptionPane.showMessageDialog(null, "请输入补丁文件目录", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //通过云标识、应用标识组合dm、jar文件报名
        List<String> appNameList = new ArrayList<String>();
        List<String> appidList = Arrays.asList(apps.split(","));
        for (String appid : appidList) {
            appNameList.add(cloud + "-" + appid);
        }

        File dmFolder = new File(folderPath + File.separator + ZipFileType.dm.toString());
        File[] dmFiles = dmFolder.listFiles();
        if (dmFiles.length == 0) {
            JOptionPane.showMessageDialog(null, "dm目录下不能为空", "", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File jarFolder = new File(folderPath + File.separator + ZipFileType.jar.toString() + File.separator + "biz");
        File[] jarFiles = jarFolder.listFiles();
        if (jarFiles.length == 0) {
            JOptionPane.showMessageDialog(null, "jar目录下不能为空", "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        info.setText("开始制作补丁\n");
        info.append("文件的MD5值生成中......\n");
        Map<String, Map<String, String>> md5Map = new HashMap<String, Map<String, String>>();
        Map<String, String> dmMap = new HashMap<String, String>();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        //获取dm文件的MD5值
        for (File dm : dmFiles) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(dm);
                String md5 = DigestUtils.md5Hex(input);
                dmMap.put(dm.getName(), md5);
                info.append("文件:" + dm.getName() + ", MD5值:" + md5 + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
        md5Map.put(ZipFileType.dm.toString(), dmMap);
        //获取jar文件的MD5值
        Map<String, String> jarMap = new HashMap<String, String>();
        for (File jar : jarFiles) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(jar);
                String md5 = DigestUtils.md5Hex(input);
                jarMap.put(jar.getName(), md5);
                info.append("文件:" + jar.getName() + ", MD5值:" + md5 + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
        md5Map.put(ZipFileType.jar.toString(), jarMap);

        String xmlFile = folderPath + File.separator + "kdpkgs.xml";
        info.append("配置文件生成中......\n");
        try {
            this.createXml(appNameList, appidList, versionNo, md5Map, xmlFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            info.append("生成配置文件异常：" + ex.getMessage() + "\n");
        }
        info.append("配置文件：" + xmlFile + "\n");

        //打成压缩包
        info.append("补丁包压缩中.....\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
        String zipPatch = CompressFileUtil.compress(folderPath, folderPath, cloud + "-v" + versionNo + "-" + formatter.format(LocalDate.now()));
        info.append("补丁制作完成，补丁包位置:" + zipPatch + "\n");
    }

    //生成xml文件
    private void createXml(List<String> appNameList, List<String> appidList, String versionNo, Map<String, Map<String, String>> md5Map, String xmlFile) throws Exception{
        //创建xml文档
        Document document = DocumentHelper.createDocument();
        //创建根元素
        Element kdpkgs = document.addElement("kdpkgs").addAttribute("isv", "kingdee").addAttribute("ver", versionNo);
        kdpkgs.addElement("format").addAttribute("ver", "1.0");
        Element description = kdpkgs.addElement("description");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        description.addElement("time").addText(formatter.format(ZonedDateTime.now()));
        description.addElement("content").addText("苍穹行业补丁工具自动生成");

        //添加根元素下的子元素及其属性,内容
        Element product = kdpkgs.addElement("product");
        product.addAttribute("name", "cosmic_cr").addAttribute("nameCN", "金蝶云苍穹建筑与房地产行业版").addAttribute("ver", versionNo);
        product.addElement("force").addText("true");
        Map<String, String> jarMap = md5Map.get(ZipFileType.jar.toString());
        Map<String, String> dmMap = md5Map.get(ZipFileType.dm.toString());
        for (String appName : appNameList) {
            Element app = product.addElement("app");
            app.addElement("name").addText(appName);
            app.addElement("ver").addText(versionNo);
            for (String appid : appidList) {
                if (appName.contains(appid)) {
                    app.addElement("appids").addText(appid);
                    break;
                }
            }
            app.addElement("force").addText("true");

            StringBuffer resource = new StringBuffer();
            for (String dmName : dmMap.keySet()) {
                if (dmName.contains(appName)) {
                    resource.append(StringUtil.getHashCode(dmName));
                    break;
                }
            }
            for (String jarName : jarMap.keySet()) {
                if (jarName.contains(appName)) {
                    resource.append(",");
                    resource.append(StringUtil.getHashCode(jarName));
                    break;
                }
            }
            app.addElement("resource").addText(resource.toString());
        }

        for (String key : dmMap.keySet()) {
            Element kdpkg = kdpkgs.addElement("kdpkg");
            kdpkg.addElement("ID").addText(String.valueOf(StringUtil.getHashCode(key)));
            kdpkg.addElement("sourcePath").addText("dm");
            kdpkg.addElement("outputPath");
            kdpkg.addElement("name").addText(key);
            kdpkg.addElement("md5").addText(dmMap.get(key));
            kdpkg.addElement("type").addText(ZipFileType.dm.toString());
        }

        for (String key : jarMap.keySet()) {
            Element kdpkg = kdpkgs.addElement("kdpkg");
            kdpkg.addElement("ID").addText(String.valueOf(StringUtil.getHashCode(key)));
            kdpkg.addElement("sourcePath").addText("jar/biz");
            kdpkg.addElement("outputPath").addText("biz");
            kdpkg.addElement("name").addText(key);
            kdpkg.addElement("md5").addText(jarMap.get(key));
            kdpkg.addElement("type").addText(ZipFileType.jar.toString());
        }
        // 格式化输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        FileOutputStream fos = new FileOutputStream(xmlFile);
        format.setEncoding("UTF-8");
        // 格式化输出流
        XMLWriter xmlWriter = new XMLWriter(fos, format);
        // 将document写入到输出流
        try {
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainUI util = new MainUI();
        util.setVisible(true);
        util.pack();
    }
}