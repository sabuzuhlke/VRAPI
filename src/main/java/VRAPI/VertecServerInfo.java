package VRAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertecServerInfo {
    public static final String VERTEC_SERVER_HOST = "172.18.112.42";
    public static final String VERTEC_SERVER_PORT = "8095";

    public static final Integer BAD_REQUEST  = 3;
    public static final Integer UNAUTHORISED = 2;
    public static final Integer FORBIDDEN    = 1;
    public static final Integer AUTHORISED   = 0;

    public static final Logger log = LoggerFactory.getLogger("TestLogger");
}
