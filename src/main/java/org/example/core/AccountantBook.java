package org.example.core;

import org.example.util.google_sheet_handler.GoogleSheetHandler;

import java.util.List;

import static org.example.core.constants.AccountantBookConstant.INCOMING_PAYMENT_SUM_INDEX;
import static org.example.util.google_sheet_handler.constants.KeyWordConstant.RUS_RUB;
import static org.example.util.google_sheet_handler.constants.SheetRangeConstant.SHEET_1_B_I;

public abstract class AccountantBook {
    public static List<List<Object>> getListsWithCellContent() {
        return GoogleSheetHandler.getListsWithCellContent(SHEET_1_B_I, INCOMING_PAYMENT_SUM_INDEX, RUS_RUB);
    }
}
