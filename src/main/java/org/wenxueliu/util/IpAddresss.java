package org.wenxueliu.util;

class IpAddress {

    /*
     * convert "10.1.1.1-2:1-2,10.1.2.1-2:2-3" to list
     *
     *  10.1.1.1-1
     *  10.1.1.1-2
     *  10.1.1.2-1
     *  10.1.1.2-2
     *  10.1.2.1-2
     *  10.1.2.1-3
     *  10.1.2.2-2
     *  10.1.2.2-3
     *
     * convert "10.1.1.1-2:*" to list
     *
     *  10.1.1.1-1024
     *  ....
     *  10.1.1.1-65535
     *  10.1.1.2-1024
     *  ....
     *  10.1.1.2-65535
     *
     * convert "10.1.1.*:*" to list
     *
     *  10.1.1.1-1024
     *  ....
     *  10.1.1.1-65535
     *  10.1.1.2-1024
     *  ....
     *  10.1.1.2-65535
     *  ...
     *  10.1.1.255-1024
     *  ....
     *  10.1.1.255-65535
     */
    private static List<String> parseAddr(String addr) {
        ArrayList<String> matchAddr = new ArrayList<String>();
        if (addr.endsWith(",")) {
            addr = addr.substring(0, addr.lastIndexOf(","));
        }
        String []addrList = addr.split(",");
        for (String address : addrList) {
            String []tmpAddr = address.split(":");

            if (tmpAddr.length != 2) {
                LOG.warn("address {} isn't vaild, ignore it", address);
                continue;
            }

            String ipBegin = null;
            String ipEnd = null;
            if (tmpAddr[0].endsWith("*")) {
                ipBegin = tmpAddr[0].substring(0, tmpAddr[0].lastIndexOf(".") + 1).concat("0");
                ipEnd = tmpAddr[0].substring(0, tmpAddr[0].lastIndexOf(".") + 1).concat("255");
            } else {
                String []ip = tmpAddr[0].split("-");
                if (ip.length == 1) {
                    ipBegin = ip[0];
                    ipEnd = ip[0];
                } else if (ip.length == 2) {
                    ipBegin = ip[0];
                    ipEnd = ipBegin.substring(0, ipBegin.lastIndexOf(".") + 1).concat(ip[1]);
                }
            }

            if (!checkIP(ipBegin, ipEnd)) {
                continue;
            }

            String portBegin = null;
            String portEnd = null;
            if (tmpAddr[1].equals("*")) {
                portBegin = "1024";
                portEnd = "65535";
            } else {
                String []port = tmpAddr[1].split("-");
                if (port.length == 1) {
                    portBegin = port[0];
                    portEnd = port[0];
                } else if (port.length == 2) {
                    portBegin = port[0];
                    portEnd = port[1];
                }
            }

            if (!checkPort(portBegin, portEnd)) {
                continue;
            }

            for (int intIp = toIPv4Address(ipBegin); intIp <= toIPv4Address(ipEnd); intIp++) {
                int intPortBegin = Integer.parseInt(portBegin);
                int intPortEnd = Integer.parseInt(portEnd);
                if (intPortBegin <= intPortEnd) {
                    for (int intPort = intPortBegin; intPort <= intPortEnd; intPort++) {
                        matchAddr.add(new StringBuilder(IPv4.fromIPv4Address(intIp)).append(ADDR_DELIMITER).append(intPort).toString());
                    }
                } else {
                    for (int intPort = intPortEnd; intPort <= intPortBegin; intPort++) {
                        matchAddr.add(new StringBuilder(IPv4.fromIPv4Address(intIp)).append(ADDR_DELIMITER).append(intPort).toString());
                    }
                }
            }
        }
        return matchAddr;
    }

    private static boolean checkIP(String ipBegin, String ipEnd) {
        try {
            if (toIPv4Address(ipBegin) > IPv4.toIPv4Address(ipEnd)) {
                LOG.warn("address {} {} isn't vaild, ignore it", ipBegin, ipEnd);
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    private static boolean checkPort(String portBegin, String portEnd) {
        int intPortBegin = Integer.parseInt(portBegin);
        int intPortEnd = Integer.parseInt(portEnd);
        if (intPortBegin <= 0 || intPortBegin > 65535) {
            return false;
        }
        if (intPortEnd <= 0 || intPortEnd > 65535) {
            return false;
        }
        return true;
    }

    /**
     * Accepts an IPv4 address of the form xxx.xxx.xxx.xxx, ie 192.168.0.1 and
     * returns the corresponding 32 bit integer.
     * @param ipAddress
     * @return
     */
    public static int toIPv4Address(String ipAddress) {
        if (ipAddress == null)
            throw new IllegalArgumentException("Specified IPv4 address must" +
                "contain 4 sets of numerical digits separated by periods");
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4)
            throw new IllegalArgumentException("Specified IPv4 address must" +
                "contain 4 sets of numerical digits separated by periods");

        int result = 0;
        for (int i = 0; i < 4; ++i) {
            int oct = Integer.valueOf(octets[i]);
            if (oct > 255 || oct < 0)
                throw new IllegalArgumentException("Octet values in specified" +
                        " IPv4 address must be 0 <= value <= 255");
            result |=  oct << ((3-i)*8);
        }
        return result;
    }

    /**
     * Accepts an IPv4 address and returns of string of the form xxx.xxx.xxx.xxx
     * ie 192.168.0.1
     *
     * @param ipAddress
     * @return
     */
    public static String fromIPv4Address(int ipAddress) {
        StringBuffer sb = new StringBuffer();
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (ipAddress >> ((3-i)*8)) & 0xff;
            sb.append(Integer.valueOf(result).toString());
            if (i != 3)
                sb.append(".");
        }
        return sb.toString();
    }
}
