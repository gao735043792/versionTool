package com.dnsxo.app;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

/**
 * @author GAOFENG (http://www.dnsxo.com)
 * @date 2020年1月11日 下午0s9:04:20
 */
public class CompressFileUtil {

    public static String compress(String srcDir, String destDir, String zipName) {
        File src = new File(srcDir);
        if (!src.exists()){
            throw new RuntimeException(srcDir + "不存在！");
        }
        Project project = new Project();
        project.setName(zipName);
        Zip zip = new Zip();
        //设置编码，防止压缩文件名字乱码，还有被压缩文件的乱码
        zip.setEncoding("UTF-8");
        zip.setProject(project);
        File dest = new File(destDir + File.separator + zipName + ".zip");
        if(dest.exists()){
            dest.delete();
        }
        zip.setDestFile(dest);
        FileSet fileSet = new FileSet();
        fileSet.setProject(project);
        fileSet.setDir(src);
        //只打包的目录与文件
        fileSet.setIncludes("/dm/*");
        fileSet.setIncludes("/jar/*");
        fileSet.setIncludes("kdpkgs.xml");
        //排除哪些文件或文件夹
        fileSet.setExcludes("*.zip");
        zip.addFileset(fileSet);
        //执行生成
        zip.execute();
        return dest.getAbsolutePath().toString();
    }
}