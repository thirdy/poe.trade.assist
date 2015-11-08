package poe.trade.assist.util;

/**
 *
 * @author thirdy
 */
public class BlackmarketException extends Exception {

    public BlackmarketException(Exception ex) {
        super(ex);
    }

	public BlackmarketException(String msg) {
		super(msg);
	}
	
	public BlackmarketException(String msg, Exception ex) {
		super(msg, ex);
	}
    
}