package lk.avix.http.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import lk.avix.http.client.HttpClient;
import lk.avix.http.listener.HttpEchoServer;
import lk.avix.http.passthrough.HttpPassthrough;

public class Bootstrap {

    private enum TYPE {
        CLIENT,
        SERVER,
        PASSTHROUGH
    }

    @Parameter(names = "--type", description = "Type of Program", required = true)
    private TYPE type;

    @Parameter(names = "--ssl", description = "Enable SSL", arity = 1)
    private boolean ssl = false;

    @Parameter(names = "--http2", description = "Use HTTP/2 protocol instead of HTTP/1.1", arity = 1)
    private boolean http2 = false;

    @Parameter(names = "--listener-port", description = "Listener Port")
    private int listenerPort = 9090;

    @Parameter(names = "--server-host", description = "Server Host")
    private String serverHost = "localhost";

    @Parameter(names = "--server-port", description = "Server Port")
    private int serverPort = 9191;

    @Parameter(names = "--server-path", description = "Server Path")
    private String serverPath = "/hello/sayHello";

    @Parameter(names = "--keystore-path", description = "Keystore Path")
    private String keystorePath;

    @Parameter(names = "--keystore-pass", description = "Keystore Password")
    private String keystorePass = "wso2carbon";

    @Parameter(names = "--truststore-path", description = "Truststore Path")
    private String truststorePath;

    @Parameter(names = "--truststore-pass", description = "Truststore Password")
    private String truststorePass = "wso2carbon";

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        final JCommander jcommander = new JCommander(bootstrap);
        jcommander.setProgramName(HttpEchoServer.class.getSimpleName());
        try {
            jcommander.parse(args);
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        bootstrap.doStart();
    }

    public void doStart() {
        float httpVersion = http2 ? 2.0f : 1.1f;
        String scheme = ssl ? "https" : "http";
        switch (type) {
            case SERVER:
                new HttpEchoServer().doStart(httpVersion, scheme, serverPort, keystorePath, keystorePass);
                break;
            case CLIENT:
                new HttpClient().doStart(httpVersion, scheme, serverHost, serverPort, serverPath,
                                         truststorePath, truststorePass);
                break;
            case PASSTHROUGH:
                new HttpPassthrough().doStart(httpVersion, scheme, listenerPort, serverHost, serverPort, serverPath,
                                              keystorePath, keystorePass, truststorePath, truststorePass);
        }
    }
}
