package com.dnsxo.app;

import com.dnsxo.enums.ProductDomainEnum;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * @author GAOFENG (http://www.dnsxo.com)
 * @date 2020年1月9日 下午11:02:24
 */
public class MainUI extends JFrame implements ActionListener {

    private String version = "4.0";
    private JButton createBtn;
    private JButton helpBtn;
    /**
     * 产品所属类型
     */
    private JComboBox<String> productTypeBox;
    /**
     * 所属云
     */
    private JComboBox<String> cloudBox;
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

        this.setTitle("金蝶云苍穹补丁制作工具V" + version);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setSize(200, 50);
        //内容区
        JPanel context = new JPanel();
        context.setSize(200, 200);
        context.setLayout(new BorderLayout(20, 20));

        JPanel head = new JPanel();
        JPanel content = new JPanel();
        JPanel bottom = new JPanel();
        context.add(head, BorderLayout.NORTH);
        context.add(content, BorderLayout.CENTER);
        context.add(bottom, BorderLayout.SOUTH);

        this.getContentPane().add(toolBar, BorderLayout.NORTH);
        this.getContentPane().add(context, BorderLayout.CENTER);

        //产品类型下拉列表
        productTypeBox = new JComboBox<>();
        productTypeBox.addItem(ProductEnum.BIZ.getName());
        productTypeBox.addItem(ProductEnum.INDUSTRY.getName());
        productTypeBox.setPreferredSize(new Dimension(150, 30));
        head.add(productTypeBox);
        productTypeBox.addActionListener(this);

        //云下拉列表
        cloudBox = new JComboBox<>();
        for (ProductDomainEnum domainEnum : ProductDomainEnum.values()) {
            cloudBox.addItem(domainEnum.getName());
        }
        cloudBox.setPreferredSize(new Dimension(150, 30));
        head.add(cloudBox);
        cloudBox.addActionListener(this);

        cloudLabel = new JLabel("云标识");
        head.add(cloudLabel);
        cloudFiled = new JTextField();
        cloudFiled.setPreferredSize(new Dimension(50, 30));
        head.add(cloudFiled);

        //应用标识
        appsLabel = new JLabel("应用标识");
        head.add(appsLabel);
        appsFiled = new JTextField();
        appsFiled.setPreferredSize(new Dimension(150, 30));
        head.add(appsFiled);

        //版本号
        versionLabel = new JLabel("版本号");
        head.add(versionLabel);
        versionNoFiled = new JTextField();
        //设置默认值
        versionNoFiled.setText(version + ".");
        versionNoFiled.setPreferredSize(new Dimension(50, 30));
        head.add(versionNoFiled);

        //归档目录
        pathLabel = new JLabel("补丁文件路径");
        head.add(pathLabel);
        folderFiled = new JTextField();
        head.add(selectFolder);
        head.add(folderFiled);
        folderFiled.setPreferredSize(new Dimension(350, 30));
        selectFolder.addActionListener(this);

        //制作按钮
        createBtn = new JButton("制作补丁");
        createBtn.addActionListener(this);
        toolBar.add(createBtn);
        toolBar.addSeparator();
        toolBar.setFloatable(true);
        helpBtn = new JButton("帮助");
        helpBtn.addActionListener(this);
        toolBar.add(helpBtn);

        info = new JTextArea(30, 100);
        JScrollPane sp = new JScrollPane(info);
        content.add(sp);

        //版权
        JLabel rightLabel = new JLabel("高峰出品 必属精品");
        bottom.add(rightLabel);
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
        }
        //产品类型
        else if (productTypeBox.equals(source)) {
            ProductEnum type = ProductEnum.getEnumByName(productTypeBox.getSelectedItem());
            ComboBoxModel data = new DefaultComboBoxModel();
            for (ProductDomainEnum domain : ProductDomainEnum.values()) {
                if (type == ProductEnum.BIZ) {
                    if (domain.isStd()) {
                        ((DefaultComboBoxModel) data).addElement(domain.getName());
                    }
                } else {
                    if (type == ProductEnum.INDUSTRY) {
                        if (!domain.isStd()) {
                            ((DefaultComboBoxModel) data).addElement(domain.getName());
                        }
                    }
                }
            }
            cloudBox.setModel(data);
            cloudFiled.setText("");
            appsFiled.setText("");

        } else if (cloudBox.equals(source)) {
            ProductDomainEnum domain = ProductDomainEnum.getEnumByName(cloudBox.getSelectedItem());
            if (domain == null) {
                return;
            }
            cloudFiled.setText(domain.getCloudCode());
            //设置默认值
            //项目云
            if (ProductDomainEnum.PMGT == domain) {
                appsFiled.setText("pmbs,pmas,pmba,pmct,pmco,pmim,pmfs,pmsc,pmem,pmpt");
            }
            //企业绩效云
            else if (ProductDomainEnum.EPM == domain) {
                cloudFiled.setText(domain.getCloudCode());
                appsFiled.setText("eb,bgmd,bgbd,bgm,bgc");
            }
            //建筑项目云
            else if (ProductDomainEnum.EC == domain) {
                appsFiled.setText("cont,ecbd,ecco,ecma");
            }
            //我家云
            else if (ProductDomainEnum.ASC == domain) {
                appsFiled.setText("psmd,abd,ren,ass,rec,cha,fts");
            }
            //我家云
            else if (ProductDomainEnum.PSC == domain) {
                appsFiled.setText("psbd");
            }

        } else if (createBtn.equals(source)) {
            //获取补丁所属产品信息
            ProductEnum type = ProductEnum.getEnumByName(productTypeBox.getSelectedItem());
            //获取云信息
            ProductDomainEnum domain = ProductDomainEnum.getEnumByName(cloudBox.getSelectedItem());

            String cloud = cloudFiled.getText().trim();
            if ("".equals(cloud)) {
                JOptionPane.showMessageDialog(this, "请输入云标识", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String apps = appsFiled.getText().trim();
            if ("".equals(apps)) {
                JOptionPane.showMessageDialog(this, "请输入应用标识", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String versionNo = versionNoFiled.getText().trim();
            if ("".equals(versionNo) || versionNo.endsWith(".")) {
                JOptionPane.showMessageDialog(this, "请输入有效的补丁版本号", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String folderPath = folderFiled.getText().trim();
            if ("".equals(folderPath)) {
                JOptionPane.showMessageDialog(this, "请输入补丁文件目录", "提示信息", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            //通过云标识、应用标识组合dm、jar文件报名
            List<String> appNameList = new ArrayList<String>();
            List<String> appidList = Arrays.asList(apps.split(","));
            for (String appid : appidList) {
                appNameList.add(cloud + "-" + appid);
            }
            //获取有效的dm文件
            String dmFolderPath = folderPath + File.separator + ZipFileType.dm.toString();
            File dmFolder = new File(dmFolderPath);
            if (!dmFolder.exists()) {
                JOptionPane.showMessageDialog(this, String.format("补丁制作中止，原因是目录%s不存在，请检查。", dmFolderPath));
            }
            File[] dmFiles = dmFolder.listFiles();
            List<File> validDmFiles = new ArrayList<File>();
            for (String cloudAndAppName : appNameList) {
                boolean isExist = false;
                for (File dmFile : dmFiles) {
                    String fileName = dmFile.getName().replace("-dm-1.x.zip", "");
                    if (fileName.equals(cloudAndAppName)) {
                        validDmFiles.add(dmFile);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    JOptionPane.showMessageDialog(this, String.format("%s目录下未找到%s相关的压缩包", dmFolder.getPath(), cloudAndAppName), "警告信息", JOptionPane.WARNING_MESSAGE);
                }
            }
            //获取有效的jar文件
            String bizFolderPath = folderPath + File.separator + ZipFileType.jar.toString() + File.separator + "biz";
            File jarFolder = new File(bizFolderPath);
            if (!jarFolder.exists()) {
                JOptionPane.showMessageDialog(this, String.format("补丁制作中止，原因是目录%s不存在，请检查。", bizFolderPath));
            }
            File[] jarFiles = jarFolder.listFiles();
            List<File> validJarFiles = new ArrayList<File>();
            for (String cloudAndAppName : appNameList) {
                boolean isExist = false;
                for (File jarFile : jarFiles) {
                    String fileName = jarFile.getName().replace(".zip", "");
                    if (fileName.equals(cloudAndAppName)) {
                        validJarFiles.add(jarFile);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    JOptionPane.showMessageDialog(this, String.format("%s目录下未找到%s相关的压缩包", jarFolder.getPath(), cloudAndAppName), "警告信息", JOptionPane.WARNING_MESSAGE);
                }
            }

            if (validDmFiles.isEmpty() && validJarFiles.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.format("%s目录下未找到相关的元数据与应用压缩包", folderPath), "错误信息", JOptionPane.ERROR_MESSAGE);
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
            for (File dm : validDmFiles) {
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
            for (File jar : validJarFiles) {
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
                this.createXml(type, domain, appNameList, appidList, versionNo, md5Map, xmlFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                info.append("生成配置文件异常：" + ex.getMessage() + "\n");
            }
            info.append("配置文件：" + xmlFile + "\n");
            //打成压缩包
            info.append("补丁包压缩中.....\n");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
            String zipPatch = FileUtil.compress(folderPath, folderPath, cloud + "-v" + versionNo + "-" + dateFormatter.format(LocalDate.now()) + timeFormatter.format(LocalTime.now()));
            info.append(LocalDateTime.now() + "，补丁制作完成，补丁包位置：" + zipPatch + "\n");
        } else if (helpBtn.equals(source)) {
            JOptionPane.showMessageDialog(this, String.format("开发者：高峰 \n 联系电话：15080668358"), "提示信息", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 生成xml文件
     */
    private void createXml(ProductEnum type, ProductDomainEnum domain, List<String> appNameList, List<String> appidList, String versionNo, Map<String, Map<String, String>> md5Map, String xmlFile) throws Exception {
        //创建xml文档
        Document document = DocumentHelper.createDocument();
        //创建根元素
        Element kdpkgs = document.addElement("skdpkgs").addAttribute("isv", "kingdee").addAttribute("ver", versionNo);
        kdpkgs.addElement("format").addAttribute("ver", "1.0");
        Element description = kdpkgs.addElement("description");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        description.addElement("time").addText(formatter.format(ZonedDateTime.now()));
        description.addElement("content").addText("苍穹补丁工具自动生成");

        //添加根元素下的子元素及其属性,内容
        Element product = kdpkgs.addElement("product");
        /*
        苍穹4.0品牌升级后分领域补丁
        if (type == ProductEnum.BIZ) {
            product.addAttribute("name", "cosmic_biz").addAttribute("nameCN", ProductEnum.BIZ.getName()).addAttribute("ver", versionNo);
        } else {
            //由于建筑的补丁已经这样发了，所以要兼容一下
            if (ProductDomainEnum.EC.getCloudCode().equals(domain.getCloudCode())) {
                product.addAttribute("name", "cosmic_cr").addAttribute("nameCN", String.format("金蝶云苍穹行业产品（%s）", ProductDomainEnum.EC.getName())).addAttribute("ver", versionNo);
            } else {
                product.addAttribute("name", "cosmic_" + domain.getCloudCode()).addAttribute("nameCN", String.format("金蝶云苍穹行业产品（%s）", domain.getName())).addAttribute("ver", versionNo);
            }
        }
        */

        product.addAttribute("name", domain.getDomainCode()).addAttribute("nameCN", String.format("%s", domain.getDomainName())).addAttribute("ver", versionNo);

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
                    if(resource.length() != 0){
                        resource.append(",");
                    }
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
        //禁止用户改变窗体大小
        util.setResizable(false);
        util.setVisible(true);
        util.pack();
    }

}