package ika.games.base.controller;

import com.simplejcode.commons.misc.struct.DynamicStruct;
import com.simplejcode.commons.misc.util.CryptoUtils;

public class PersistentObject extends DynamicStruct {

    public final int id;
    public final String name;
    private final byte[] hexName;

    public PersistentObject(DynamicStruct struct) {
        super(struct);
        id = struct.getInt(Constants.ID);
        name = struct.getString(Constants.NAME);
        hexName = CryptoUtils.toHex(name).getBytes();
    }

    public byte[] getHexName() {
        return hexName;
    }

}
