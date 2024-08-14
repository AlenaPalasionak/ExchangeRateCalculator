package org.example.google_sheet_handler;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.example.Main;
import org.example.util.logger.Log;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SheetsStart {
    private static final String APPLICATION_NAME = "Google Sheets API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final NetHttpTransport HTTP_TRANSPORT;
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    static {
        try {
            Log.info("(AbstractDataTransferor) 1.  HTTP_TRANSPORT is gonna be initialised");
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            Log.info("(AbstractDataTransferor)  2. HTTP_TRANSPORT was gonna be initialised but new RuntimeException was thrown "
                    + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Sheets getService() {
        try {
            return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Credential getCredentials() throws IOException {
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            Log.info("(AbstractDataTransferor) 6. Resource not found: " + CREDENTIALS_FILE_PATH);
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
