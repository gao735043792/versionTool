package com.dnsxo.app;

import com.dnsxo.enums.ProductEnum;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

/**
 * @author GAOFENG (http://www.dnsxo.com)
 * @date 2020年1月9日 下午11:02:24
 */
public class MainUI extends JFrame implements ActionListener {

    private JButton createBtn;

    /**
     * 产品所属类型
     */
    private JComboBox<String> jComboBox;
    /**
     * 补丁目录
     */
    private JLabel pathLabel;
    private JTextField folderFiled;
    private JButton selectFolder = new JButton("...");
    /**
     * 文件选择器
     */
    private JFileChooser jFileChooser = new JFileChooser();

    /**
     * 版本号
     */
    private JLabel versionLabel;
    private JTextField versionNoFiled;
    /**
     * 云标识
     */
    private JLabel cloudLabel;
    private JTextField cloudFiled;
    /**
     * 应用标识
     */
    private JLabel appsLabel;
    private JTextField appsFiled;

    private JTextArea info;

    private MainUI() {
        init();
    }

    private void init() {
        initFrame();
    }

    /**
     * 布局
     */
    private void initFrame() {
        this.setTitle("金蝶云苍穹补丁制作工具");
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

        //下拉列表
        jComboBox = new JComboBox<String>();
        jComboBox.addItem(ProductEnum.BIZ.getName());
        jComboBox.addItem(ProductEnum.CR.getName());
        jp1.add(jComboBox);
        jComboBox.addActionListener(this);

        cloudLabel = new JLabel("云标识");
        jp1.add(cloudLabel);
        cloudFiled = new JTextField(6);
        jp1.add(cloudFiled);

        //应用标识
        appsLabel = new JLabel("应用标识");
        jp1.add(appsLabel);
        appsFiled = new JTextField(15);
        jp1.add(appsFiled);

        //版本号
        versionLabel = new JLabel("版本号");
        jp1.add(versionLabel);
        versionNoFiled = new JTextField(10);
        //设置默认值
        versionNoFiled.setText("2.0.");
        jp1.add(versionNoFiled);

        //归档目录
        pathLabel = new JLabel("补丁文件目录");
        jp1.add(pathLabel);
        folderFiled = new JTextField(30);
        jp1.add(folderFiled);
        jp1.add(selectFolder);
        selectFolder.addActionListener(this);

        //制作按钮
        createBtn = new JButton("开始制作");
        createBtn.addActionListener(this);
        jp1.add(createBtn);
        info = new JTextArea(30, 100);
        JScrollPane sp = new JScrollPane(info);
        jp2.add(sp);

        //版权
        JLabel rightLabel = new JLabel("建筑出品，必属精品。Mr.靠谱");
        jp3.add(rightLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        // 判断触发方法的按钮是哪个
        if (selectFolder.equals(source)) {
            // 设定只能选择到文件夹
            jFileChooser.setFileSelectionMode(1);
            // 此句是打开文件选择器界面的触发语句
            int state = jFileChooser.showOpenDialog(null);
            if (state == 1) {
                return;
            } else {
                // 选择到的目录
                File folder = jFileChooser.getSelectedFile();
                folderFiled.setText(folder.getAbsolutePath());
            }
        } else if (jComboBox.equals(source)) {
            ProductEnum type = ProductEnum.values()[jComboBox.getSelectedIndex()];
            //设置默认值
            if (ProductEnum.BIZ == type) {
                cloudFiled.setText("pmgt");
                appsFiled.setText("pmbs,pmpm,pmas,pmba,pmct,pmco,pmim,pmfs,pmsc,pmem");
            } else if (ProductEnum.CR == type) {
                cloudFiled.setText("ec");
                appsFiled.setText("cont,ecbd,ecco,ecma");
            }

        } else if (createBtn.equals(source)) {
            //获取补丁所属产品
            ProductEnum type = ProductEnum.values()[jComboBox.getSelectedIndex()];
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

            File jarFolder = new File(folderPath + File.separator + ZipFileType.jar.toString() + File.separator + "biz");
            File[] jarFiles = jarFolder.listFiles();

            if (dmFiles.length == 0 && jarFiles.length == 0) {
                JOptionPane.showMessageDialog(null, "dm、jar目录下不能同时为空", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
            info.setText(LocalDateTime.now() + ",开始制作补丁\n");
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
                if (this.macDefaultFile(dm)) {
                    continue;
                }
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
                if (this.macDefaultFile(jar)) {
                    continue;
                }
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
                this.createXml(type, appNameList, appidList, versionNo, md5Map, xmlFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                info.append("生成配置文件异常：" + ex.getMessage() + "\n");
            }
            info.append("配置文件：" + xmlFile + "\n");
            //打成压缩包
            info.append("补丁包压缩中.....\n");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
            String zipPatch = CompressFileUtil.compress(folderPath, folderPath, cloud + "-v" + versionNo + "-" + dateFormatter.format(LocalDate.now()) + timeFormatter.format(LocalTime.now()));
            info.append(LocalDateTime.now() + "，补丁制作完成，补丁包位置：" + zipPatch + "\n");
        }
    }

    /**
     * 生成xml文件
     */
    private void createXml(ProductEnum type, List<String> appNameList, List<String> appidList, String versionNo, Map<String, Map<String, String>> md5Map, String xmlFile) throws Exception {
        //创建xml文档
        Document document = DocumentHelper.createDocument();
        //创建根元素
        Element kdpkgs = document.addElement("kdpkgs").addAttribute("isv", "kingdee").addAttribute("ver", versionNo);
        kdpkgs.addElement("format").addAttribute("ver", "1.0");
        Element description = kdpkgs.addElement("description");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        description.addElement("time").addText(formatter.format(ZonedDateTime.now()));
        description.addElement("content").addText("苍穹补丁工具自动生成");

        //添加根元素下的子元素及其属性,内容
        Element product = kdpkgs.addElement("product");
        if (type == ProductEnum.BIZ) {
            product.addAttribute("name", "cosmic_" + type.getTypeCode()).addAttribute("nameCN", "金蝶云苍穹平台" + type.getName()).addAttribute("ver", versionNo);
        } else {
            product.addAttribute("name", "cosmic_" + type.getTypeCode()).addAttribute("nameCN", "金蝶云苍穹" + type.getName() + "行业版").addAttribute("ver", versionNo);
        }
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

    /**
     * mac系统下默认文件判断
     */
    private boolean macDefaultFile(File file) {
        return ".DS_STORE".equalsIgnoreCase(file.getName());
    }

    public static void main(String[] args) {
        MainUI util = new MainUI();
        util.setVisible(true);
        util.pack();
    }

}