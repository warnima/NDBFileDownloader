package com.ndb.file.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service("fileOperationService")
public class FileOperationServiceImpl implements FileOperationService {
	 
	private static final String SESSION_CONFIG_STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	
	@Value("${sftp.client.host}")
	private String host;
	@Value("${sftp.client.port}")
	private Integer port;
	@Value("${sftp.client.protocol}")
	private String protocol;
	@Value("${sftp.client.username}")
	private String username;
	@Value("${sftp.client.password}")
	private String password;
	@Value("${sftp.client.root}")
	private String root;
	@Value("${sftp.client.privateKey}")
	private String privateKey;
	@Value("${sftp.client.passphrase}")
	private String passphrase;
	@Value("${sftp.client.sessionStrictHostKeyChecking}")
	private String sessionStrictHostKeyChecking;
	@Value("${sftp.client.sessionConnectTimeout}")
	private Integer sessionConnectTimeout;
	@Value("${sftp.client.channelConnectedTimeout}")
	private Integer channelConnectedTimeout;
	
	@Value("${local.fle.basePath}")
	private String basePath;
	
	private Session createSession(JSch jsch, String host, String username, Integer port) throws Exception {
        Session session = null;
        if (port <= 0) {
            session = jsch.getSession(username, host);
        } else {
            session = jsch.getSession(username, host, port);
        }
        if (session == null) {
            throw new Exception(host + " session is null");
        }
        session.setConfig(SESSION_CONFIG_STRICT_HOST_KEY_CHECKING, sessionStrictHostKeyChecking);
        return session;
    }

    private void disconnect(ChannelSftp sftp) {
        try {
            if (sftp != null) {
                if (sftp.isConnected()) {
                    sftp.disconnect();
                } 
                if (null != sftp.getSession()) {
                    sftp.getSession().disconnect();
                }
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    private ChannelSftp createSftp() throws Exception {
    	try {
	        JSch jsch = new JSch();
	        Session session = createSession(jsch, host, username, port);
	        session.setPassword(password);
	        session.connect(sessionConnectTimeout);
	        Channel channel = session.openChannel(protocol);
	        channel.connect(channelConnectedTimeout);
	        return (ChannelSftp) channel;
    	}catch (Exception e) {
    		throw new Exception("Create Connection Fail.");
		}
    }

	@Override
	public List<String> getSftpDerectoryList() throws Exception {
		ChannelSftp sftp = createSftp();
		try {
			List<String> dirList = new ArrayList<String>();
			Vector<LsEntry> lsList=sftp.ls(root);
			for(LsEntry elm:lsList) {
				if(elm.getAttrs().isDir()) {
					dirList.add(elm.getFilename());
				}
			}
			return dirList;
		}catch (Exception e) {
    		throw new Exception("Read folders Fail.");
		}finally {
			disconnect(sftp);
		}
	}
	
	@Override
	public List<String> getLocalDerectoryList() throws Exception {
		try {
			List<String> dirList = new ArrayList<String>();
			File baseDirctoryPath = new File(basePath);
			if(baseDirctoryPath.exists()) {
				File []lsList=baseDirctoryPath.listFiles();
				for(File file:lsList) {
					if(file.isDirectory()) {
						dirList.add(file.getName());
					}
				}
			}
			return dirList;
		}catch (Exception e) {
    		throw new Exception("Read folders Fail.");
		}
	}

	@Override
	public List<String> getSftpFileList(String folderName) throws Exception {
		ChannelSftp sftp = createSftp();
		try {
			List<String> fileList = new ArrayList<String>();
			Vector<LsEntry> lsList=sftp.ls(root+"/"+folderName);
			for(LsEntry elm:lsList) {
				if(!elm.getAttrs().isDir()) {
					fileList.add(elm.getFilename());
				}
			}
			return fileList;
		}catch (Exception e) {
    		throw new Exception("Read files Fail.");
		}finally {
			disconnect(sftp);
		}
	}
	
	@Override
	public List<String> getLocalFileList() throws Exception {
		try {
			List<String> fileList = new ArrayList<String>();
			File baseDirctoryPath = new File(basePath);
			File []lsList=baseDirctoryPath.listFiles();
			for(File file:lsList) {
				if(file.isFile()) {
					fileList.add(file.getName());
				}
			}
			return fileList;
		}catch (Exception e) {
    		throw new Exception("Read files Fail.");
		}
	}
	
	@Override
    public File downloadSftpFile(String targetPath) throws Exception {
        ChannelSftp sftp = this.createSftp();
        OutputStream outputStream = null;
        try {
        	sftp.cd(root);
            File file = new File(targetPath.substring(targetPath.lastIndexOf("/") + 1));
            outputStream = new FileOutputStream(file);
            sftp.get(targetPath, outputStream);
            return file;
        } catch (Exception e) {
            throw new Exception("Download File failure");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            this.disconnect(sftp);
        }
	}
	
	@Override
    public File downloadLocalFile(String targetPath) throws Exception {
        try {
            File file = new File(basePath+"/"+targetPath);
            if(file.exists()) {
            	return file;
            }else {
            	throw new Exception("Download File failure");
            }
        } catch (Exception e) {
            throw new Exception("Download File failure");
        } 
	}

	@Override
	public void transferFile(String localFileName) throws Exception {
		ChannelSftp sftp = createSftp();
		try {
			sftp.put(localFileName,root);
		}catch (Exception e) {
    		throw new Exception("SFTP Fail.");
		}finally {
			disconnect(sftp);
		}
	}
    
}
