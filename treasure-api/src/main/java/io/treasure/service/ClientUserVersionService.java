package io.treasure.service;

import io.treasure.enm.EClientUserVersion;

public interface ClientUserVersionService {
    boolean checkNormalOperation(String mobile, int processType);
}
