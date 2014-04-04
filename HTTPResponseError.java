public class HTTPResponseError extends Exception {

	public HTTPResponseError(int responseCode) {
		super("Error, Server returned: " + responseCode);
	}

}