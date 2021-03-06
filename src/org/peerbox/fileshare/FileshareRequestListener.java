package org.peerbox.fileshare;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.peerbox.fileshare.messages.FileRequest;
import org.peerbox.fileshare.messages.FileResponse;
import org.peerbox.fileshare.messages.Response;
import org.peerbox.fileshare.messages.SharedDirectoryRequest;
import org.peerbox.fileshare.messages.SharedDirectoryResponse;
import org.peerbox.network.http.HttpStaticFileServer;
import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCServiceRequestListener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FileshareRequestListener implements RPCServiceRequestListener {
	protected final Gson gson = new Gson();
	protected FileShareManager manager;

	public FileshareRequestListener(FileShareManager manager) {
		this.manager = manager;
	}

	@Override
	public void onRequestRecieved(RPCEvent e) {
		JsonParser parser = new JsonParser();

		final JsonObject root = (JsonObject) parser.parse(e.getDataString());
		String command = root.get("command").getAsString();
		// Request request;
		Response response = null;
		if (command.equals(SharedDirectoryRequest.COMMAND)) {
			SharedDirectoryRequest fnr = gson.fromJson(root, SharedDirectoryRequest.class);
			FileInfo[] contents = manager.getSharedContents(fnr.getSharedRelativePath());
			if (contents == null) {
				response = new SharedDirectoryResponse(null);
				// response = new
				// FindNodeResponse(ni.getBuckets().getNearestNodes(fnr.getTargetIdentifier(),
				// ni.getConfiguration().getK()));
			} else {
				response = new SharedDirectoryResponse(contents);
			}

		} else if (command.equals(FileRequest.COMMAND)) {
			FileRequest fnr = gson.fromJson(root, FileRequest.class);
			String requestId = UUID.randomUUID().toString();
			// TODO: generate URI to pass to client
			// TODO: figure out how to determine local IP address
			// URI uri = new URI("http://")
			// TODO: set the expiration date properly
			// TODO: take out hard coded file response URI
			if (manager.setRequestIDtoFileRequest(fnr.getRelativePath() == null ? "/" : fnr.getRelativePath(),
					requestId, fnr.getFile(), (System.currentTimeMillis() / 1000) + 10)) {
				try {
					response = new FileResponse(new URI("http://" + manager.getRPC().getLocalURI().getHost()
							+ ":30000/" + requestId));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				response = new FileResponse(null);

			}
		}
		if (response != null) {
			String responseString = gson.toJson(response);
			e.respond(responseString);
		}
	}

}
