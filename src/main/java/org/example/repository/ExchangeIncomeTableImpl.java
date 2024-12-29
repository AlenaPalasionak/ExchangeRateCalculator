package org.example.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.example.config.Config;
import org.example.model.ExchangeRate;
import org.example.model.Payment;
import org.example.model.Transaction;
import org.example.model.non_operating_income.AbstractExchangeIncome;
import org.example.service.RusRubExchangeIncomeService;
import org.example.util.GoogleSheetsConnector;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static jdk.jfr.internal.consumer.EventLog.update;

public class ExchangeIncomeTableImpl implements ExchangeIncomeTable {
    public static final String RATE_DIFFERENCE_SPREADSHEET_ID_KEY = "rate_difference_spreadsheet";
    public final String RATE_DIFFERENCE_SPREADSHEET_ID = Config.getProperties().getProperty(RATE_DIFFERENCE_SPREADSHEET_ID_KEY);
    private static final String RANGE = "Sheet1!A1";
    private static final List<Transaction> transactions = new RusRubExchangeIncomeService().getTransactions();

    public void writeData(List<Transaction> transactions) {
        try {
            Sheets sheetsService = GoogleSheetsConnector.connectToSheets();
            ValueRange body = new ValueRange()
                    .setValues(List.of(
                            List.of(fillData(transactions));

            sheetsService.spreadsheets().values().
                    update(RATE_DIFFERENCE_SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fillData (Transaction transaction) {
        String actDate;
        BigDecimal receivableAmount;
        ExchangeRate actDateExchangeRate;


        BigDecimal payableAmount;
        List<Payment> incomingPaymentList;
        List<Payment> outgoingPaymentList;
        boolean accountBalance;

        BigDecimal commission;
        String actNumber;

        AbstractExchangeIncome completionCertificateVSPaymentExchangeIncome;
        AbstractExchangeIncome commissionExchangeIncome;
        AbstractExchangeIncome receivedVSPaidExchangeIncome;
        AbstractExchangeIncome accountExchangeIncome;
    }
}
