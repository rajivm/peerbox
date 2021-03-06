package org.peerbox.security;

import java.io.Serializable;

public class SignedMessage implements Serializable {
	protected String message;
	protected byte[] signature;

	public SignedMessage(String mess, byte[] sig) {
		message = mess;
		signature = sig;
	}

	protected SignedMessage() {

	}

	public String getMessage() {
		return message;
	}

	public byte[] getSignature() {
		return signature;
	}
}