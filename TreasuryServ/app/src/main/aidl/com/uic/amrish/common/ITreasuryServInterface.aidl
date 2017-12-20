// ITreasuryServInterface.aidl
package com.uic.amrish.common;

// Declare any non-default types here with import statements

interface ITreasuryServInterface {

    List<String> monthlyCash(int year);

    List<String> dailyCash(int day, int month, int year, int workingDays);

    double yearlyCash(int year);
}
