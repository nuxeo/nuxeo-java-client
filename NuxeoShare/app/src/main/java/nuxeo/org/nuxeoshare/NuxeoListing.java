package nuxeo.org.nuxeoshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.objects.Document;
import org.nuxeo.client.api.objects.Documents;
import org.nuxeo.client.api.objects.blob.Blob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NuxeoListing extends AppCompatActivity {

    private NuxeoClient nuxeoClient;
    private Document root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuxeo_listing);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        SharedPreferences settings = getSharedPreferences(NuxeoShare.PREFS_NAME, 0);
        String login = settings.getString("login", "Administrator");
        String pwd = settings.getString("pwd", "Administrator");
        String url = settings.getString("url", null);

        if(url==null){
            // send toast message
        }

        System.setProperty("log4j2.disable.jmx", "true");
        nuxeoClient = new NuxeoClient(url, login, pwd);

        root = nuxeoClient.repository().fetchDocumentRoot();
        //Documents children = root.fetchChildren();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String path = getPath(imageUri);
            File file = new File(path);
            Blob fileBlob = new Blob(file);
            fileBlob = nuxeoClient.automation().newRequest("Blob.AttachOnDocument").param("document", "/default-domain/UserWorkspaces/vpasquier/android").input(fileBlob).execute();
            if(fileBlob!=null){
                // send cool message
            }else{
                // send bad exception
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }
}
