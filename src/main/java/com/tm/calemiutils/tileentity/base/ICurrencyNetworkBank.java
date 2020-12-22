package com.tm.calemiutils.tileentity.base;

public interface ICurrencyNetworkBank extends INetwork {

    int getStoredCurrency ();

    int getMaxCurrency ();

    void setCurrency (int setAmount);

    boolean canDeposit(int depositAmount);

    boolean canWithdraw(int withdrawAmount);

    void depositCurrency (int depositAmount);

    void withdrawCurrency (int withdrawAmount);
}
