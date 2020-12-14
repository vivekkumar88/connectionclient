/*
 Created by V K on 12/13/20.
 */

package com.example.conectioncall.call.protocol.common.extras;

public class RemoteOperationConstants {

    public enum OperationName {

        RemoteDoorLock { public String toString() { return "doorLock"; }},
        RemoteDoorUnlock { public String toString() { return "doorUnlock"; }},
        RemoteLights { public String toString() { return "lights"; }},
        RemoteHorn { public String toString() { return "horn"; }},
        RemoteLocate { public String toString() { return "locate"; }},
        RemoteAC { public String toString() { return "remoteAC"; }},
        RemoteCustomize { public String toString() { return "customize"; }},
        RemoteSpeedAlert { public String toString() { return "speedAlert"; }},
        RemotePrivacyMode { public String toString() { return "privacyMode"; }},
        RemoteCurfew { public String toString() { return "curfew"; }},
        RemoteEngineOff { public String toString() { return "engineOff"; }},
        RemoteGeofence { public String toString() { return "geofence"; }},
        RemoteVHR { public String toString() { return "vehicleStatus"; }};

        public static OperationName getNameByCode(String code) {
            for (int i = 0; i < OperationName.values().length; i++) {
                if (code.equals(OperationName.values()[i].toString()))
                    return OperationName.values()[i];
            }
            return null;
        }
    }
}