package com.example.android.disdetector;

import android.util.Log;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by globe_000 on 9/27/2017.
 */

public class GMailReader extends javax.mail.Authenticator {

    private static final String TAG = "GMailReader";

    private String mailhost = "imap.gmail.com";
    private Session session;
    private Store store;

    public GMailReader(String user, String password) {
        Properties props = System.getProperties();
        if(props == null){
            Log.e(TAG, "Null properties!");
        } else {
            props.setProperty("mail.store.protocol","imaps");
        }
        try {
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect(mailhost,user,password);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

//    public void getContent(Message msg){
//        try{
//            String contentType = msg.getContentType();
//        }
//    }

    public synchronized  String readMail() throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_ONLY);
           // Log.d("readMail", "made it");
            Message [] msgs= folder.getMessages();
            Message msg = msgs[msgs.length - 1];
            Multipart mp = (Multipart) msg.getContent();
            BodyPart bp = mp.getBodyPart(0);
            Log.d("readMail", bp.getContent().toString());
            return bp.getContent().toString();
        } catch (Exception e ){
            Log.e("readMail", e.getMessage(), e);
            return null;
        }
    }

}
