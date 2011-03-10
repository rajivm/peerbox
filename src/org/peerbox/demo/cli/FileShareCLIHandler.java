package org.peerbox.demo.cli;

import java.io.File;

import org.peerbox.fileshare.FileInfo;
import org.peerbox.fileshare.FileShareManager;
import org.peerbox.fileshare.ResponseListener;
import org.peerbox.fileshare.messages.FileResponse;
import org.peerbox.fileshare.messages.SharedDirectoryResponse;
import org.peerbox.friend.Friend;
import org.peerbox.friend.FriendManager;
import org.peerbox.network.http.HttpClient;
import org.peerbox.network.http.HttpClientListener;

public class FileShareCLIHandler implements CLIHandler{
	
	protected FileShareManager fileshare;
	protected FriendManager friend_manager;
	public FileShareCLIHandler(FileShareManager fs_manager, FriendManager fr_manager) {
		this.fileshare = fs_manager;
		this.friend_manager = fr_manager;
	}
	@Override
	public void handleCommand(String[] args, final ExtendableCLI cli) {
		// TODO Auto-generated method stub
		if(args.length < 2) {
			help();
			return;
		}
		String function = args[1];
		if(function.equalsIgnoreCase("share")) {
			if(args.length != 3) {
				help();
				return;
			}
			String folder = args[2];
			if(fileshare.setSharedFilePath(folder)) {
				cli.out().println("Sharing folder located at "+folder);
			} else {
				cli.out().println("No directory exists at "+folder+".  Please specify another folder to share");
			}
		}
		else if(function.equalsIgnoreCase("browse")) {
			if(args.length < 3 || args.length > 4) {
				help();
				return;
			}
			final String alias = args[2];
			Friend friend = friend_manager.getFriend(alias);
			if(friend != null) {
				String relativePath = "";
				if(args.length == 4) {
					relativePath = args[3];
				}
				fileshare.getSharedDirectory(relativePath, friend, new ResponseListener<SharedDirectoryResponse>() {

					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						cli.out().println("Unable to retrieve shared directory from "+alias);
					}

					@Override
					public void onResponseReceived(
							SharedDirectoryResponse response) {
						if(response.getContents() != null) {
							FileInfo[] contents = response.getContents();
							for(int i=0; i<contents.length;i++){
								System.out.println(contents[i].getName());
							}
						}
						else{
							cli.out().println("The shared directory does not exist");
						}
						
					}
					
				});
			}
			else {
				cli.out().println("The friend "+alias+" does not exist");
			}
		}
		else if(function.equalsIgnoreCase("get")) {
			if(args.length != 4) {
				help();
				return;
			}
			final String alias = args[2];
			Friend friend = friend_manager.getFriend(alias);
			if(friend != null) {
				final String filePath = args[3];
				final File file = new File(filePath);
				fileshare.getFile(file.getParent(), friend, file.getName(), new ResponseListener<FileResponse>() {

					@Override
					public void onFailure() {
						cli.out().println("Failed to receive response to file request");
						
					}

					@Override
					public void onResponseReceived(
							FileResponse response) {
						//System.out.println("RECEIVED RESPONSE TO FILE REQUEST");
						//TODO: Pass full download path
						fileshare.download(response.getFileLocURI(), file.getName(), new HttpClientListener() {

							@Override
							public void finished() {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void downloadError() {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void started() {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void localFileError() {
								// TODO Auto-generated method stub
								
							}
							
						});
						
					}
					
				});
				
			}
			else {
				cli.out().println("The friend "+alias+" does not exist");
			}
		}
		
	}
	
	public void help() {
		System.out.println("Invalid command.");
	}

}
