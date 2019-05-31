package com.iwinad.uploadpicture;

import com.orhanobut.logger.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * @author :yixf
 * @version :1.0
 * @copyright : 深圳市创冠新媒体网络传媒有限公司版权所有
 * @creation date: 2017/8/11 0011
 * @description:${desc}
 * @update date :
 */
public class FtpUtil {

    private static FTPClient ftpClient = null;
    /**
     * 通过ftp上传文件
     * @param url ftp服务器地址 如： 192.168.1.110
     * @param port 端口如 ： 21
     * @param username  登录名
     * @param password   密码
     * @param remotePath  上到ftp服务器的磁盘路径
     * @param fileNamePath  要上传的文件路径
     * @return
     */
    public static void connectFtp(int index,String url, String port, String username,String password, String remotePath, String fileNamePath) {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(url, Integer.parseInt(port));
                boolean loginResult = ftpClient.login(username, password);
                int returnCode = ftpClient.getReplyCode();
                if (loginResult&& FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                    ftpClient.makeDirectory(remotePath);
                    // 设置上传目录
                    ftpClient.changeWorkingDirectory(remotePath);
                    ftpClient.setBufferSize(20480);
                    ftpClient.setControlEncoding("UTF-8");
                    ftpClient.enterLocalPassiveMode();
                    try {
                        uploadFile(index,ftpClient,fileNamePath,"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post("0");
                Logger.e("FTP客户端出错！"+e.toString());
            }
    }
    /**
     * 断开FTP连接
     */
    public static void dissConnect(){
        if(ftpClient==null){
            return;
        }
        try {
            ftpClient.disconnect();
            ftpClient = null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("关闭FTP连接发生异常！", e);
        }
    }
    // 实现上传文件的功能
    public static synchronized void uploadFile(int index,FTPClient ftpClient,String localPath, String serverPath)
            throws Exception {
        // 上传文件之前，先判断本地文件是否存在
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            Logger.e("本地文件不存在");
            return ;
        }
        String fileNames[] = localFile.getName().split(".");
        String endName = fileNames[fileNames.length-1];
        String fileName = System.currentTimeMillis()+index+"."+endName;
        Logger.e("the file name is "+fileName);
        // 如果本地文件存在，服务器文件也在，上传文件，这个方法中也包括了断点上传
        long localSize = localFile.length(); // 本地文件的长度
        FTPFile[] files = ftpClient.listFiles(fileName);
        long serverSize = 0;
        if (files.length == 0) {
            Logger.e("服务器文件不存在");
            serverSize = 0;
        } else {
            serverSize = files[0].getSize(); // 服务器文件的长度
        }
        if (localSize <= serverSize) {
            if (ftpClient.deleteFile(fileName)) {
                Logger.e("服务器文件存在,删除文件,开始重新上传");
                serverSize = 0;
            }
        }
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        // 进度
        long step = localSize / 100;
        long process = 0;
        long currentSize = 0;
        // 好了，正式开始上传文件
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setRestartOffset(serverSize);
        raf.seek(serverSize);
        OutputStream output = ftpClient.appendFileStream(fileName);
        byte[] b = new byte[20480];
        int length = 0;
        while ((length = raf.read(b)) != -1) {
            output.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    Logger.e("上传进度：" + process);
                }
            }
        }
        output.flush();
        output.close();
        raf.close();
        if (ftpClient.completePendingCommand()) {
            Logger.e("文件上传成功");
            EventBus.getDefault().post("1");
        } else {
            Logger.e("文件上传失败");
            EventBus.getDefault().post("0");
        }
        ftpClient.disconnect();
    }
}
