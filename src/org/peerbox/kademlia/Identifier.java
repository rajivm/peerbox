package org.peerbox.kademlia;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Identifier implements Identifiable {
	protected BigInteger value;
	protected static final SecureRandom secureRandom = new SecureRandom();

	protected Identifier() {

	}

	protected Identifier(BigInteger value) {
		// TODO: check that the number of bytes is valid
		this.value = value;
	}

	public BigInteger getIntegerValue() {
		// TODO: check that the number of bytes is valid
		return value;
	}

	public static BigInteger calculateDistance(Identifier x, Identifier y) {
		return x.getIntegerValue().xor(y.getIntegerValue());
	}

	public static BigInteger calculateDistance(Identifiable x, Identifiable y) {
		return calculateDistance(x.getIdentifier(), y.getIdentifier());
	}

	public Identifier getIdentifier() {
		return this;
	}

	public static Identifier fromBytes(byte[] bytes) {
		return new Identifier(new BigInteger(1, bytes));
	}

	/**
	 * This generates a secure random 160bit Identifier object for Kademlia
	 * TODO: Somehow support for variable key-size lengths should be added.
	 * 
	 * @return
	 */
	public static Identifier generateRandom() {
		byte[] randomBytes = new byte[20];
		secureRandom.nextBytes(randomBytes);
		return fromBytes(randomBytes);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Identifier)) {
			return false;
		}

		Identifier id = (Identifier) o;

		return value.equals(id.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	public String toString() {
		return value.toString();
	}
}
