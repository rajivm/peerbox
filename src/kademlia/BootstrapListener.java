package kademlia;

/**
 * Listener that is started after bootstrap process
 * is initiated
 *
 */

public interface BootstrapListener {
	void onBootstrapSuccess();
	void onBootstrapFailure();
}
