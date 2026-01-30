package com.slytechs.sdk.common.systables.iface;

import com.slytechs.sdk.common.memory.MemorySize;
import com.slytechs.sdk.common.util.Count;

public record UnixIfStats(
    long rxBytes,
    long txBytes,
    long rxPackets,
    long txPackets,
    long rxErrors,
    long txErrors,
    long rxDropped,
    long txDropped
) implements IfStats {
    
    public MemorySize rxBytesSize() { return MemorySize.ofBytes(rxBytes); }
    public MemorySize txBytesSize() { return MemorySize.ofBytes(txBytes); }
    public Count rxPacketsCount() { return Count.ofCount(rxPackets); }
    public Count txPacketsCount() { return Count.ofCount(txPackets); }
}