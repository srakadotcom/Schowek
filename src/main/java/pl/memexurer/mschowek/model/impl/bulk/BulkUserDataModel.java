package pl.memexurer.mschowek.model.impl.bulk;

import pl.memexurer.mschowek.model.impl.StandardUserDataModel;
import pl.memexurer.mschowek.util.BinaryUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BulkUserDataModel extends StandardUserDataModel {
    private boolean markUpdated;

    public BulkUserDataModel(Map<Integer, Integer> itemCountMap) {
        super(itemCountMap);
    }

    public BulkUserDataModel() {
        super(new HashMap<>());
        this.markUpdated = true;
    }

    @Override
    public void setItemCount(int id, int i) {
        super.setItemCount(id, i);
        this.markUpdated = true;
    }

    public boolean isMarkUpdated() {
        return markUpdated;
    }

    public byte[] getSerializedValue() throws IOException {
        return BinaryUtils.write(super.itemCount);
    }
}
