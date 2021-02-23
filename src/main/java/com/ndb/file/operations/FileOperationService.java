package com.ndb.file.operations;

import java.io.File;
import java.util.List;

public interface FileOperationService {
	 public List<String> getSftpDerectoryList()throws Exception;
	 public List<String> getSftpFileList(String folderName)throws Exception;
	 public File downloadSftpFile(String targetPath) throws Exception;
	 public File downloadLocalFile(String targetPath) throws Exception;
	 public List<String> getLocalDerectoryList() throws Exception;
	 public List<String> getLocalFileList() throws Exception;
	 public void transferFile(String localFileName) throws Exception;
	 
}
