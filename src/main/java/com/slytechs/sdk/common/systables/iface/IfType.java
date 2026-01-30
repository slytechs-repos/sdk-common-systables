package com.slytechs.sdk.common.systables.iface;
public enum IfType {
    PHYSICAL,   // Real hardware NIC
    VIRTUAL,    // Software interface (bridge, veth, etc)
    LOOPBACK    // Loopback interface
}