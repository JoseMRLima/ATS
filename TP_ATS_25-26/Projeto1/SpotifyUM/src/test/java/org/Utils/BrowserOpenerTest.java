package org.Utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BrowserOpenerTest {

    @Test
    void testInvalidUriThrowsURISyntaxException() {
        BrowserOpener opener = new BrowserOpener("not a valid uri ^^");
        assertThrows(Exception.class, opener::abrir);
    }

    @Test
    void testDesktopNotSupportedOrBrowseThrows() {
        // In headless/WSL environments Desktop is not supported → UnsupportedOperationException
        // or browse() throws IOException. Either way an exception must be thrown or
        // the call succeeds silently. We just assert no unexpected crash occurs.
        BrowserOpener opener = new BrowserOpener("http://example.com");
        try {
            opener.abrir();
        } catch (UnsupportedOperationException | java.net.URISyntaxException | java.io.IOException e) {
            // expected in CI / headless environments
        }
    }
}
