package com.ndb.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.ndb.file.operations.FileOperationService;

public class ScheduleTask {
	@Autowired(required=true)
	@Qualifier(value="fileOperationService")
	private FileOperationService fileOperationService;
	
	@Value("${sftp.client.host}")
	private String host;
	
	public FileOperationService getFileOperationService() {
		return fileOperationService;
	}

	public void setFileOperationService(FileOperationService fileOperationService) {
		this.fileOperationService = fileOperationService;
	}
	
	
	@Scheduled(fixedRate=5000)
	public void runTask() {
		try {
			//do
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
