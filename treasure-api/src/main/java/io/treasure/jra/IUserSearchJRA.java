package io.treasure.jra;

public interface IUserSearchJRA {
    void add(String userId,String value);

    Long removeAll(String userId);

    boolean isExistMember(String userId,String value);
    Long delItem(String userId,String value);
}
